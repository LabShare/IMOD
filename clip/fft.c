/*
 *  fft.c -- calculate fft for clip, also used by filter & correlation
 *
 *  Original author: James Kremer
 *  Revised by: David Mastronarde   email: mast@colorado.edu
 *
 *  Copyright (C) 1995-2005 by Boulder Laboratory for 3-Dimensional Electron
 *  Microscopy of Cells ("BL3DEMC") and the Regents of the University of 
 *  Colorado.  See dist/COPYRIGHT for full copyright notice.
 */
/*  $Author$

$Date$

$Revision$
Log at end of file
*/

#include <stdlib.h>
#include <string.h>
#include "mrcc.h"
#include "clip.h"
#include "cfft.h"

static int clip_nicesize(int size);
static int mrcODFFT(float *buf, int nx, int ny, int idir);
static int clip_wrapfile(MrcHeader *hdata);
static int clip_3dfft_swap(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt);
static int clip_fftfile3(MrcHeader *hdata, int idir);
static int clip_wrapvol(Istack *v);
static int clip_3difft_swap(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt);
static int clip_3dffft_swap(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt);

/*
 * The entry point from clip for all fft operations, does 2D FFTs
 */
int clip_fft(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt)
{
  Islice *slice;
  int k, z = 0;

  /* Common tests for parameters */
  if ((hin->mode == MRC_MODE_COMPLEX_FLOAT) ||
      (hin->mode == MRC_MODE_COMPLEX_SHORT)) {

    /* For inverse FFT, it must have full input size;
       default mode is float but others are OK; default X 
       size depends if it is old mirrored size (even) or non-mirrored (odd). */
    if (opt->ix != IP_DEFAULT || opt->iy != IP_DEFAULT || 
        opt->cx != IP_DEFAULT || opt->cy != IP_DEFAULT)
      show_warning("clip inverse fft - input sizes or centers are ignored");
    opt->ix = opt->iy = opt->cx = opt->cy = IP_DEFAULT; 
    if (opt->mode == IP_DEFAULT)
      opt->mode = MRC_MODE_FLOAT;
  } else {

    /* Forward fft must not resize */
    if (opt->ox != IP_DEFAULT || opt->oy != IP_DEFAULT || 
        opt->oz != IP_DEFAULT || opt->mode != IP_DEFAULT)
      show_warning("clip forward fft - output sizes or mode are ignored");
    opt->ox = opt->oy = opt->oz = IP_DEFAULT;
    opt->mode = MRC_MODE_COMPLEX_FLOAT;
    opt->ocanresize = FALSE;
  }

  if (opt->dim == 3)
    return(clip_3dfft(hin, hout, opt));

  /* For inverse FFT,  default X 
     size depends if it is old mirrored size (even) or non-mirrored (odd). */
  if ((hin->mode == MRC_MODE_COMPLEX_FLOAT) ||
      (hin->mode == MRC_MODE_COMPLEX_SHORT)) {
    if (opt->ox == IP_DEFAULT)
      opt->ox = (hin->nx % 2) ? 2 * (hin->nx - 1) : hin->nx;
  }

  set_input_options(opt, hin);

  if ((hin->mode != MRC_MODE_COMPLEX_FLOAT) &&
      (hin->mode != MRC_MODE_COMPLEX_SHORT)) {
    if ((!clip_nicesize(opt->ix)) ||
        (!clip_nicesize(opt->iy)) || opt->ix % 2){
      printf("ERROR: clip - fft input size (%d, %d) is odd and/or has factors"
             " greater than 19.\n", opt->ix, opt->iy);
      return(-1);
    }
    opt->ox = opt->ix / 2 + 1;
    opt->oy = opt->iy;
    opt->oz = opt->nofsecs;
    if (opt->add2file && (hout->mode != opt->mode || hout->nx != opt->ox ||
                          hout->ny != opt->oy)) {
      show_error("clip forward 2d fft - cannot append to output file of "
                 "different size or mode");
      return -1;
    }
  }

  z = set_output_options(opt, hout);
  if (z < 0)
    return(z);

  mrc_head_label_cp(hin, hout);
  mrc_head_label(hout, "clip: 2d fft");
  show_status("Doing fast fourier transform...\n");     

  for (k = 0; k < opt->nofsecs; k++) {
    slice = sliceReadSubm(hin, opt->secs[k], 'z', opt->ix, opt->iy,
                          (int)opt->cx, (int)opt->cy);
    if (!slice){
      show_error("fft: Error reading slice.");
      return(-1);
    }
    slice_fft(slice);
    if (clipWriteSlice(slice, hout, opt, k, &z, 1))
      return -1;
  }
  return set_mrc_coords(opt);  
}

