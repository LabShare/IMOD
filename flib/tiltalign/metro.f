C	METRO -- VARIABLE METRIC MINIMIZATION W/ FLETCHER'S SWITCHING METHOD
C
C		REV 1.0		10/1/79 -- JLC
C
	SUBROUTINE METRO (N,X,F,G,FACTOR,EST,EPS,LIMITin,IER,H,KOUNT)
	DIMENSION X(*),G(*),H(*)
	DOUBLE PRECISION DGAM,DD,S,AK,YA,YB,GA,GB,Z,W,NUJ,NUK
	DOUBLE PRECISION HG,GHG,HGGH,DG,DOT,MU
	MU = 1.D-04
C
C SCRATCH VECTOR STORAGE IN H:
C		H(1 -- N**2) 		- COVARIANCE MATRIX
C		H(N**2+1 -- N**2+N)	- OLD ARGUMENT VECTOR
C		H(N**2+N+1 -- N**2+2N)	- OLD GRADIENT VECTOR
C		H(N**2+2N+1 -- N**2+3N) - DIRECTION VECTOR, 
C					  OVERWRITTEN W HG WHEN UPDATING MA
C
C EVAL INITIAL ARG, GENERATE IDENTITY MATRIX SCALED BY NORM GRAD
	step=est
	STEP=FACTOR
	N2=N**2
	n2pn=n**2+n
	n2p2n=n**2+2*n
	IER=0
	KOUNT=0
	limit=abs(limitin)
	CALL FUNCT (N,X,F,G)
1	GG=DOT(G,G,N)
	DO 20 J=1,N
		DO 10 K=1,N
10			H((J-1)*N+K)=0.
20		H((J-1)*N+J)=1./SQRT(GG/N)
C
C ......................................   MAIN ITERATION LOOP ..............
C
C SAVE OLD F,X,G
21	KOUNT=KOUNT+1
C
	IF(limitin.gt.0.and.MOD(KOUNT,10).EQ.0)WRITE(6,777)KOUNT,F
777	FORMAT(T48,'Cycle',I5,T65,E14.7)
C
	DO 30 J=1,N
		H(n2+  J)=X(J)
		H(n2pn+J)=G(J)
C
C COMPUTE DIRECTION VECTOR
		P=0.
		DO 25 K=1,N
25			P=P-H((J-1)*N+K)*G(K)
30		H(n2p2n+J)=P
C
C COMPUTE COMPONENT OF NEW GRADIENT ALONG SEARCH VECTOR
	YB=F
	GG=DOT(G,G,N)
	GB=DOT(H(n2p2n+1),G,N)
	IF (GG.EQ.0.) GO TO 500		!CONVERGED
	IF (GB.GE.0.D0) GO TO 1		!CAN'T REDUCE F ALONG THIS LINE --
					!RETRY STEEPEST
C
C ------------------------------   EXTRAPOLATE -------------------------------
	YA=YB
	GA=GB
	DO 50 J=1,N
50		X(J)=X(J)+STEP*H(n2p2n+J)
	CALL FUNCT (N,X,F,G)
	YB=F
	GB=DOT(H(n2p2n+1),G,N)
	DG=0.D0
	DO 60 J=1,N
60		DG=DG+(X(J)-H(n2+J))*H(n2pn+J)
	IF (DG.GE.0.D0) GO TO 600		!SOMETHING'S FLAKY
	IF ((YB-YA)/DG.GE.MU) STEP=FACTOR	!GOOD IMPROVEMENT -- 
						!USE FULL SHIFT NEXT
	IF ((YB-YA)/DG.GE.MU) GO TO 100		!IMPROVEMENT W/OUT LINEAR 
						!SEARCH WAS SATISFA
C
C NOT ENOUGH IMPROVEMENT -- TRY CUBIC INTERPOLATION IF STEP WAS TOO LARGE
	IF (GB.LT.0..AND.YB.LT.YA) GO TO 95
	S=STEP
	Z=3.D0*(YA-YB)/S+GA+GB
	W=DSQRT(Z**2-GA*GB)
	AK=S*(GB+W-Z)/(GB-GA+2.D0*W)
	IF (DABS(AK).GT.S) AK=AK*0.90D0*S/DABS(AK)
	DO 80 J=1,N
