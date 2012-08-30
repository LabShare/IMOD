package etomo.process;

import java.util.HashMap;

import etomo.BaseManager;
import etomo.type.AxisID;

/**
* <p>Description: Data used by the process managers.  This class has a one-to-one
* relationship with BaseManager.</p>
* 
* <p>Copyright: Copyright 2012</p>
*
* <p>Organization:
* Boulder Laboratory for 3-Dimensional Electron Microscopy of Cells (BL3DEMC),
* University of Colorado</p>
* 
* @author $Author$
* 
* @version $Revision$
* 
* <p> $Log$ </p>
*/
public final class AxisProcessData {
  public static final String rcsid = "$Id:$";

  private final HashMap killedList = new HashMap();

  private final ProcessData savedProcessDataA;
  private final ProcessData savedProcessDataB;

  private SystemProcessInterface threadAxisA = null;
  private SystemProcessInterface threadAxisB = null;
  private Thread processMonitorA = null;
  private Thread processMonitorB = null;
  private boolean blockAxisA = true;
  private boolean blockAxisB = true;

  void dumpState() {
    System.err.println("[processMonitorA:" + processMonitorA + ",processMonitorB:"
        + processMonitorB + ",killedList:");
    if (killedList != null) {
      System.err.println(killedList.toString());
    }
    System.err.println(",blockAxisA:" + blockAxisA + ",blockAxisB:" + blockAxisB + "]");
  }

  public AxisProcessData(final BaseManager manager) {
    savedProcessDataA = new ProcessData(AxisID.FIRST, manager);
    savedProcessDataB = new ProcessData(AxisID.SECOND, manager);
  }

  public String toString() {
    return "threadAxisA=" + threadAxisA + ",threadAxisB=" + threadAxisB
        + ",\nprocessMonitorA=" + processMonitorA + ",processMonitorB=" + processMonitorB
        + ",\nkilledList=" + killedList;
  }

  boolean isThreadAxisNull(final AxisID axisID) {
    if (axisID == AxisID.SECOND) {
      return threadAxisB == null;
    }
    return threadAxisA == null;
  }

  /**
   * Save the process thread reference for the appropriate axis
   * 
   * @param thread
   * @param axisID
   */
  void mapAxisThread(final SystemProcessInterface thread, final AxisID axisID) {
    if (axisID == AxisID.SECOND) {
      threadAxisB = thread;
    }
    else {
      threadAxisA = thread;
    }
  }

  public SystemProcessInterface getThread(final AxisID axisID) {
    SystemProcessInterface thread = null;
    if (axisID == AxisID.SECOND) {
      thread = threadAxisB;
    }
    else {
      thread = threadAxisA;
    }
    return thread;
  }

  /**
   * Save the process monitor thread reference for the appropriate axis
   * 
   * @param processMonitor
   * @param axisID
   */
  void mapAxisProcessMonitor(final Thread processMonitor, final AxisID axisID) {
    if (axisID == AxisID.SECOND) {
      processMonitorB = processMonitor;
    }
    else {
      processMonitorA = processMonitor;
    }
  }

  void clearThread(final ComScriptProcess script) {
    // Null out the correct thread
    // Interrupt the process monitor and nulll out the appropriate references
    if (threadAxisA == script) {
      if (processMonitorA != null) {
        processMonitorA.interrupt();
        processMonitorA = null;
      }
      threadAxisA = null;
    }
    if (threadAxisB == script) {
      if (processMonitorB != null) {
        processMonitorB.interrupt();
        processMonitorB = null;
      }
      threadAxisB = null;
    }
  }

  void clearThread(final ReconnectProcess script) {
    // Null out the correct thread
    // Interrupt the process monitor and nulll out the appropriate references
    if (threadAxisA == script) {
      if (processMonitorA != null) {
        processMonitorA.interrupt();
        processMonitorA = null;
      }
      threadAxisA = null;
    }
    if (threadAxisB == script) {
      if (processMonitorB != null) {
        processMonitorB.interrupt();
        processMonitorB = null;
      }
      threadAxisB = null;
    }
  }

  void clearThread(final DetachedProcess process) {
    // Null the reference to the appropriate thread
    if (process == threadAxisA) {
      threadAxisA = null;
    }
    if (process == threadAxisB) {
      threadAxisB = null;
    }
  }

  void clearThread(final BackgroundProcess process) {
    // Null the reference to the appropriate thread
    if (process == threadAxisA) {
      threadAxisA = null;
    }
    if (process == threadAxisB) {
      threadAxisB = null;
    }
  }

  public ProcessData getSavedProcessData(final AxisID axisID) {
    if (axisID == AxisID.SECOND) {
      return savedProcessDataB;
    }
    return savedProcessDataA;
  }

  void putKilledList(final String processID) {
    killedList.put(processID, "");
  }

  boolean containsKeyKilledList(final String pidField) {
    return killedList.containsKey(pidField);
  }

  boolean isBlockAxis(final AxisID axisID) {
    if (axisID == AxisID.SECOND) {
      return blockAxisB;
    }
    return blockAxisA;
  }

  void setBlockAxis(final AxisID axisID, final boolean input) {
    if (axisID == AxisID.SECOND) {
      blockAxisB = input;
    }
    else {
      blockAxisA = input;
    }
  }
}
