# IMOD 3.0.16
#
# Startup file for tcsh users of IMOD under Linux - place it in /etc/profile.d
#
# It assumes that IMOD is located in /usr/local - if not, modify IMOD_DIR here
# or set IMOD_DIR before sourcing this file
#
# It assumes that the java run-time environment is installed at the
# recommended location - to use a different jre, modify IMOD_JAVADIR here or
# set IMOD_JAVADIR before sourcing this file

#
# Set IMOD_DIR if it is not set already
#
if (! $?IMOD_DIR) setenv IMOD_DIR /usr/local/IMOD

#
# Set IMOD_JAVADIR if it is not set already
#
if (! $?IMOD_JAVADIR) setenv IMOD_JAVADIR /usr/local/j2re1.4.0_04

#
# Put the IMOD programs on the path
#
if ($?PATH) then
    setenv PATH $IMOD_DIR/bin:$PATH
else
    setenv PATH $IMOD_DIR/bin
endif

#
# Set variable with location of the IMOD plugins
#
setenv IMOD_PLUGIN_DIR $IMOD_DIR/lib/imodplug

#
# Tell the system where the IMOD libraries are located.
#
if ($?LD_LIBRARY_PATH) then
	setenv LD_LIBRARY_PATH $IMOD_DIR/lib:$LD_LIBRARY_PATH
else
	setenv LD_LIBRARY_PATH $IMOD_DIR/lib
endif

#
# A subm alias to run command files in the background with submfg
#
alias subm 'submfg \!* &'

#
# This command allows fast backprojection if the USFFT license file exists
# in either /usr/local/USFFT by hostname, or in IMOD_DIR
#
if (-d /usr/local/USFFT) then
    setenv USFFT2_LICENSE_FILE /usr/local/USFFT/license.clo.$HOST
else
    setenv USFFT2_LICENSE_FILE $IMOD_DIR/license.clo
endif
