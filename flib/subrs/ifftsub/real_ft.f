      SUBROUTINE REAL_FT (EVEN, ODD, N, DIM)
      INTEGER N
      INTEGER DIM(5)
      REAL EVEN(1), ODD(1)
C
C     REAL FOURIER TRANSFORM
C
c	real NYQUIST component is stored as imaginary term at origin
c	modified by D.A. AGARD  3/18/87
C
C     FOR CONVENIENCE THE DATA IS PRESENTED IN TWO PARTS, THE FIRST
C     CONTAINING THE EVEN NUMBERED REAL TERMS AND THE SECOND CONTAINING
C     THE ODD NUMBERED TERMS (NUMBERING STARTING AT 0).  ON RETURN THE
C     REAL PART OF THE TRANSFORM REPLACES THE EVEN TERMS AND THE
C     IMAGINARY PART OF THE TRANSFORM REPLACES THE ODD TERMS.
C
      REAL A, B, C, D, E, F, ANGLE, CO, SI, TWO PI, TWO N
      INTEGER NT, D2, D3, D4, D5
      INTEGER I, J, K, L, N OVER 2, I0, I1, I2
C
      TWO PI = 6.2831853
      TWO N = FLOAT(2*N)
C
      CALL CMPL FT (EVEN, ODD, N, DIM)
C
      NT = DIM(1)
      D2 = DIM(2)
      D3 = DIM(3)
      D4 = DIM(4) - 1
      D5 = DIM(5)
      N OVER 2 = N/2 + 1
C
      IF (N OVER 2 .LT. 2) GO TO 400
      DO 300 I = 2, N OVER 2
      ANGLE = TWO PI*FLOAT(I-1)/TWO N
      CO = COS(ANGLE)
      SI = SIN(ANGLE)
      I0 = (I - 1)*D2 + 1
      J = (N + 2 - 2*I)*D2
      DO 200 I1 = I0, NT, D3
      I2 = I1 + D4
      DO 100 K = I1, I2, D5
      L = K + J
      A = (EVEN(L) + EVEN(K))/2.0
      C = (EVEN(L) - EVEN(K))/2.0
      B = (ODD(L) + ODD(K))/2.0
      D = (ODD(L) - ODD(K))/2.0
      E = C*SI + B*CO
      F = C*CO - B*SI
      EVEN(K) = A + E
      EVEN(L) = A - E
      ODD(K) = F - D
      ODD(L) = F + D
  100 CONTINUE
  200 CONTINUE
  300 CONTINUE
C
  400 CONTINUE
      IF (N .LT. 1) GO TO 600
      J = N*D2
      DO 500 I1 = 1, NT, D3
      I2 = I1 + D4
      DO 500 K = I1, I2, D5
      a = EVEN(K) - ODD(K)
      EVEN(K) = EVEN(K) + ODD(K)
      odd(k) = a
  500 CONTINUE
C
  600 CONTINUE
      RETURN
C
      END