/*
 * Takes the FFT of one slice 
 */ 
int slice_fft(Islice *slice)
{
  float *tbuf;
  int nx2  = slice->xsize + 2;
  int ncs  = (int)slice->xsize * sizeof(float);
  int i, j, newXsize;
  int idir = 1;

  if (slice->mode == MRC_MODE_COMPLEX_FLOAT || 
      slice->mode == MRC_MODE_COMPLEX_SHORT)
    idir = -1;
  
  if (idir == 1){

    /* forward fft.  Convert to float if needed, then pack data into
       wider buffer for FFT and take fft */

    sliceFloat(slice); 
    tbuf = (float *)malloc(sizeof(float) * nx2 * slice->ysize);
    for(i = 0; i < slice->ysize; i++)
      memcpy(&(tbuf[i * nx2]), 
             &(slice->data.f[i * slice->xsize]), 
             ncs);
    free(slice->data.f);
    mrcToDFFT(tbuf, slice->xsize, slice->ysize, 0);

    /* Set slice to new properties and wrap the lines */
    sliceInit(slice, slice->xsize / 2 + 1, slice->ysize, 
              MRC_MODE_COMPLEX_FLOAT,
              tbuf);
    sliceWrapFFTLines(slice);

#ifdef OLD_STUFF
    /* The old set of operations, painfully reproduced with mirrored image
       load in 3dmod */
    /* Move top half of FFT to lower right of new array, and store the
       extra pixel at the left edge of each row */
    for(j = 0, i = y2; i < slice->ysize; i++, j++){
      memcpy(&(buf[(2 * j * slice->xsize) + slice->xsize]),
             &(tbuf[i * nx2]), 
             nxs);
      memcpy(&(buf[(2 * j * slice->xsize)]),
             &(tbuf[(i * nx2)+slice->xsize]),sizeof(float)*2);
    }
    /* Now start at the FFT origin and move bottom half of FFT into the
       upper right of new array */
    for(i = 0; i < y2; i++, j++){
      memcpy(&(buf[(2 * j * slice->xsize) + slice->xsize]),
             &(tbuf[i * nx2]),
             nxs);
      memcpy(&(buf[(2 * j * slice->xsize)]),
             &(tbuf[(i * nx2)+slice->xsize]),sizeof(float)*2);
           
    }

    /* Copy each row after the first point to the mirror row in Y, in
       inverse order.  But it has to shift by one, so copy the bottom row */
    for(j = 1 ; j < slice->ysize; j++)
      for(k = slice->xsize - 2,i = 2; i < slice->xsize; i++,k-=2){
        buf[i+(slice->xsize*2*j)] = 
          buf[slice->xsize+k + (slice->xsize*2*(slice->ysize - j))];
        buf[++i + (slice->xsize*2*j)] = 
          buf[slice->xsize+ k+1 + (slice->xsize*2*(slice->ysize - j))];
      }
    memcpy(&(buf[2]), &(buf[2 + 2 * slice->xsize]), 
             (slice->xsize - 2) * sizeof(float));
    free(tbuf);
#endif

  }else{

    /* Inverse FFT: first convert to float */
    sliceComplexFloat(slice); 

    /* If even length, it is old-style mirrored; reduce it */
    if (!(slice->xsize % 2))
      sliceReduceMirroredFFT(slice);

    /* Unwrap the lines in any case then take the FFT */
    sliceWrapFFTLines(slice);
    /*  fprintf(stderr, "unwrapped\n");
    for (j = 0; j < slice->ysize; j++) {
      for (i = 0; i < 2 * slice->xsize; i++)
        fprintf(stderr, "%8.2f ", slice->data.f[i + j * 2 * slice->xsize]);
      fprintf(stderr, "\n");
      } */
    newXsize = 2 * (slice->xsize - 1);
    mrcToDFFT(slice->data.f, newXsize, slice->ysize, 1);
    
    /* Repack data to eliminate extra elements */
    for (j = 0; j < slice->ysize; j++)
      for (i = 0; i < newXsize; i++)
        slice->data.f[i + j * newXsize] =
          slice->data.f[i + j * (newXsize + 2)];

    slice->xsize = newXsize;
    slice->mode = 2;
  }
  return(0);
}

/*
 * Entry point to process options and do I/O for 3D fft
 */
