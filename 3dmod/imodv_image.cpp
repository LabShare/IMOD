/*
 *  imodv_image.c -- View image slice in model view control dialog.
 *
 *  Original author: James Kremer
 *  Revised by: David Mastronarde   email: mast@colorado.edu
 *
 *  Copyright (C) 1995-2006 by Boulder Laboratory for 3-Dimensional Electron
 *  Microscopy of Cells ("BL3DEMC") and the Regents of the University of 
 *  Colorado.  See dist/COPYRIGHT for full copyright notice.
 *
 *  $Id$
 */

#include <qcheckbox.h>
#include <qlayout.h>
#include <qgl.h>
#include <qslider.h>
#include <qtooltip.h>
//Added by qt3to4:
#include <QKeyEvent>
#include <QCloseEvent>
#include <QTime>
#include "preferences.h"
#include "multislider.h"
#include "dia_qtutils.h"
#include "imodv.h"
#include "imod.h"
#include "imodv_gfx.h"
#include "imodv_stereo.h"
#include "imodv_image.h"
#include "imodv_input.h"
#include "imod_display.h"
#include "control.h"
#include "preferences.h"
#include "xcramp.h"


#define MAX_SLICES 1024

enum {IIS_X_COORD = 0, IIS_Y_COORD, IIS_Z_COORD, IIS_X_SIZE, IIS_Y_SIZE,
      IIS_Z_SIZE, IIS_SLICES, IIS_TRANSPARENCY, IIS_BLACK, IIS_WHITE};

static void mkcmap(void);
static void imodvDrawTImage(Ipoint *p1, Ipoint *p2, Ipoint *p3, Ipoint *p4,
                            Ipoint *clamp, unsigned char *data,
                            int width, int height);
static void setAlpha(int iz, int zst, int znd, int izdir);
static void setSliceLimits(int ciz, int miz, bool invertZ, int drawTrans, 
                           int &zst, int &znd, int &izdir);
static void setCoordLimits(int cur, int maxSize, int drawSize, 
                           int &str, int &end);
static void endTexMapping();

// 3/23/11: NEW NAMING CONVENTION, sVariable for all static variables

static GLubyte *sTdata = NULL;
static int sTexImageSize = 0;
static GLubyte sCmap[3][256];
static int sBlackLevel = 0;
static int sWhiteLevel = 255;
static int sFalsecolor = 0;
static int sImageTrans = 0;
static int sCmapInit = 0;
static int cmapZ = 0;
static int cmapTime = 0;
static int sNumSlices = 1;
static int sXdrawSize = -1;
static int sYdrawSize = -1;
static int sZdrawSize = -1;
static int sLastYsize = -1;
ImodvImage *sDia = NULL;
ImodvApp  *sA = NULL;
int    sFlags = 0;
  
  /* DNM 12/30/02: unused currently */
  /* int    xsize, ysize, zsize;
     int    *xd, *yd, *zd; */

static double sWallLoad, sWallDraw;

// Open, close, or raise the dialog box
void imodvImageEditDialog(ImodvApp *a, int state)
{
  if (!state){
    if (sDia)
      sDia->close();
    return;
  }
  if (sDia) {
    sDia->raise();
    return;
  }

  sDia = new ImodvImage(imodvDialogManager.parent(IMODV_DIALOG),
                                      "image view");
  sA = a;

  mkcmap();
  imodvDialogManager.add((QWidget *)sDia, IMODV_DIALOG);
  adjustGeometryAndShow((QWidget *)sDia, IMODV_DIALOG);
  imodvImageUpdate(a);
}

// Update the dialog box (just the view flag for now)
void imodvImageUpdate(ImodvApp *a)
{
  if (a->texMap && !sFlags) {
    sFlags |= IMODV_DRAW_CZ;
    if (sDia)
      diaSetChecked(sDia->mViewZBox, true);
  } else if (!a->texMap && sFlags) {
    sFlags = 0;
    if (sDia) {
      diaSetChecked(sDia->mViewXBox, false);
      diaSetChecked(sDia->mViewYBox, false);
      diaSetChecked(sDia->mViewZBox, false);
    }
  }
  if (sDia) {
    sDia->mViewXBox->setEnabled(!(a->stereo && a->imageStereo));
    sDia->mViewYBox->setEnabled(!(a->stereo && a->imageStereo));
    sDia->mSliders->setEnabled(IIS_SLICES, 
                                             !(a->stereo && a->imageStereo));
  }
}