80		X(J)=X(J)-AK*H(n2p2n+J)
	CALL FUNCT (N,X,F,G)
	IF (F.GT.YA.OR.F.GT.YB) GO TO 95	!INTERPOLATION FAILED
C
C DECREASE STEP SIZE NEXT TIME
	STEP=STEP*DMAX1(1.-AK,1.D-1)
	GO TO 100
C
C TRY STEPSIZE OF 0.1**N IF NOTHING ELSE WORKS
95	CALL MOVE (X,H(n2+1),4*N)	!RESTORE OLD ARGUMENT
	CALL MOVE (G,H(n2pn+1),4*N)	!RESTORE OLD GRADIENT
	STEP=0.1*STEP
	IF (STEP/FACTOR.LT.1.E-06) GO TO 700	!ERROR
	GO TO 21				!TRY IT
C -------------------------- END EXTRAPOLATION -----------------------------
C
C CHECK CONVERGENCE
100	DELTA=0.
	DGAM=0.D0
	DG=0.D0
	DO 110 J=1,N
		DG=DG+(X(J)-H(n2+J))*H(n2pn+J)
		DGAM=DGAM+(X(J)-H(n2+J))*(G(J)-H(n2pn+J))
110		DELTA=DELTA+(X(J)-H(n2+J))**2
	IF (SQRT(DELTA).LE.EPS) RETURN
	IF (DG.GE.0.D0) GO TO 600		!ERROR
	IF (KOUNT.GE.LIMIT) GO TO 750
C
C UPDATE COVARIANCE MATRIX ACCORDING TO SWITCHING ALGORITHM OF FLETCHER
	GHG=0.D0
	POSDEF=0.
	DO 130 J=1,N
		SIG=0.
		HG=0.D0
		DO 120 K=1,N
			SIG=SIG+H((J-1)*N+K)*G(K)
120			HG=HG+H((J-1)*N+K)*(G(K)-H(n2pn+K))
		H(n2p2n+J)=HG
		GHG=GHG+(G(J)-H(n2pn+J))*HG
130		POSDEF=POSDEF+G(J)*SIG
	IF (POSDEF.LT.0.) GO TO 800		!ERROR
C
C H0 ALGORITHM
	DO 140 J=1,N
		DO 140 K=1,N
			DD=(X(J)-H(n2+J))*(X(K)-H(n2+K))
			HGGH=H(n2p2n+J)*H(n2p2n+K)
			H((J-1)*N+K)=H((J-1)*N+K)+DD/DGAM-HGGH/GHG
140			CONTINUE
C
C H1 ALGORITHM
	IF (DGAM.LT.GHG) GO TO 21
	DO 150 J=1,N
		NUJ=DSQRT(GHG)*((X(J)-H(n2+J))/DGAM-H(n2p2n+J)/GHG)
		DO 150 K=1,N
			NUK=DSQRT(GHG)*((X(K)-H(n2+K))/DGAM-
     .			H(n2p2n+K)/GHG)
			H((J-1)*N+K)=H((J-1)*N+K)+NUJ*NUK
150			CONTINUE
	GO TO 21			!CONTINUE W NEXT DIRECTION
C ..................................... END MAIN LOOP .....................
C
C DONE -- SET ERROR CODE AND RETURN
500	RETURN							!NORMAL RETURN
600	IER=1							!DG > 0
	RETURN
700	IER=2						!LINEAR SEARCH LOST
	RETURN
750	IER=3						!KOUNT HAS REACHED LIMIT
	RETURN
800	IER=4						!MATRIX NON-POSITIVE DEFINITE
	RETURN
	END
C**DOT
	DOUBLE PRECISION FUNCTION DOT(A,B,N)
	DIMENSION A(*),B(*)
	DOUBLE PRECISION AD,BD
C
	DOT = 0.0
	DO 100 J = 1,N
	  AD = A(J)
	  BD = B(J)
	  DOT = DOT + AD*BD
100	CONTINUE
C
	C= DOT
	DOT = C
	RETURN
	END