int clip_3dfft(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt)
{
  Istack *v;
  int k;

  if (hin->mode != MRC_MODE_COMPLEX_FLOAT){
    if (opt->ix == IP_DEFAULT) opt->ix = hin->nx;
    if (opt->iy == IP_DEFAULT) opt->iy = hin->ny;
    if (opt->iz == IP_DEFAULT) opt->iz = hin->nz;
      
    if ((!clip_nicesize(opt->ix)) ||
        (!clip_nicesize(opt->iy)) ||
        (!clip_nicesize(opt->iz)) || opt->ix % 2){
      printf("ERROR: clip - fft input size %dx%dx%d is odd and/or has "
             "factors greater than 19.\n", opt->ix, opt->iy, opt->iz);
      return(-1);
    }
  }
      
  if (opt->sano){
    return(clip_3dfft_swap(hin, hout, opt));
  }
      
  v = grap_volume_read(hin, opt);
  if (!v){
    return(-1);
  }

  if (hin->mode != MRC_MODE_COMPLEX_FLOAT)
    for (k = 0; k < v->zsize; k++){
      if (hin->mode == MRC_MODE_COMPLEX_SHORT)
        sliceComplexFloat(v->vol[k]);
      else
        sliceFloat(v->vol[k]);
    }

  show_status("Doing 3d fast fourier transform in core...\n");
  clip_fftvol(v);

  mrc_head_new(hout, v->vol[0]->xsize, v->vol[0]->ysize, 
               v->zsize, v->vol[0]->mode);

  /* DNM 7/31/02: copy labels over, make label specify direction */
  mrc_head_label_cp(hin, hout);
  if (hin->mode != MRC_MODE_COMPLEX_FLOAT)
    mrc_head_label(hout, "Clip: Forward 3D FFT");
  else
    mrc_head_label(hout, "Clip: Inverse 3D FFT");

  if (grap_volume_write(v, hout, opt))
    return(-1);

  /* DNM 7/31/02: Copy cell and sample sizes over just like fftrans
     so that inverse transform will retain pixel spacing */
  hout->mx = hin->mx;
  hout->my = hin->my;
  hout->mz = hin->mz;
  hout->xlen = hin->xlen;
  hout->ylen = hin->ylen;
  hout->zlen = hin->zlen;
  if (mrc_head_write(hout->fp, hout))
    return -1;

  grap_volume_free(v);
  return(0);
}

/* 
 * Do 3D FFT in memory on the given volume, must be float or complex float
 */
int clip_fftvol(Istack *v)
{
  Islice *slice;
  int z, i;
  int ncs    = (int)v->vol[0]->xsize * sizeof(float);
  int nx2    = v->vol[0]->xsize + 2;
  float *tbuf;

  if (v->vol[0]->mode == SLICE_MODE_COMPLEX_FLOAT){
    clip_wrapvol(v); 
    clip_fftvol3(v, -2);  

    for(z = 0; z < v->zsize; z++){
      /* must use -1 for idir or else phases get messed up */
      mrcToDFFT(v->vol[z]->data.f, (v->vol[z]->xsize - 1)*2,
                v->vol[z]->ysize, -1);
      v->vol[z]->mode = SLICE_MODE_FLOAT;
      v->vol[z]->xsize *= 2;
      sliceBoxIn(v->vol[z], 0, 0,
                 v->vol[z]->xsize - 2, v->vol[z]->ysize);

    }
  }else{
    /* forward fft */
    for(z = 0; z < v->zsize; z++){
      slice = v->vol[z];
      sliceFloat(slice);
      tbuf = (float *)malloc(sizeof(float) * nx2 * slice->ysize);
      for(i = 0; i < slice->ysize; i++)
        memcpy(&(tbuf[i * nx2]),
               &(slice->data.f[i * slice->xsize]),
               ncs);
      free(slice->data.f);
      slice->data.f = tbuf;
      slice->xsize += 2;
      mrcToDFFT(v->vol[z]->data.f, v->vol[z]->xsize - 2,
                v->vol[z]->ysize, 0);
      slice->xsize /= 2;
      slice->mode = SLICE_MODE_COMPLEX_FLOAT;

    }
    clip_fftvol3(v, -1);  
    clip_wrapvol(v);

  }
  return(0);
}

/* 
 * do a complex<->complex fft in the 3rd dimension only 
 */
