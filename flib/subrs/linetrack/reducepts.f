c	  REDUCEPTS reduces a set of points by smoothing and selecting a
c	  minimal subset of the points.  Each of the original points will be
c	  within a tolerance TOL of the segments defined by the new set of
c	  points.
c	  XX, YY are coordinates of the points
c	  NPTS is the number of points
c	  INORDER is the order of a polynomial to be fit to successive sets of
c	  points for smoothing; INFIT is the number of points to fit.  No
c	  smoothing is used if either value is zero.
c	  TOL is the tolerance
c	  XS, YS are scratch real*4 arrays, NEXTPT and MINSEG are scratch
c	  integer*4 arrays which should be at least as large as the original
c	  number of points
c	  The reduced set of points and number of points are returned in
c	  XX, YY and NPTS.
c	  
c	  THIS IS A SPECIAL VERSION THAT WILL ONLY DO LINEAR OR QUADRATIC FITS
c	  FOR SMOOTHING, AND CONTAINS ALL OF THE SUBROUTINES FOR THESE FITS.
c
	subroutine reducepts(xx,yy,npts,inorder,infit,tol,xs,ys,nextpt,
     &	    minseg)
	parameter (limfit=30,limord=2)
	real*4 xx(*),yy(*),xs(*),ys(*)
	integer*4 nextpt(*),minseg(*)
	real*4 xt(limfit),yt(limfit),slop(limord)
c
	nfit=min(infit,limfit)
	iorder=min(inorder,limord)
	do icen=1,npts
c	    
c	    copy data into xs,ys arrays
c
	  xs(icen)=xx(icen)
	  ys(icen)=yy(icen)
	  if(iorder.gt.0.and.nfit.gt.iorder.and.nfit.le.npts.and.
     &	      icen.gt.1.and.icen.lt.npts)then
c	  
c	  try to smooth data if iorder/nfit are set
c	  
	    ist=max(1,icen-nfit/2)
	    ind=min(npts,ist+nfit-1)
	    ist=ind+1-nfit
	    dx=xx(ind)-xx(ist)
	    dy=yy(ind)-yy(ist)
	    dlen=sqrt(dx**2+dy**2)
	    xmid=0.5*(xx(ind)+xx(ist))
	    ymid=0.5*(yy(ind)+yy(ist))
	    if(dlen.gt.1.)then
c		
c		if the segment spanned by nfit points is long enough, rotate
c		it about its midpoint so that it is horizontal
c
	      sinth=-dy/dlen
	      costh=dx/dlen
	      do in=ist,ind
		io=in+1-ist
		xt(io)=costh*(xx(in)-xmid)-sinth*(yy(in)-ymid)
		yt(io)=sinth*(xx(in)-xmid)+costh*(yy(in)-ymid)
		if(in.eq.icen)jcen=io
	      enddo
c		
c		fit to rotated points
c
c	      call localpolyfit(xt,yt,nfit,iorder,slop,bint)
	      if(iorder.eq.1)then
		call lsfit(xt,yt,nfit,slop(1),bint,ro)
	      else
		call quadfit(xt,yt,nfit,slop(2),slop(1),bint)
	      endif
	      ycen=bint
	      do i=1,iorder
		ycen=ycen+slop(i)*xt(jcen)**i
	      enddo
	      if(abs(ycen-yt(jcen)).lt.tol)then
c		  
c		  if the fitted point is within tolerance of the actual value
c		  replace the actual value in the xs,ys arrays with the
c		  back-rotated fitted position
c
		xs(icen)=costh*xt(jcen)+sinth*ycen+xmid
		ys(icen)=-sinth*xt(jcen)+costh*ycen+ymid
	      endif
	    endif
	  endif
	enddo
c
c	  moving from right to left, look at possible segments from a given
c	  point going to right.  A segment is possible if all intervening
c	  points are within TOL of the segment.  From the given point, find
c	  the possible segment that involves the fewest segments to get to
c	  the right end.  Keep track of the endpoint of the best segment in
c	  NEXTPT and the total number of segments in MINSEG
c
	minseg(npts)=0
	minseg(npts-1)=1
	nextpt(npts)=npts+1
	nextpt(npts-1)=npts
	tolsq=tol**2
c	  
c	  Stop looking at longer segments after finding NOUTLIM segments that
c	  are not possible
c
	noutlim=3
	do left=npts-2,1,-1
c	    
c	    set left edge of segment
c
	  minseg(left)=minseg(left+1)+1
	  nextpt(left)=left+1
	  irt=left+2
	  x1=xs(left)
	  y1=ys(left)
	  nout=0
	  do while(irt.le.npts.and.nout.lt.noutlim)
	    if(1+minseg(irt).lt.minseg(left))then