// Set the number of slices and the transparency from movie controller - 
// do not update the image
void imodvImageSetThickTrans(int slices, int trans)
{
 int maxSlices = Imodv->vi->zsize < MAX_SLICES ?
    Imodv->vi->zsize : MAX_SLICES;
  if (slices < 1)
    slices = 1;
  if (slices > maxSlices)
    slices = maxSlices;
  sNumSlices = slices;
  if (trans < 0)
    trans = 0;
  if (trans > 100)
    trans = 100;
  sImageTrans = trans;
  if (sDia) {
    sDia->mSliders->setValue(IIS_SLICES, sNumSlices);
    sDia->mSliders->setValue(IIS_TRANSPARENCY, sImageTrans);
  }
}

// Return the number of slices and transparancy
int imodvImageGetThickness(void)
{
  return sNumSlices;
}
int imodvImageGetTransparency(void)
{
  return sImageTrans;
}

/****************************************************************************/
/* TEXTURE MAP IMAGE. */
/****************************************************************************/

// Make a color map
static void mkcmap(void)
{
  int rampsize, cmapReverse = 0;
  float slope, point;
  int r,g,b,i;
  ImodvApp *a = Imodv;
  int white = a->vi->colormapImage ? 255 : sWhiteLevel;
  int black = a->vi->colormapImage ? 0 : sBlackLevel;

  /* DNM 10/26/03: kept it from reversing the actual levels by copying to
     separate variables; simplified reversal */
  if (black > white){
    cmapReverse = black;
    black = white;
    white = cmapReverse;
    cmapReverse = 1;
  }

  rampsize = white - black;
  if (rampsize < 1) rampsize = 1;
     
  for (i = 0; i < black; i++)
    sCmap[0][i] = 0;
  for (i = white; i < 256; i++)
    sCmap[0][i] = 255;
  slope = 256.0 / (float)rampsize;
  for (i = black; i < white; i++){
    point = (float)(i - black) * slope;
    sCmap[0][i] = (unsigned char)point;
  }
     
  if (cmapReverse && !a->vi->colormapImage){
    for(i = 0; i < 256; i++)
      sCmap[0][i] = 255 - sCmap[0][i];
  }
  if (sFalsecolor || a->vi->colormapImage){
    for(i = 0; i < 256; i++){
      xcramp_mapfalsecolor(sCmap[0][i], &r, &g, &b);
      sCmap[0][i] = (unsigned char)r;
      sCmap[1][i] = (unsigned char)g;
      sCmap[2][i] = (unsigned char)b;
    }
  }else{
    for(i = 0; i < 256; i++){
      sCmap[1][i] = sCmap[2][i] = sCmap[0][i];
    }
  }

  sCmapInit = 1;
}

static void imodvDrawTImage(Ipoint *p1, Ipoint *p2, Ipoint *p3, Ipoint *p4,
                            Ipoint *clamp,
                            unsigned char *data,
                            int width, int height)
{
  float xclamp, yclamp;
  double wallStart = wallTime();
  static int first = 1;

  // 5/16/04: swap here instead of before calling
  // No need to swap anymore
  xclamp = clamp->x;
  yclamp = clamp->y;

  // glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
  //if (first)
    glTexImage2D(GL_TEXTURE_2D, 0, 3, width+2, height+2, 
                 1, GL_RGB, GL_UNSIGNED_BYTE,
                 data);
  first = 0;
  sWallLoad += wallTime() - wallStart;
  wallStart = wallTime();

  /* glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

  // To use linear we need to provide border pixels and different tex coords
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  
  glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
         
  glEnable(GL_TEXTURE_2D); */

  glBegin(GL_QUADS);
  glTexCoord2f(0.0, 0.0); glVertex3fv((GLfloat *)p1);
  glTexCoord2f(xclamp, 0.0); glVertex3fv((GLfloat *)p2);
  glTexCoord2f(xclamp, yclamp); glVertex3fv((GLfloat *)p3);
  glTexCoord2f(0.0, yclamp); glVertex3fv((GLfloat *)p4);


  glEnd();
  sWallDraw += wallTime() - wallStart;

  /*  glFlush();
      glDisable(GL_TEXTURE_2D); */
}

