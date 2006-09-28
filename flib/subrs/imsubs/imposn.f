C       *IMPOSN
C       
C       Position Read/Write pointer to RELATIVE Section NZ and Line NY
C       
c       modified by mast to position correctly in bit mode files
c       DNM 3/1/01: change to include file, move initialization of 
c       ibleft to imopen.f
c       
      SUBROUTINE IMPOSN(ISTREAM,NZ,NY)
      implicit none
      include 'imsubs.inc'
      integer*4 istream, nz, ny, j, nsection, line, nbytes, npixel, nsize
C       
      J = LSTREAM(ISTREAM)
      FLAG(J) = .FALSE.
      nsection = NZ + 1
      IF (nsection .LT. 1) nsection = 1
      line = NY
      IF (line .LT. 0) line = 0
c       
      if(spider(j))then                         !if SPIDER file
        lrecspi(j)=nsection*ncrs(2,j)-line
        return
      endif
      if(mode(j).lt.8)then                      !if regular modes
        NBYTES = NB(MODE(J) + 1)
        call qseek(j, 1 + nbhdr + nbsym(j), line + 1, nsection,
     &      ncrs(1,j) * nbytes, ncrs(2,j))
      else                              !if bit modes, seek to start of section
        nsize = (ncrs(1,j)*ncrs(2,j)*mode(j)+7)/8
        call qseek(j, 1+ nbhdr + nbsym(j), 0, nsection, nsize, 1)
        npixel=line*ncrs(1,j)                   !# of pixels left to skip
        ibleft(j)=0                             !from an even boundary
        call bitskip(j,npixel,1,*99)
      endif
C       
      RETURN
99    print *,'error doing read from bitskip'
      return
      END
