c	  VMSTOCSH takes a VMS-style command file from standard input and
c	  converts it to text suitable for piping to a C shell, on standard
c	  output.  The command file may contain: lines to run programs, which
c	  must be preceded by either a $ or a %; comment lines, preceded by
c	  either $! or #; and entries to the programs that are run, which
c	  follow the line starting the program, just as in a VMS command file.
c	  See the manual page for more details.
c
c	  $Author$
c
c	  $Date$
c
c	  $Revision$
c
c	  $Log$
c
	logical reading
	integer*4 iffirst,lenin,lencom,indarrow
	character*1024 linein,linecom
	character*80 logfile
	character*10 herestring/'HERESTRING'/
c	  
	logfile=' '
	lenlog=1
c	  
c	  make indarrow 8 for piping to cat, 2 otherwise
c
	indarrow = 2
	if(iargc().ne.0)then
	  call getarg(1,logfile)
	  lenlog=lnblnk(logfile)
	  write(6,101)'if (-e '//logfile(1:lenlog)//') \\mv -f '
     &	      //logfile(1:lenlog)//' '//logfile(1:lenlog)//'~'
c	    
c	    The alternatives for no piping to cat or piping to cat
c
	  logfile='  > '//logfile(1:lenlog)
c	  logfile=' | cat  > '//logfile(1:lenlog)

	  lenlog=lenlog+indarrow+2
	endif

	write(6,101)'if ($?IMOD_DIR) set path = ($IMOD_DIR/bin $path)'
	write(6,101)'echo2 Shell PID: $$'

	iffirst=-1
	reading=.true.
	do while (reading)
	  reading=.false.
	  linein='$ '
	  read(5,101,end=10)linein
101	  format(a)
	  reading=.true.
10	  lenin=lnblnk(linein)
c	    
c	    For Cygwin/windows, if the line is not properly stripped of
c	    Return, replace it now
c
	  if (lenin.gt.0 .and. linein(lenin:lenin).eq.char(13)) then
	    linein(lenin:lenin) = ' '
	    lenin = lenin - 1
	  endif
	  if(linein(1:1).ne.'#' .and. linein(1:2).ne.'$!')then
	    if(iffirst.eq.0.and.linecom(lencom:lencom).eq.'\\')then
	      if (lencom.gt.1 .and. linecom(lencom-1:lencom-1).eq.'\\') then
c		  
c		  if last line needs to be continued in the output
c		  dump the last line, replace with current line
c		  
		write(6,101)linecom(2:lencom-1)
		linecom = ' '//linein
	      else
c		
c		  otherwise, a continuation line of a command line: add it on
c		  
		linecom=linecom(1:lencom-1)//' '//linein(1:lenin)
	      endif
	      lencom=lnblnk(linecom)
c
	    elseif(linein(1:1).eq.'$'.or.linein(1:1).eq.'%') then
c		
c		a new command line: if the last line was not an entry line
c		to a previous command, it was a command itself and needs to
c		be passed through now; if it was an entry line, put out the
c		herestring to terminate entries
c
	      if(iffirst.ge.0)then
		if(iffirst.eq.0)then
		  write(6,101)linecom(2:lencom)//logfile(1:lenlog)
		  logfile(indarrow:indarrow)='>'
		else
		  write(6,101)herestring
		endif
	      endif
	      linecom=linein
	      lencom=lenin
	      iffirst=0
	    else
c		
c		not a command line: if it is the first entry line, dump the
c		command line with the << herestring on the end
c		in any case, pass the line through
c
	      if(iffirst.ge.0)then
		if(iffirst.eq.0)then
		  write(6,101)linecom(2:lencom)
     &		      //' << '//herestring//logfile(1:lenlog)
		  logfile(indarrow:indarrow)='>'
		  iffirst=1
		endif
		write(6,101)linein(1:lenin)
	      endif
	    endif
	  endif
	enddo
	call exit(0)
	end

	