// Determine starting and ending slice and direction, and set the alpha
static void setSliceLimits(int ciz, int miz, bool invertZ, int drawTrans, 
                           int &zst, int &znd, int &izdir)
{
  zst = ciz - sNumSlices / 2;
  znd = zst + sNumSlices - 1;
  if (zst < 0)
    zst = 0;
  if (znd >= miz)
    znd = miz - 1;
  izdir = 1;

  // If transparency is needed and it is time to draw solid, or no transparency
  // is needed and it time to draw transparent, set up for no draw
  if (((znd > zst || sImageTrans) && !drawTrans) ||
      (zst == znd && !sImageTrans && drawTrans))
    znd = zst - 1;

  // Swap direction if needed
  if (invertZ) {
    izdir = zst;
    zst = znd;
    znd = izdir;
    izdir = -1;
  }
}

// Compute starting and ending draw coordinates from center coordinate, desired
// size and maximum size, shifting center if necessary
static void setCoordLimits(int cur, int maxSize, int drawSize, 
                           int &str, int &end)
{
  str = cur - drawSize / 2;
  str = B3DMAX(1, str);
  end = B3DMIN(str + drawSize, maxSize - 1);
  str = B3DMAX(1, end - drawSize);
}

// Set the alpha factor based on transparency, number of images and which image
// this one is
static void setAlpha(int iz, int zst, int znd, int izdir)
{
  float alpha, b;
  int nDraw = (znd - zst) / izdir + 1;
  int m = (iz - zst) / izdir + 1;

  // b is the final alpha factor for each plane after all drawing
  // alpha is computed from b for the plane # m
  b = (float)(0.01 * (100 - sImageTrans) / nDraw);
  alpha = (float)(b / (1. - (nDraw - m) * b));
  glColor4f(alpha, alpha, alpha, alpha);
  if (sImageTrans || nDraw > 1) {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA,  GL_ONE_MINUS_SRC_ALPHA);
  }
}

#define FILLDATA(a)  uvind = 3 * ((sTexImageSize + 2) * v + u); \
  sTdata[uvind] = sCmap[0][a];                                  \
  sTdata[uvind + 1] = sCmap[1][a];                              \
  sTdata[uvind + 2] = sCmap[2][a];