int clip_fftvol3(Istack *v, int idir)
{
  int x, y, z, i, j;
  Islice *slice;
  Ival val;
  float *inp, *outp;
  int  vxsize = v->vol[0]->xsize;
  int  vysize = v->vol[0]->ysize;

  slice = sliceCreate(v->zsize, vxsize,
                      SLICE_MODE_COMPLEX_FLOAT);
  for(y = 0; y < vysize; y++){

    for (z = 0; z < slice->xsize; z++) {
      inp = v->vol[z]->data.f + 2 * y * vxsize;
      outp = slice->data.f + 2 * z;
      for (x = 0; x < slice->ysize; x++) {
        outp[2 * x * slice->xsize] = inp[2 * x];
        outp[2 * x * slice->xsize + 1] = inp[2 * x + 1];
        //sliceGetComplexFloatVal(v->vol[z], x, y, val);
        //slicePutComplexFloatVal(slice, z, x, val);
      }
    }

    mrcODFFT(slice->data.f, slice->xsize, slice->ysize, idir);

    /* put slice back into volume. */
    for (z = 0; z < slice->xsize; z++) {
      outp = v->vol[z]->data.f + 2 * y * vxsize;
      inp = slice->data.f + 2 * z;
      for (x = 0; x < slice->ysize; x++) {
        outp[2 * x] = inp[2 * x * slice->xsize];
        outp[2 * x + 1] = inp[2 * x * slice->xsize + 1];
        //sliceGetComplexFloatVal(slice, z, x, val);
        //slicePutComplexFloatVal(v->vol[z], x, y, val);
      }
    }
  }
  sliceFree(slice);
  return(0);
}

/*
 * Wrap the volume so FFT origin moves from origin to middle in Y/Z or back
 */
int clip_wrapvol(Istack *v)
{
  Ival val, tval;
  Islice *tsl;
  int i, j,j2;
  int k,k2;
  int nx = v->vol[0]->xsize;
  int ny = v->vol[0]->ysize;
  int hny = ny / 2;
  int hnz = v->zsize / 2;

  for(k = 0; k < v->zsize; k++){
    for(i = 0; i < nx; i++){
      for(j = 0, j2 = hny; j < hny; j++,j2++){
        sliceGetVal(v->vol[k], i, j, val);
        sliceGetVal(v->vol[k], i, j2, tval);
        slicePutVal(v->vol[k], i, j, tval);
        slicePutVal(v->vol[k], i, j2, val);
      }
    }
  }
  for(k = 0, k2 = hnz; k < hnz; k++, k2++){
    tsl = v->vol[k];
    v->vol[k] = v->vol[k2];
    v->vol[k2] = tsl;
  }
  return(0);
}

/*
 * Entry for 3D fft with temporary file for 3rd dimension 
 */
int clip_3dfft_swap(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt)
{
  show_status("Doing 3d fast fourier transform...\n");
  if (hin->mode == SLICE_MODE_COMPLEX_FLOAT)
    return(clip_3difft_swap(hin, hout, opt));
  return(clip_3dffft_swap(hin, hout, opt));
}

/*
 * Does inverse 3D fft with temporary file for 3rd dimension 
 */
int clip_3difft_swap(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt)
{
  int k;
  int nz = hin->nz;
  Islice  *s = sliceCreate
    (hin->nx * 2, hin->ny, SLICE_MODE_FLOAT);
  Islice  *sout;

  /* BIG WARNING: input file gets mangled. */
  show_status("Mangeling.\n");
  fclose(hin->fp);
  hin->fp = fopen(opt->fnames[0], "rb+");
  clip_wrapfile(hin);
  show_status("Inverse in 3rd dim.\n");
  clip_fftfile3(hin, -2);


  if (opt->mode == IP_DEFAULT) opt->mode = SLICE_MODE_FLOAT;

  mrc_head_new(hout,  s->xsize - 2, hin->ny, hin->nz, opt->mode);
  mrc_head_label(hout, "clip: 3D Inverse FFT");
  if (mrc_head_write(hout->fp, hout))
    return -1;
  show_status("Inverse 2nd and 1st dim.\n");

  for(k = 0; k < nz; k++){
    if (mrc_read_slice(s->data.f, hin->fp, hin, k, 'z'))
      return -1;
    mrcToDFFT(s->data.f, (hin->nx - 1) * 2, hin->ny, -1);
    sout = sliceBox(s, 0, 0, s->xsize - 2, s->ysize);
    sliceNewMode(sout, opt->mode);
    if (mrc_write_slice(sout->data.b, hout->fp, hout, k, 'z'))
      return -1;
    sliceFree(sout);
  }
  sliceFree(s);

  s = sliceCreate(hout->nx, hout->ny, hout->mode);
  if (mrc_read_slice((void *)s->data.b, hout->fp, hout, 0, 'z'))
    return -1;
  sliceMMM(s);
  hout->amin = s->min;
  hout->amax = s->max;
  hout->amean = s->mean;
  for(k = 1; k < hout->nz; k++){
    if (mrc_read_slice((void *)s->data.b, hout->fp, hout, k, 'z'))
      return -1;
    sliceMMM(s);
    hout->amean += s->mean;
    if (s->min < hout->amin)
      hout->amin = s->min;
    if (s->max > hout->amax)
      hout->amax = s->max;
  }
  hout->amean /= hout->nz;
  if (mrc_head_write(hout->fp, hout))
    return -1;
  sliceFree(s);

  return(0);
}

