package etomo.process;

/**
 * <p>Description: ParsePID will parse the process ID from a (csh) process
 * that writes it out to standard error.  The process ID is stored in a string
 * buffer that is created by the invoking object.  This is implemented as
 * runnable class with the expectation that it will be run in its own thread.</p>
 * 
 * <p>Copyright: Copyright (c) 2002</p>
 * 
 * <p>Organization: Boulder Laboratory for 3D Fine Structure,
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 * 
 * <p> $Log$ </p>
 */
/**
 * @author rickg
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class ParsePID implements Runnable {
  public static final String rcsid = "$Id$";
  SystemProgram csh;
  StringBuffer PID;
  
  public ParsePID(SystemProgram cshProcess, StringBuffer bufPID) {
    csh = cshProcess;
    PID = bufPID;
  }


  public void run() {
    //  Wait for the csh thread to start
    while (!csh.isStarted()) {
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException except) {
        return;
      }
    }

    // Once it is started scan the stderr output for the appropriate string
    while (PID.length() == 0 && !csh.isDone()) {
      try {
        parsePIDString();
        Thread.sleep(100);
      }
      catch (InterruptedException except) {
        return;
      }
    }
  }

  /**
   * Walk the standard error output to parse the PID string
   */
  private void parsePIDString() {
    String[] stderr = csh.getStdError();
    for (int i = 0; i < stderr.length; i++) {
      if (stderr[i].startsWith("Shell PID:")) {
        String[] tokens = stderr[i].split("\\s+");
        if (tokens.length > 2) {
          PID.append(tokens[2]);
        }
      }
    }
  }
}