// The call from within the openGL calling routines to draw the image
void imodvDrawImage(ImodvApp *a, int drawTrans)
{
  Ipoint ll, lr, ur, ul, clamp;
  int tstep;
  int cix, ciy, ciz;
  int mix, miy, miz;
  int xstr, xend, ystr, yend, zstr, zend;
  unsigned char **idata;
  b3dUInt16 **usidata;
  unsigned char pix;
  int i, mi, j, mj;
  int u, v, uvind;
  int ix, iy, iz, idir, numSave;
  int cacheSum, curtime;
  unsigned char **imdata;
  b3dUInt16 **usimdata;
  unsigned char *bmap = NULL;
  bool flipped, invertX, invertY, invertZ;
  Imat *mat;
  Ipoint inp, outp;
  QTime drawTime;
  GLint texwid;
  drawTime.start();
  sWallLoad = sWallDraw = 0.;
     
  mix = a->vi->xsize;
  miy = a->vi->ysize;
  miz = a->vi->zsize;
  if (sDia)
    sDia->updateCoords();

  if (!sFlags) 
    return;

  ivwGetLocation(a->vi, &cix, &ciy, &ciz);
  ivwGetTime(a->vi, &curtime);
  if (!sCmapInit || (a->vi->colormapImage && 
                    (ciz != cmapZ || curtime != cmapTime))) {
    mkcmap();
    cmapTime = curtime;
    cmapZ = ciz;
  }

  if (sXdrawSize < 0) {
    sXdrawSize = mix;
    sYdrawSize = miy;
    sZdrawSize = miz;
  }

  // If doing stereo pairs, draw the pair as long as the step up stays in the
  // same set of images for an area
  if (a->imageStereo && a->stereo < 0) {
    iz = ciz + a->imageDeltaZ;
    if (ciz % a->imagesPerArea < iz % a->imagesPerArea)
      ciz = iz;
  }

  // Get data pointers if doing X or Y planes
  if ((sFlags & (IMODV_DRAW_CX | IMODV_DRAW_CY)) &&
       !(a->imageStereo && a->stereo)) {
    if (ivwSetupFastAccess(a->vi, &imdata, 0, &cacheSum))
      return;
    flipped = (!a->vi->vmSize || a->vi->fullCacheFlipped) && 
      a->vi->li->axis == 2;
    usimdata = (b3dUInt16 **)imdata;
  }
  if (a->vi->ushortStore) {
    bmap = ivwUShortInRangeToByteMap(a->vi);
    if (!bmap)
      return;
  }

  // If doing multiple slices, need to find direction in which to do them
  invertX = invertY = invertZ = false;
  if (a->vi->colormapImage && sNumSlices > 1) {
    sNumSlices = 1;
    if (sDia)
      sDia->mSliders->setValue(IIS_SLICES, sNumSlices);
  }
  numSave = sNumSlices;
  if (a->imageStereo && a->stereo)
    sNumSlices = 1;

  if (sNumSlices > 1) {
    mat = imodMatNew(3);
    imodMatRot(mat, (double)a->imod->view->rot.z, b3dZ);
    imodMatRot(mat, (double)a->imod->view->rot.y, b3dY);
    imodMatRot(mat, (double)a->imod->view->rot.x, b3dX);
    
    inp.x = 1.;
    inp.y = inp.z = 0.0f;
    imodMatTransform(mat, &inp, &outp);
    invertX = outp.z < 0.;
    inp.x = 0.;
    inp.y = 1.;
    imodMatTransform(mat, &inp, &outp);
    invertY = outp.z < 0.;
    inp.y = 0.;
    inp.z = 1.;
    imodMatTransform(mat, &inp, &outp);
    invertZ = outp.z < 0.;
    imodMatDelete(mat);
  }

  // Set up OpenGL stuff, starting with assessing the texture size that works
  for (tstep = 520; tstep > 64; tstep /= 2) {
    glTexImage2D(GL_PROXY_TEXTURE_2D, 0, 3, tstep+2, tstep+2, 1, GL_RGB, GL_UNSIGNED_BYTE,
                 NULL);
    glGetTexLevelParameteriv(GL_PROXY_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, &texwid);
    if (texwid > 0)
      break;
  }

  // Allocate a big enough array if necessary
  if (tstep > sTexImageSize) {
    B3DFREE(sTdata);
    sTdata = B3DMALLOC(GLubyte, 3 * (tstep + 2) * (tstep + 2));
    if (!sTdata) {
      imodPrintStderr("Failed to allocate array for image display");
      endTexMapping();
      return;
    }
    sTexImageSize = tstep;
  }

  glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

  // To use linear we need to provide border pixels and different tex coords
  // This will have to be changed when border pixels are not allowed in OpenGL 3+
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  
  glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
         
  glEnable(GL_TEXTURE_2D);

  /* Draw Current Z image. */
  if (sFlags & IMODV_DRAW_CZ) {

    setSliceLimits(ciz, miz, invertZ, drawTrans, zstr, zend, idir);
    setCoordLimits(cix, mix, sXdrawSize, xstr, xend);
    setCoordLimits(ciy, miy, sYdrawSize, ystr, yend);
    for (iz = zstr; idir * (zend - iz) >= 0 ; iz += idir) {
      setAlpha(iz, zstr, zend, idir);
      ll.z = lr.z = ur.z = ul.z = iz;
      idata = ivwGetZSection(a->vi, iz);
      if (!idata)
        continue;
      usidata = (b3dUInt16 **)idata;

      // Loop on patches in Y, get limits to fill and set corners
      for (iy = ystr; iy < yend; iy += tstep){
        clamp.y = 1.0f;
        mj = iy + tstep;
        if (mj > yend) {
          mj = yend;
          clamp.y = (yend - iy) / (float)tstep;
        }
        lr.y = ll.y = iy;
        ul.y = ur.y = mj;
          
        // Loop on patches in X, get limits to fill and set corners
        for (ix = xstr; ix < xend; ix += tstep){
          clamp.x = 1.0f;
          mi = ix + tstep;
          if (mi > xend) {
            mi = xend;
            clamp.x = (xend-ix) / (float)tstep;
          }
          ul.x = ll.x = ix;
          ur.x = lr.x = mi;
        
          // Fill the data for one patch then draw the patch
          if (a->vi->ushortStore) {
            for (j = iy-1; j < mj+1; j++) {
              v = (j - (iy-1));
              for (i = ix-1; i < mi+1; i++) {
                u = i - (ix-1);
                pix = bmap[usidata[j][i]];
                FILLDATA(pix);
              }
            }
          } else {
            for (j = iy-1; j < mj+1; j++) {
              v = (j - (iy-1));
              for (i = ix-1; i < mi+1; i++) {
                u = i - (ix-1);
                pix = idata[j][i];
                FILLDATA(pix);
              }
            }
          }
          
          imodvDrawTImage(&ll, &lr, &ur, &ul, &clamp,
                          (unsigned char *)sTdata, tstep, tstep);
        }
      }
    }
  }
  glDisable(GL_BLEND);

  /* Draw Current X image. */
  if ((sFlags & IMODV_DRAW_CX) && 
      !(a->stereo && a->imageStereo)) {

    setSliceLimits(cix, mix, invertX, drawTrans, xstr, xend, idir);
    setCoordLimits(ciy, miy, sYdrawSize, ystr, yend);
    setCoordLimits(ciz, miz, sZdrawSize, zstr, zend);

    for (ix = xstr; idir * (xend - ix) >= 0 ; ix += idir) {
      setAlpha(ix, xstr, xend, idir);
      ll.x = lr.x = ur.x = ul.x = ix;

      for (iz = zstr; iz < zend; iz += tstep){
        clamp.y = 1.0f;
        mj = iz + tstep;
        if (mj > zend) {
          mj = zend;
          clamp.y = (zend - iz) / (float)tstep;
        }
        lr.z = ll.z = iz;
        ul.z = ur.z = mj;
        
        for (iy = ystr; iy < yend; iy += tstep){
          clamp.x = 1.0f;
          mi = iy + tstep;
          if (mi > yend) {
            mi = yend;
            clamp.x = (yend - iy) / (float)tstep;
          }
          ul.y = ll.y = iy;
          ur.y = lr.y = mi;
        
          // Handle cases of flipped or not with different loops to put test
          // on presence of data in the outer loop
          if (flipped) {
            for (i = iy-1; i < mi+1; i++) {
              u = i - (iy-1);
              if (imdata[i]) {
                if (a->vi->ushortStore) {
                  for (j = iz-1; j < mj+1; j++) {
                    v = j - (iz-1);
                    pix = bmap[usimdata[i][ix + (j * mix)]];
                    FILLDATA(pix);
                  }
                } else {
                  for (j = iz-1; j < mj+1; j++) {
                    v = j - (iz-1);
                    pix = imdata[i][ix + (j * mix)];
                    FILLDATA(pix);
                  }
                }
              } else {
                for (j = iz-1; j < mj+1; j++) {
                  v = j - (iz-1);
                  FILLDATA(0);
                }
              }
            }
          } else {
            for (j = iz-1; j < mj+1; j++) {
              v = j - (iz-1);
              if (imdata[j]) {
                if (a->vi->ushortStore) {
                  for (i = iy-1; i < mi+1; i++) {
                    u = i - (iy-1);
                    pix = bmap[usimdata[j][ix + (i * mix)]];
                    FILLDATA(pix);
                  }
                } else {
                  for (i = iy-1; i < mi+1; i++) {
                    u = i - (iy-1);
                    pix = imdata[j][ix + (i * mix)];
                    FILLDATA(pix);
                  }
                }
              } else {
                for (i = iy-1; i < mi+1; i++) {
                  u = i - (iy-1);
                  FILLDATA(0);
                }
              }
            }
          }
          
          imodvDrawTImage(&ll, &lr, &ur, &ul, &clamp,
                          (unsigned char *)sTdata, tstep, tstep);
        }
      }
    }       
  }
  glDisable(GL_BLEND);

  /* Draw Current Y image. */
  if ((sFlags & IMODV_DRAW_CY) && 
      !(a->stereo && a->imageStereo)) {
    setSliceLimits(ciy, miy, invertY, drawTrans, ystr, yend, idir);
    setCoordLimits(cix, mix, sXdrawSize, xstr, xend);
    setCoordLimits(ciz, miz, sZdrawSize, zstr, zend);

    for (iy = ystr; idir * (yend - iy) >= 0 ; iy += idir) {
      setAlpha(iy, ystr, yend, idir);
      ll.y = lr.y = ur.y = ul.y = iy;

      for (iz = zstr; iz < zend; iz += tstep) {
        clamp.y = 1.0f;
        mj = iz + tstep;
        if (mj > zend) {
          mj = zend;
          clamp.y = (zend - iz) / (float)tstep;
        }
        lr.z = ll.z = iz;
        ul.z = ur.z = mj;

        for (ix = xstr; ix < xend; ix += tstep) {
          clamp.x = 1.0f;
          mi = ix + tstep;
          if (mi > xend) {
            mi = xend;
            clamp.x = (xend - ix) / (float)tstep;
          }
          ul.x = ll.x = ix;
          ur.x = lr.x = mi;
          
          // This one is easier, one outer loop and flipped, non-flipped, or
          // no data cases for inner loop
          for (j = iz-1; j < mj+1; j++) {
            v = j - (iz-1);
            if (flipped && imdata[iy]) {
              if (a->vi->ushortStore) {
                for (i = ix-1; i < mi+1; i++) {
                  u = i - (ix-1);
                  pix = bmap[usimdata[iy][i + (j * mix)]];
                  FILLDATA(pix);
                }
              } else {
                for (i = ix-1; i < mi+1; i++) {
                  u = i - (ix-1);
                  pix = imdata[iy][i + (j * mix)];
                  FILLDATA(pix);
                }
              }
            } else if (!flipped && imdata[j]) {
              if (a->vi->ushortStore) {
                for (i = ix-1; i < mi+1; i++) {
                  u = i - (ix-1);
                  pix = bmap[usimdata[j][i + (iy * mix)]];
                  FILLDATA(pix);
                }
              } else {
                for (i = ix-1; i < mi+1; i++) {
                  u = i - (ix-1);
                  pix = imdata[j][i + (iy * mix)];
                  FILLDATA(pix);
                }
              }
            } else {
              for (i = ix-1; i < mi+1; i++) {
                u = i - (ix-1);
                FILLDATA(0);
              }
            }

          }
          imodvDrawTImage(&ll, &lr, &ur, &ul, &clamp,
                          (unsigned char *)sTdata, tstep, tstep);
        }
      }
    }
  }

  B3DFREE(bmap);
  glDisable(GL_BLEND);
  //glFlush();
  glDisable(GL_TEXTURE_2D);
  sNumSlices = numSave;
  if (imodDebug('v'))
    imodPrintStderr("Draw time %d  load %.2f draw %.2f\n", drawTime.elapsed(),
                    sWallLoad * 1000., sWallDraw * 1000.);
}