/*
 * Does forward 3D fft with temporary file for 3rd dimension 
 */
int clip_3dffft_swap(MrcHeader *hin, MrcHeader *hout, ClipOptions *opt)
{
  int k, z;
  Islice  *s;
  Islice  *ts;
  int llx, lly, urx, ury;

  llx = opt->cx - ((float)opt->ix * 0.5f);
  urx = llx + opt->ix;
  lly = opt->cy - ((float)opt->iy * 0.5f);
  ury = lly + opt->iy;

  ts = sliceCreate(hin->nx, hin->ny, hin->mode);
  if (!ts)
    return(-1);

  k = (opt->cz - ((float)opt->iz * 0.5f));
  mrc_head_new(hout,  (opt->ix + 2)/2, opt->iy, opt->iz, 
               SLICE_MODE_COMPLEX_FLOAT);
  mrc_head_label(hout, "clip: 3D FFT");     
  if (mrc_head_write(hout->fp, hout))
    return -1;
  ts->mean = opt->pad;

  for(z = 0; z < opt->iz; k++, z++){
    if (k >= 0){
      if (mrc_read_slice((void *)ts->data.b, hin->fp, hin, k, 'z'))
        return -1;
      s = sliceBox(ts, llx, lly, urx + 2, ury);
      sliceFloat(s);
    }else{
      s = sliceCreate(opt->ix + 2, opt->iy, SLICE_MODE_FLOAT);
      sliceClear(s, &(s->mean));
    }
    mrcToDFFT(s->data.f, s->xsize - 2, s->ysize, 0);
    s->xsize = (s->xsize+2)/2;
    s->mode = SLICE_MODE_COMPLEX_FLOAT;
    if (mrc_write_slice((void *)s->data.b, hout->fp, hout, z, 'z'))
      return -1;
    sliceFree(s);
  }

  sliceFree(ts);

  show_status("Transforming 3rd dim.\n");
  clip_fftfile3(hout, -1); 

  show_status("Demangeling.\n");
  clip_wrapfile(hout);

  s = mrc_slice_create(hout->nx, hout->ny, hout->mode);
  if (mrc_read_slice((void *)s->data.b, hout->fp, hout, 0, 'z'))
    return -1;
  sliceMMM(s);
  hout->amin = s->min;
  hout->amax = s->max;
  hout->amean = s->mean;
  for(z = 1; z < hout->nz; z++){
    if (mrc_read_slice((void *)s->data.b, hout->fp, hout, z, 'z'))
      return -1;
    sliceMMM(s);
    hout->amean += s->mean;
    if (s->min < hout->amin)
      hout->amin = s->min;
    if (s->max > hout->amax)
      hout->amax = s->max;
  }
  hout->amean /= hout->nz;
  if (mrc_head_write(hout->fp, hout))
    return -1;
  sliceFree(s);
  return(0);
}

/*
 * Does a complex<->complex FFT in 3rd dimension (Y-slices) of a data file 
 */
