package etomo.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

import etomo.ApplicationManager;
import etomo.type.AxisID;
import etomo.util.InvalidParameterException;
import etomo.util.Utilities;

/**
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright (c) 2002, 2003</p>
 * 
  * <p>Organization: Boulder Laboratory for 3D Electron Microscopy (BL3DEM),
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 * 
 * <p> $Log$
 * <p> Revision 3.3  2004/06/17 01:29:12  sueh
 * <p> removed unnecessary import
 * <p>
 * <p> Revision 3.2  2004/06/14 17:26:08  sueh
 * <p> bug# 460 set startTime earlier, allow startTime to be set in
 * <p> the child class, make sure the instance is still reuseable
 * <p>
 * <p> Revision 3.1  2004/04/08 17:33:59  rickg
 * <p> Use Utilities.milliesToMinAndSecs to get time string
 * <p>
 * <p> Revision 3.0  2003/11/07 23:19:00  rickg
 * <p> Version 1.0.0
 * <p>
 * <p> Revision 1.5  2003/09/08 22:21:31  rickg
 * <p> Limit percentage done to between 0 and 99
 * <p>
 * <p> Revision 1.4  2003/08/04 22:22:57  rickg
 * <p> Fixed typo
 * <p>
 * <p> Revision 1.3  2003/07/01 22:55:02  rickg
 * <p> Added starting text to progress for slow startup scripts
 * <p>
 * <p> Revision 1.2  2003/07/01 19:26:30  rickg
 * <p> Changed all sizes to k bytes
 * <p> Change round to floor to prevent 1:60 times
 * <p>
 * <p> Revision 1.1  2003/06/27 20:16:36  rickg
 * <p> Initial revision
 * <p> </p>
 */

public abstract class FileSizeProcessMonitor implements Runnable {
  public static final String rcsid =
    "$Id$";
  ApplicationManager applicationManager;
  AxisID axisID;
  long processStartTime;
  File watchedFile;
  FileChannel watchedChannel;
  
  int nKBytes;

  int updatePeriod = 500;

  public FileSizeProcessMonitor(ApplicationManager appMgr, AxisID id) {
    applicationManager = appMgr;
    axisID = id;
  }

  // The dervied class must implement this function to 
  // - set the expected number of bytes in the output file
  // - initialize the progress bar through the application manager, the maximum
  //   value should be the expected size of the file in k bytes
  // - set the watchedFile reference to the output file being monitored.
  abstract void calcFileSize() throws InvalidParameterException, IOException;

  public void run() {
    try {
      // Reset the progressBar 
      applicationManager.setProgressBar(" ", 1, axisID);
      applicationManager.setProgressBarValue(0, "Starting...", axisID);
      
      //  Calculate the expected file size in bytes, initialize the progress bar
      //  and set the File object.
      calcFileSize();

      //  Wait for the output file to be created and set the process start time
      waitForFile();
    }
    //  Interrupted ???  kill the thread by exiting
    catch (InterruptedException except) {
      return;
    }
    catch (InvalidParameterException except) {
      except.printStackTrace();
      return;
    }
    catch (IOException except) {
      except.printStackTrace();
      return;
    }

    // Periodically update the process bar by checking the size of the file
    updateProgressBar();
  }

  /**
   * Wait for the new output file to be created.  Make sure it is current by
   * comparing the modification time of the file to the start time of this
   * function. Set the process start time to the first new file modification
   * time since we don't have access to the file creation time.  
   */
  void waitForFile() throws InterruptedException {
    long lastSize = 0;
    long currentSize = 0;
    boolean newOutputFile = false;
    boolean needChannel = true;
    while (!newOutputFile) {
      if (watchedFile.exists()) {
        if(needChannel) {
          FileInputStream stream = null;
          try {
            stream = new FileInputStream(watchedFile);
          }
          catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println(
              "Shouldn't be in here, we already checked for existence");
          }
          watchedChannel = stream.getChannel();
          needChannel = false;
          try {
            lastSize = watchedChannel.size();
          }
          catch (IOException except) {
            except.printStackTrace();
          }
        }
        
        try {
          currentSize = watchedChannel.size();
          if(currentSize > lastSize) {
            newOutputFile = true;
            processStartTime = System.currentTimeMillis();
          }
          else {
            lastSize = currentSize;
          }
        }
        catch (IOException except) {
          except.printStackTrace();
        }
      }
      Thread.sleep(updatePeriod);
    }
  }

  /**
   * Watch the file size, comparing it to the expected completed file size and
   * update the progress bar 
   *
   */
  void updateProgressBar() {
    boolean fileWriting = true;

    while (fileWriting) {
      int currentLength = 0;
      try {
        currentLength = (int) (watchedChannel.size() / 1024);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      double fractionDone = (double) currentLength / nKBytes;
      int percentage = (int) Math.round(fractionDone * 100);

      //  Catch any wierd values before they get displayed
      if(percentage < 0) {
        percentage = 0;
      }
      if(percentage > 99) {
        percentage = 99;
      }
      
      long elapsedTime = System.currentTimeMillis() - processStartTime;
      double remainingTime = elapsedTime / fractionDone - elapsedTime;
      String message =
        String.valueOf(percentage)
          + "%   ETC: "
          + Utilities.millisToMinAndSecs(remainingTime);
      applicationManager.setProgressBarValue(currentLength, message, axisID);
      
      //  TODO: need to put a fail safe in here to
      try {
        Thread.sleep(updatePeriod);
      }
      catch (InterruptedException exception) {
        fileWriting = false;
        try {
          watchedChannel.close();
        }
        catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }
}