// Take care of all flags associated with texture mapping being on
static void endTexMapping()
{
  sFlags = 0;
  Imodv->texMap = 0;
  B3DFREE(sTdata);
  sTexImageSize = 0;
  sTdata = NULL;
}


// THE ImodvImage CLASS IMPLEMENTATION

static char *buttonLabels[] = {"Done", "Help"};
static char *buttonTips[] = {"Close dialog box", "Open help window"};
static char *sliderLabels[] = {"X", "Y", "Z", "X size", "Y size", "Z size",
                               "# of slices", "Transparency", 
                               "Black Level", "White Level"};

ImodvImage::ImodvImage(QWidget *parent, const char *name)
  : DialogFrame(parent, 2, 1, buttonLabels, buttonTips, true, 
                ImodPrefs->getRoundedStyle(), "3dmodv Image View", "", name)
{
  mCtrlPressed = false;

  // Make view checkboxes
  mViewXBox = diaCheckBox("View X image", this, mLayout);
  mViewXBox->setChecked(sFlags & IMODV_DRAW_CX);
  connect(mViewXBox, SIGNAL(toggled(bool)), this, SLOT(viewXToggled(bool)));
  mViewYBox = diaCheckBox("View Y image", this, mLayout);
  mViewYBox->setChecked(sFlags & IMODV_DRAW_CY);
  connect(mViewYBox, SIGNAL(toggled(bool)), this, SLOT(viewYToggled(bool)));
  mViewZBox = diaCheckBox("View Z image", this, mLayout);
  mViewZBox->setChecked(sFlags & IMODV_DRAW_CZ);
  connect(mViewZBox, SIGNAL(toggled(bool)), this, SLOT(viewZToggled(bool)));
  mViewXBox->setToolTip("Display YZ plane at current X");
  mViewYBox->setToolTip("Display XZ plane at current Y");
  mViewZBox->setToolTip("Display XY plane at current Z");

  // Make multisliders
  mSliders = new MultiSlider(this, 10, sliderLabels);

  mSliders->setRange(IIS_X_COORD, 1, Imodv->vi->xsize);
  mSliders->setRange(IIS_X_SIZE, 1, Imodv->vi->xsize);
  if (sLastYsize < 0) {
    sXdrawSize = Imodv->vi->xsize;
    sYdrawSize = Imodv->vi->ysize;
    sZdrawSize = Imodv->vi->zsize;
    sLastYsize = Imodv->vi->ysize;
  }
  updateCoords();
  mSliders->setValue(IIS_X_SIZE, sXdrawSize);
  mSliders->setValue(IIS_Y_SIZE, sYdrawSize);
  mSliders->setValue(IIS_Z_SIZE, sZdrawSize);
  mSliders->setRange(IIS_SLICES, 1, 64);
  mSliders->setValue(IIS_SLICES, sNumSlices);
  mSliders->setRange(IIS_TRANSPARENCY, 0, 100);
  mSliders->setValue(IIS_TRANSPARENCY, sImageTrans);
  mSliders->setValue(IIS_BLACK, sBlackLevel);
  mSliders->setValue(IIS_WHITE, sWhiteLevel);
  mLayout->addLayout(mSliders->getLayout());
  (mSliders->getLayout())->setSpacing(4);
  connect(mSliders, SIGNAL(sliderChanged(int, int, bool)), this, 
          SLOT(sliderMoved(int, int, bool)));
  mSliders->getSlider(IIS_X_COORD)->setToolTip(
                "Set current image X coordinate");
  mSliders->getSlider(IIS_Y_COORD)->setToolTip(
                "Set current image Y coordinate");
  mSliders->getSlider(IIS_Z_COORD)->setToolTip(
                "Set current image Z coordinate");
  mSliders->getSlider(IIS_X_SIZE)->setToolTip(
                "Set image size to display in X");
  mSliders->getSlider(IIS_Y_SIZE)->setToolTip(
                "Set image size to display in Y");
  mSliders->getSlider(IIS_Z_SIZE)->setToolTip(
                "Set image size to display in Z");
  mSliders->getSlider(IIS_SLICES)->setToolTip(
                "Set number of slices to display");
  mSliders->getSlider(IIS_TRANSPARENCY)->setToolTip(
                "Set percent transparency");
  mSliders->getSlider(IIS_BLACK)->setToolTip(
                "Set minimum black level of contrast ramp");
  mSliders->getSlider(IIS_WHITE)->setToolTip(
                "Set maximum white level of contrast ramp");

  // Make false color checkbox
  mFalseBox = diaCheckBox("False color", this, mLayout);
  mFalseBox->setChecked(sFalsecolor);
  connect(mFalseBox, SIGNAL(toggled(bool)), this, SLOT(falseToggled(bool)));
  mFalseBox->setToolTip("Display image in false color");

  if (Imodv->vi->colormapImage) {
    mFalseBox->setEnabled(false);
    mSliders->getSlider(IIS_SLICES)->setEnabled(false);
    mSliders->getSlider(IIS_BLACK)->setEnabled(false);
    mSliders->getSlider(IIS_WHITE)->setEnabled(false);
  }

  connect(this, SIGNAL(actionClicked(int)), this, SLOT(buttonPressed(int)));
}