int clip_fftfile3(MrcHeader *hdata, int idir)
{
  unsigned int i, j;
  unsigned int nx = hdata->nx;
  unsigned int ny = hdata->nz;
  Islice *slice, *fs;
  Ival val;
  int  y;
  int  vysize = hdata->ny;
     
  if (hdata->mode != SLICE_MODE_COMPLEX_FLOAT)
    return(-1);

  slice = sliceCreate(hdata->nx, hdata->nz,
                      SLICE_MODE_COMPLEX_FLOAT);
  fs    = sliceCreate(hdata->nz, hdata->nx,
                      SLICE_MODE_COMPLEX_FLOAT);

  for(y = 0; y < vysize; y++){
    if (mrc_read_slice(slice->data.f, hdata->fp, hdata, y, 'y'))
      return -1;
      
    for(j = 0; j < ny; j++)
      for(i = 0; i < nx; i++){
        sliceGetComplexFloatVal(slice, i, j, val);
        slicePutComplexFloatVal(fs,    j, i, val);
      }
      
    mrcODFFT(fs->data.f, fs->xsize, fs->ysize, idir);
      
    for(j = 0; j < ny; j++)
      for(i = 0; i < nx; i++){
        sliceGetComplexFloatVal(fs,    j, i, val);
        slicePutComplexFloatVal(slice, i, j, val);
      }
      
    if (mrc_write_slice(slice->data.f, hdata->fp, hdata, y, 'y'))
      return (-1);
  }
  sliceFree(slice);
  return(0);
}

/*
 * Rearrange file to move FFT origin from origin to middle in Y/Z or back
 */
int clip_wrapfile(MrcHeader *hdata)
{
  Ival val, tval;
  Islice *s1, *s2;
  int i, j,j2;
  int k,k2;
  int nx = hdata->nx;
  int ny = hdata->ny;
  int hny = ny / 2;
  int hnz = hdata->nz / 2;
     
  s1 = sliceCreate(hdata->nx, hdata->ny, hdata->mode);
  s2 = sliceCreate(hdata->nx, hdata->ny, hdata->mode);

  for(k = 0, k2 = hnz; k < hnz; k++, k2++){
    if (mrc_read_slice(s1->data.f, hdata->fp, hdata, k, 'z'))
      return -1;
    if (mrc_read_slice(s2->data.f, hdata->fp, hdata, k2, 'z'))
      return -1;
      
    for(i = 0; i < nx; i++){
      for(j = 0, j2 = hny; j < hny; j++,j2++){
        sliceGetVal(s1, i, j,  val);
        sliceGetVal(s1, i, j2, tval);
        slicePutVal(s1, i, j,  tval);
        slicePutVal(s1, i, j2, val);
        sliceGetVal(s2, i, j,  val);
        sliceGetVal(s2, i, j2, tval);
        slicePutVal(s2, i, j,  tval);
        slicePutVal(s2, i, j2, val);
      }
    }

    if (mrc_write_slice(s1->data.f, hdata->fp, hdata, k2, 'z') ||
        mrc_write_slice(s2->data.f, hdata->fp, hdata, k, 'z'))
      return -1;
  }
  sliceFree(s1);
  sliceFree(s2);
  return(0);
}

/*******************************************************************/
/* c-stub for formerly fortran SUBROUTINE TODFFT(ARRAY,NX,NY,IDIR) */
/* (now a C-routine with fortran calling convention)               */
/* buf is (nx + 2)/2 by ny floats                                  */
/* idir 0 = forward, 1 = inverse, -1 = inverse w/o conjugate       */

/*
 * ODFFT do a series of 1D FFT's on 2D data.
 * idir -1 forward, -2 reverse.
 */

void mrcToDFFT(float buf[], int nx, int ny, int idir)
{
  todfft(buf, &nx, &ny, &idir); 
}


int mrcODFFT(float *buf, int nx, int ny, int idir)
{
  odfft(buf, &nx, &ny, &idir);
  return(0);
}

int clip_nicesize(int size)
{
  int i;

  for(i = 2; i < 20; i++)
    while( !(size%i))
      size = size / i;

  if (size > 1)
    return(0);
  else
    return(1);

  /*
    int nsize = niceframe_(&size, &inc, &fac);
    if (nsize == size)
    return(1);
    else
    return(0);
  */
}


/*
$Log$
Revision 3.6  2004/11/04 17:04:21  mast
Switched to producing and using non-mirrored FFTs

Revision 3.5  2004/10/24 21:43:37  mast
Modified to use header from C version of FFT library, changed orientation
of planes that are done in 3rd dimension of 3D FFT, and rewrote to
compose planes as fast as possible.

Revision 3.4  2004/06/20 21:58:05  mast
Fixed one-pixel shift between mirrored rows and actual rows

Revision 3.3  2004/04/22 19:08:45  mast
Added error checks and returns on mrc I/O calls

Revision 3.2  2003/10/24 03:09:45  mast
open files as binary

Revision 3.1  2002/08/01 00:00:16  mast
Preserve pixel size and lables when doing 3D FFT

*/