c		
c		look at a segment only if it will give a better path: set
c		right edge
c
	      ifout=0
	      x2=xs(irt)
	      y2=ys(irt)
	      dx=x2-x1
	      dy=y2-y1
	      denom=dx**2+dy**2
	      its=left+1
	      do while(its.lt.irt.and.ifout.eq.0)
c		  
c		  check distance of points from segment until one falls out
c
		dx0=xx(its)-x1
		dy0=yy(its)-y1
		if(denom.lt.1.e-5)then
		  distsq=dx0**2+dy0**2
		else
		  tmin=(dx0*dx+dy0*dy)/denom
		  distsq=(tmin*dx-dx0)**2+(tmin*dy-dy0)**2
		endif
		if(distsq.ge.tolsq)ifout=1
		its=its+1
	      enddo
c		
c		if its an OK segment, set it up as new minimum
c
	      if(ifout.eq.0)then
		minseg(left)=1+minseg(irt)
		nextpt(left)=irt
	      else
		nout=nout+1
	      endif
	    endif
	    irt=irt+1
	  enddo
	enddo
c	  
c	  when we get to the left edge, the minimal path is available by 
c	  following the chain of NEXTPT values
c
	npo=1
	ipo=1
	do while(nextpt(ipo).le.npts)
	  ipo=nextpt(ipo)
	  npo=npo+1
	  xx(npo)=xs(ipo)
	  yy(npo)=ys(ipo)
	enddo
c	type *,npts,npo
	npts=npo
	return
	end


	subroutine quadfit(x,y,n,aa,bb,cc)
	parameter (limfit=30)
	real*4 x(*),y(*),xt(limfit)
	do i=1,n
	  xt(i)=x(i)**2
	enddo
	call lsfit2(xt,x,y,n,aa,bb,cc)
	return
	end


	subroutine lsfit2(x1,x2,y,n,a,b,c)
	real*4 x1(*),x2(*),y(*)
	x1s=0.
	x2s=0.
	ys=0.
	do i=1,n
	  x1s=x1s+x1(i)
	  x2s=x2s+x2(i)
	  ys=ys+y(i)
	enddo
c	  
	x1m=x1s/n
	x2m=x2s/n
	ym=ys/n
	x1sqs=0.
	x2sqs=0.
	x1x2s=0.
	x1ys=0.
	x2ys=0.
	do i=1,n
	  x1p=x1(i)-x1m
	  x2p=x2(i)-x2m
	  yp=y(i)-ym
	  x1sqs=x1sqs+x1p**2
	  x2sqs=x2sqs+x2p**2
	  x1ys=x1ys+x1p*yp
	  x2ys=x2ys+x2p*yp
	  x1x2s=x1x2s+x1p*x2p
	enddo
	denom=x1sqs*x2sqs-x1x2s**2
	anum=x1ys*x2sqs-x1x2s*x2ys
	bnum=x1sqs*x2ys-x1ys*x1x2s
	a=0.
	b=0.
	c=0.
	if(abs(denom).lt.1.e-20*max(abs(anum),abs(bnum)))return
	a=anum/denom
	b=bnum/denom
	c=ym-a*x1m-b*x2m
	return
	end

c	  LSFIT fits a straight line to the N points in arrays X and Y by the
c	  method of least squares, returning SLOPE, intercept BINT, and
c	  correlation coeficient RO
c
	subroutine lsfit(x,y,n,slope,bint,ro)
	dimension x(*),y(*)
	call lsfits(x,y,n,slope,bint,ro,se,sb,sa)
	return
	end

	subroutine lsfits(x,y,n,slope,bint,ro,se,sb,sa)
	dimension x(*),y(*)
	sx=0.
	sy=0.
	do 10 i=1,n
	  sx=sx+x(i)
	  sy=sy+y(i)
10	continue
	xbar=sx/n
	ybar=sy/n
	sxpsq=0.
	sxyp=0.
	sypsq=0.
	do 12 i=1,n
	  xp=x(i)-xbar
	  yp=y(i)-ybar
	  sxpsq=sxpsq+xp**2
	  sypsq=sypsq+yp**2
	  sxyp=sxyp+xp*yp
12	continue
	d=n*sxpsq
	slope=sxyp/sxpsq
	bint=(ybar*sxpsq-xbar*sxyp)/sxpsq
	roden=sqrt(sxpsq*sypsq)
	ro=1.
	if(roden.ne.0..and.abs(sxyp).le.abs(roden))ro=sxyp/roden
	se=0.
	sxy=sxyp+n*xbar*ybar
	sysq=sypsq+n*ybar**2
	if(n.gt.2)se=sqrt(max(0.,(sysq-bint*sy-slope*sxy)/(n-2)))
	sa=se*sqrt(1./n+(sx**2/n)/d)
	sb=se/sqrt(d/n)
	return
	end