// Update the current coordinate sliders and their ranges, update the ranges
// of the Y and Z size sliders and swap y and Z size if flipped
void ImodvImage::updateCoords()
{
  int maxSlices = Imodv->vi->zsize < MAX_SLICES ? 
    Imodv->vi->zsize : MAX_SLICES;
  mSliders->setValue(IIS_X_COORD, (int)(Imodv->vi->xmouse + 1.5f));
  mSliders->setRange(IIS_Y_COORD, 1, Imodv->vi->ysize);
  mSliders->setValue(IIS_Y_COORD, (int)(Imodv->vi->ymouse + 1.5f));
  mSliders->setRange(IIS_Z_COORD, 1, Imodv->vi->zsize);
  mSliders->setValue(IIS_Z_COORD, (int)(Imodv->vi->zmouse + 1.5f));
  mSliders->setRange(IIS_Y_SIZE, 1, Imodv->vi->ysize);
  mSliders->setRange(IIS_Z_SIZE, 1, Imodv->vi->zsize);
  mSliders->setRange(IIS_SLICES, 1, maxSlices);
  
 if (sLastYsize != Imodv->vi->ysize) {
    int tmpSize = sYdrawSize;
    sYdrawSize = sZdrawSize;
    sZdrawSize = tmpSize;
    mSliders->setValue(IIS_Y_SIZE, sYdrawSize);
    mSliders->setValue(IIS_Z_SIZE, sZdrawSize);
    sLastYsize = Imodv->vi->ysize;
    if (sNumSlices > maxSlices) {
      sNumSlices = maxSlices;
      mSliders->setValue(IIS_SLICES, sNumSlices);
    }
  }
}

// Viewing image is turned on or off
void ImodvImage::viewToggled(bool state, int flag)
{
  if (!state) {
    sFlags &= ~flag;
    if (!sFlags)
      Imodv->texMap = 0;
  } else {
    sFlags |= flag;
    Imodv->texMap = 1;
  }
  imodvStereoUpdate();
  imodvDraw(Imodv);
}

// respond to a change of transparency or contrast
void ImodvImage::sliderMoved(int which, int value, bool dragging)
{
  switch (which) {
  case IIS_X_COORD:
    Imodv->vi->xmouse = value - 1;
    ivwBindMouse(Imodv->vi);
    break;
  case IIS_Y_COORD:
    Imodv->vi->ymouse = value - 1;
    ivwBindMouse(Imodv->vi);
    break;
  case IIS_Z_COORD:
    Imodv->vi->zmouse = value - 1;
    ivwBindMouse(Imodv->vi);
    break;

  case IIS_X_SIZE:
    sXdrawSize = value;
    break;
  case IIS_Y_SIZE:
    sYdrawSize = value;
    break;
  case IIS_Z_SIZE:
    sZdrawSize = value;
    break;

  case IIS_SLICES: 
    sNumSlices = value;
    break;

  case IIS_TRANSPARENCY: 
    sImageTrans = value;
    Imodv->texTrans = sImageTrans;
    break;

  case IIS_BLACK:
    sBlackLevel = value;
    mkcmap();
    break;
  case IIS_WHITE:
    sWhiteLevel = value;
    mkcmap();
    break;
  }

  // draw if slider clicked or is in hot state
  if (!dragging || ImodPrefs->hotSliderActive(mCtrlPressed)) {
    if (which > IIS_Z_COORD)
      imodvDraw(Imodv);
    else
      imodDraw(Imodv->vi, IMOD_DRAW_XYZ);
  }
}

// User toggles false color
void ImodvImage::falseToggled(bool state)
{
  sFalsecolor = state ? 1 : 0;
  mkcmap();
  imodvDraw(Imodv);
}

// Action buttons
void ImodvImage::buttonPressed(int which)
{
  if (which)
    imodShowHelpPage("modvImage.html#TOP");
  else
    close();
}
  
void ImodvImage::fontChange( const QFont & oldFont )
{
  mRoundedStyle = ImodPrefs->getRoundedStyle();
  DialogFrame::fontChange(oldFont);
}

// Accept a close event and set dia to null
void ImodvImage::closeEvent ( QCloseEvent * e )
{
  imodvDialogManager.remove((QWidget *)sDia);
  sDia = NULL;
  e->accept();
}

// Close on escape; watch for the hot slider key; pass on keypress
void ImodvImage::keyPressEvent ( QKeyEvent * e )
{
  if (utilCloseKey(e))
    close();
  else {
    if (hotSliderFlag() != NO_HOT_SLIDER && e->key() == hotSliderKey()) {
      mCtrlPressed = true;
      grabKeyboard();
    }
    imodvKeyPress(e);
  }
}

// pass on key release; watch for hot slider release
void ImodvImage::keyReleaseEvent ( QKeyEvent * e )
{
  if (e->key() == hotSliderKey()) {
    mCtrlPressed = false;
    releaseKeyboard();
  }
  imodvKeyRelease(e);
}
