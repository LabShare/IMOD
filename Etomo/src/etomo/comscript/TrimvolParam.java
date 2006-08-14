/**
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright (c) 2002 - 2006</p>
 * 
 * <p>Organization: Boulder Laboratory for 3D Fine Structure,
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 * 
 * <p> $Log$
 * <p> Revision 3.19  2006/07/10 20:04:20  sueh
 * <p> bug# 881 Don't subtract 1 from scale X/Y min/max if it is null
 * <p>
 * <p> Revision 3.18  2006/07/05 23:23:54  sueh
 * <p> Convert rubberband points to coordinates for scaling X and Y.
 * <p>
 * <p> Revision 3.17  2006/06/28 23:28:30  sueh
 * <p> bug# 881 Added scaleXMin, scaleXMax, scaleYMin, and scaleYMax.
 * <p>
 * <p> Revision 3.16  2006/06/27 17:47:19  sueh
 * <p> bug# 879 Add rotateX.
 * <p>
 * <p> Revision 3.15  2006/05/22 22:42:51  sueh
 * <p> bug# 577 Added getCommand().
 * <p>
 * <p> Revision 3.14  2006/05/11 19:50:36  sueh
 * <p> bug# 838 Add CommandDetails, which extends Command and
 * <p> ProcessDetails.  Changed ProcessDetails to only contain generic get
 * <p> functions.  Command contains all the command oriented functions.
 * <p>
 * <p> Revision 3.13  2006/04/06 19:38:35  sueh
 * <p> bug# 808 Implementing ProcessDetails.  Added Fields to pass requests to
 * <p> the generic gets.
 * <p>
 * <p> Revision 3.12  2006/01/20 20:48:23  sueh
 * <p> updated copyright year
 * <p>
 * <p> Revision 3.11  2005/11/19 01:54:59  sueh
 * <p> bug# 744 Moved functions only used by process manager post
 * <p> processing and error processing from Commands to ProcessDetails.
 * <p> This allows ProcesschunksParam to be passed to DetackedProcess
 * <p> without having to add unnecessary functions to it.
 * <p>
 * <p> Revision 3.10  2005/09/02 18:56:20  sueh
 * <p> bug# 720 Pass the manager to TrimvolParam instead of propertyUserDir
 * <p> because TrimvolParam is constructed by MetaData before
 * <p> propertyUserDir is set.
 * <p>
 * <p> Revision 3.9  2005/07/29 00:50:21  sueh
 * <p> bug# 709 Going to EtomoDirector to get the current manager is unreliable
 * <p> because the current manager changes when the user changes the tab.
 * <p> Passing the manager where its needed.
 * <p>
 * <p> Revision 3.8  2005/06/20 16:41:16  sueh
 * <p> bug# 522 Made MRCHeader an n'ton.  Getting instance instead of
 * <p> constructing in setDefaultRange().
 * <p>
 * <p> Revision 3.7  2005/04/25 20:41:32  sueh
 * <p> bug# 615 Passing the axis where a command originates to the message
 * <p> functions so that the message will be popped up in the correct window.
 * <p> This requires adding AxisID to many objects.
 * <p>
 * <p> Revision 3.6  2005/01/08 01:46:27  sueh
 * <p> bug# 578 Changed the names of the statics used to make variables
 * <p> available in the Command interface.  Add GET_.  Updated Command
 * <p> interface.
 * <p>
 * <p> Revision 3.5  2004/12/16 02:14:31  sueh
 * <p> bug# 564 Fixed bug: command array was not refreshing.  Refresh
 * <p> command array when getCommandArray() is called.
 * <p>
 * <p> Revision 3.4  2004/12/08 21:22:34  sueh
 * <p> bug# 564 Implemented Command.  Provided access to swapYZ.
 * <p>
 * <p> Revision 3.3  2004/12/02 18:27:49  sueh
 * <p> bug# 557 Added a static getOutputFile(String datasetName) to put the
 * <p> responsibility of knowing how to build the trimvol output file in
 * <p> TrimvolParam.
 * <p>
 * <p> Revision 3.2  2004/06/22 01:53:33  sueh
 * <p> bug# 441 Added store(), load(), and equals().  Prevented
 * <p> setDefaultRange from overriding non-default values.  Moved
 * <p> the logic for creating the inputFile and outputFile names into
 * <p> this class
 * <p>
 * <p> Revision 3.1  2004/04/22 23:27:28  rickg
 * <p> Switched getIMODBinPath method
 * <p>
 * <p> Revision 3.0  2003/11/07 23:19:00  rickg
 * <p> Version 1.0.0
 * <p>
 * <p> Revision 1.12  2003/11/06 21:28:51  sueh
 * <p> bug307 setDefaultRange(String): Set sectionScaleMin to 1/3
 * <p> y or z max and sectionScaleMax to 2/3 y or z max.
 * <p>
 * <p> Revision 1.11  2003/11/06 16:50:27  rickg
 * <p> Removed -e flag for tcsh execution for all but the com scripts
 * <p>
 * <p> Revision 1.10  2003/11/04 20:56:11  rickg
 * <p> Bug #345 IMOD Directory supplied by a static function from ApplicationManager
 * <p>
 * <p> Revision 1.9  2003/10/20 23:25:26  rickg
 * <p> Bug# 253 Added convert to bytes checkbox
 * <p>
 * <p> Revision 1.8  2003/05/23 22:03:37  rickg
 * <p> Added -P to command string to get shell PID output
 * <p>
 * <p> Revision 1.7  2003/05/21 21:23:46  rickg
 * <p> Added e flag to tcsh execution
 * <p>
 * <p> Revision 1.6  2003/05/14 21:45:59  rickg
 * <p> Added full path to trimvol script for windows
 * <p>
 * <p> Revision 1.5  2003/04/16 22:19:30  rickg
 * <p> Initial revision
 * <p>
 * <p> Revision 1.4  2003/04/16 00:14:12  rickg
 * <p> Trimvol in progress
 * <p>
 * <p> Revision 1.3  2003/04/14 23:56:59  rickg
 * <p> Default state of YZ swap changed to true
 * <p>
 * <p> Revision 1.2  2003/04/10 23:40:40  rickg
 * <p> In progress
 * <p>
 * <p> Revision 1.1  2003/04/09 23:36:57  rickg
 * <p> In progress
 * <p> </p>
 */
package etomo.comscript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import etomo.BaseManager;
import etomo.type.AxisID;
import etomo.type.AxisType;
import etomo.type.ConstEtomoNumber;
import etomo.type.EtomoNumber;
import etomo.util.MRCHeader;
import etomo.util.InvalidParameterException;

public class TrimvolParam implements CommandDetails {
  public static final String rcsid = "$Id$";

  public static final String PARAM_ID = "Trimvol";
  public static final String XMIN = "XMin";
  public static final String XMAX = "XMax";
  public static final String YMIN = "YMin";
  public static final String YMAX = "YMax";
  public static final String ZMIN = "ZMin";
  public static final String ZMAX = "ZMax";
  public static final String CONVERT_TO_BYTES = "ConvertToBytes";
  public static final String FIXED_SCALING = "FixedScaling";
  private static final String swapYZString = "SwapYZ";
  private static final String ROTATE_X_KEY = "RotateX";
  public static final String INPUT_FILE = "InputFile";
  public static final String OUTPUT_FILE = "OutputFile";

  private static final int commandSize = 3;
  private static final String commandName = "trimvol";

  private int xMin = Integer.MIN_VALUE;
  private int xMax = Integer.MIN_VALUE;
  private int yMin = Integer.MIN_VALUE;
  private int yMax = Integer.MIN_VALUE;
  private int zMin = Integer.MIN_VALUE;
  private int zMax = Integer.MIN_VALUE;
  private final EtomoNumber scaleXMin = new EtomoNumber("ScaleXMin");
  private final EtomoNumber scaleXMax = new EtomoNumber("ScaleXMax");
  private final EtomoNumber scaleYMin = new EtomoNumber("ScaleYMin");
  private final EtomoNumber scaleYMax = new EtomoNumber("ScaleYMax");
  private boolean convertToBytes = true;
  private boolean fixedScaling = false;
  private final EtomoNumber sectionScaleMin = new EtomoNumber("SectionScaleMin");
  private final EtomoNumber sectionScaleMax = new EtomoNumber("SectionScaleMax");
  private final EtomoNumber fixedScaleMin = new EtomoNumber("FixedScaleMin");
  private final EtomoNumber fixedScaleMax = new EtomoNumber("FixedScaleMax");
  private boolean swapYZ = true;
  private boolean rotateX = false;
  private String inputFile = "";
  private String outputFile = "";
  private String[] commandArray;
  private AxisID axisID;
  private final BaseManager manager;

  public TrimvolParam(BaseManager manager) {
    this.manager = manager;
  }

  public AxisID getAxisID() {
    return axisID;
  }

  /**
   *  Insert the objects attributes into the properties object.
   */
  public void store(Properties props) {
    store(props, "");
  }

  public void store(Properties props, String prepend) {
    String group;
    prepend += PARAM_ID;
    group = prepend + ".";
    /* if (prepend == "") {
     group = PARAM_ID + ".";
     }
     else {
     group = prepend + PARAM_ID + ".";
     }*/
    props.setProperty(group + XMIN, String.valueOf(xMin));
    props.setProperty(group + XMAX, String.valueOf(xMax));
    props.setProperty(group + YMIN, String.valueOf(yMin));
    props.setProperty(group + YMAX, String.valueOf(yMax));
    props.setProperty(group + ZMIN, String.valueOf(zMin));
    props.setProperty(group + ZMAX, String.valueOf(zMax));
    props.setProperty(group + CONVERT_TO_BYTES, String.valueOf(convertToBytes));
    props.setProperty(group + FIXED_SCALING, String.valueOf(fixedScaling));
    sectionScaleMin.store(props, prepend);
    sectionScaleMax.store(props, prepend);
    fixedScaleMax.store(props, prepend);
    fixedScaleMin.store(props, prepend);
    fixedScaleMax.store(props, prepend);
    props.setProperty(group + swapYZString, String.valueOf(swapYZ));
    props.setProperty(group + ROTATE_X_KEY, String.valueOf(rotateX));
    props.setProperty(group + INPUT_FILE, inputFile);
    props.setProperty(group + OUTPUT_FILE, outputFile);
    scaleXMin.store(props, prepend);
    scaleXMax.store(props, prepend);
    scaleYMin.store(props, prepend);
    scaleYMax.store(props, prepend);
  }

  /**
   *  Get the objects attributes from the properties object.
   */
  public void load(Properties props) {
    load(props, "");
  }

  public void load(Properties props, String prepend) {
    String group;
    prepend += PARAM_ID;
    group = prepend + ".";
    /* if (prepend == "") {
     group = PARAM_ID + ".";
     }
     else {
     group = prepend + PARAM_ID + ".";
     }*/

    // Load the trimvol values if they are present, don't change the
    // current value if the property is not present
    xMin = Integer.valueOf(
        props.getProperty(group + XMIN, Integer.toString(xMin))).intValue();

    xMax = Integer.valueOf(
        props.getProperty(group + XMAX, Integer.toString(xMax))).intValue();

    yMin = Integer.valueOf(
        props.getProperty(group + YMIN, Integer.toString(yMin))).intValue();

    yMax = Integer.valueOf(
        props.getProperty(group + YMAX, Integer.toString(yMax))).intValue();

    zMin = Integer.valueOf(
        props.getProperty(group + ZMIN, Integer.toString(zMin))).intValue();

    zMax = Integer.valueOf(
        props.getProperty(group + ZMAX, Integer.toString(zMax))).intValue();

    convertToBytes = Boolean.valueOf(
        props.getProperty(group + CONVERT_TO_BYTES, Boolean
            .toString(convertToBytes))).booleanValue();

    fixedScaling = Boolean
        .valueOf(
            props.getProperty(group + FIXED_SCALING, Boolean
                .toString(fixedScaling))).booleanValue();
    sectionScaleMin.load(props, prepend);
    sectionScaleMax.load(props, prepend);
    fixedScaleMin.load(props, prepend);
    fixedScaleMax.load(props, prepend);

    swapYZ = Boolean.valueOf(
        props.getProperty(group + swapYZString, Boolean.toString(swapYZ)))
        .booleanValue();

    rotateX = Boolean.valueOf(
        props.getProperty(group + ROTATE_X_KEY, Boolean.toString(rotateX)))
        .booleanValue();

    inputFile = props.getProperty(group + INPUT_FILE, inputFile);

    outputFile = props.getProperty(group + OUTPUT_FILE, outputFile);

    scaleXMin.load(props, prepend);
    scaleXMax.load(props, prepend);
    scaleYMin.load(props, prepend);
    scaleYMax.load(props, prepend);
  }

  /**
   * @return
   */
  public boolean isConvertToBytes() {
    return convertToBytes;
  }

  /**
   * @param convertToBytes
   */
  public void setConvertToBytes(boolean convertToBytes) {
    this.convertToBytes = convertToBytes;
  }

  private void createCommand() {
    ArrayList options = genOptions();
    commandArray = new String[options.size() + commandSize];
    // Do not use the -e flag for tcsh since David's scripts handle the failure 
    // of commands and then report appropriately.  The exception to this is the
    // com scripts which require the -e flag.  RJG: 2003-11-06  
    commandArray[0] = "tcsh";
    commandArray[1] = "-f";
    commandArray[2] = BaseManager.getIMODBinPath() + commandName;
    for (int i = 0; i < options.size(); i++) {
      commandArray[i + commandSize] = (String) options.get(i);
    }
  }

  /**
   * Get the command string specified by the current state
   */
  public ArrayList genOptions() {
    ArrayList options = new ArrayList();
    options.add("-P");

    // TODO add error checking and throw an exception if the parameters have not
    // been set
    if (xMin >= 0 && xMax >= 0) {
      options.add("-x");
      options.add(String.valueOf(xMin) + "," + String.valueOf(xMax));
    }
    if (yMin >= 0 && yMax >= 0) {
      options.add("-y");
      options.add(String.valueOf(yMin) + "," + String.valueOf(yMax));
    }
    if (zMin >= 0 && zMax >= 0) {
      options.add("-z");
      options.add(String.valueOf(zMin) + "," + String.valueOf(zMax));
    }
    if (convertToBytes) {
      if (fixedScaling) {
        options.add("-c");
        options.add(fixedScaleMin.toString() + "," + fixedScaleMax.toString());

      }
      else {
        options.add("-s");
        options.add(String.valueOf(sectionScaleMin) + ","
            + String.valueOf(sectionScaleMax));
        if (!scaleXMin.isNull() && !scaleXMax.isNull()) {
          options.add("-sx");
          options.add(scaleXMin.toString() + "," + scaleXMax.toString());
        }
        if (!scaleYMin.isNull() && !scaleYMax.isNull()) {
          options.add("-sy");
          options.add(scaleYMin.toString() + "," + scaleYMax.toString());
        }
      }
    }

    if (swapYZ) {
      options.add("-yz");
    }

    if (rotateX) {
      options.add("-rx");
    }
    // TODO check to see that filenames are apropriate
    options.add(inputFile);
    options.add(outputFile);

    return options;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getFixedScaleMax() {
    return fixedScaleMax;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getFixedScaleMin() {
    return fixedScaleMin;
  }

  /**
   * @return boolean
   */
  public boolean isFixedScaling() {
    return fixedScaling;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getSectionScaleMax() {
    return sectionScaleMax;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getSectionScaleMin() {
    return sectionScaleMin;
  }

  /**
   * @return boolean
   */
  public boolean isSwapYZ() {
    return swapYZ;
  }

  public boolean isRotateX() {
    return rotateX;
  }

  /**
   * @return int
   */
  public int getXMax() {
    return xMax;
  }

  /**
   * @return int
   */
  public int getXMin() {
    return xMin;
  }

  /**
   * @return int
   */
  public int getYMax() {
    return yMax;
  }

  /**
   * @return int
   */
  public int getYMin() {
    return yMin;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getScaleXMax() {
    return scaleXMax;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getScaleXMin() {
    return scaleXMin;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getScaleYMax() {
    return scaleYMax;
  }

  /**
   * @return int
   */
  public ConstEtomoNumber getScaleYMin() {
    return scaleYMin;
  }

  /**
   * @return int
   */
  public int getZMax() {
    return zMax;
  }

  /**
   * @return int
   */
  public int getZMin() {
    return zMin;
  }

  /**
   * Sets the fixedScaleMax.
   * @param fixedScaleMax The fixedScaleMax to set
   */
  public ConstEtomoNumber setFixedScaleMax(String fixedScaleMax) {
    return this.fixedScaleMax.set(fixedScaleMax);
  }

  /**
   * Sets the fixedScaleMin.
   * @param fixedScaleMin The fixedScaleMin to set
   */
  public ConstEtomoNumber setFixedScaleMin(String fixedScaleMin) {
    return this.fixedScaleMin.set(fixedScaleMin);
  }

  /**
   * Sets the fixedScaling.
   * @param fixedScaling The fixedScaling to set
   */
  public void setFixedScaling(boolean fixedScaling) {
    fixedScaleMin.setNullIsValid(!fixedScaling);
    fixedScaleMax.setNullIsValid(!fixedScaling);
    sectionScaleMin.setNullIsValid(fixedScaling);
    sectionScaleMax.setNullIsValid(fixedScaling);
    this.fixedScaling = fixedScaling;
  }

  /**
   * Sets the scaleSectionMax.
   * @param scaleSectionMax The scaleSectionMax to set
   */
  public ConstEtomoNumber setSectionScaleMax(String scaleSectionMax) {
    return this.sectionScaleMax.set(scaleSectionMax);
  }

  /**
   * Sets the scaleSectionMin.
   * @param scaleSectionMin The scaleSectionMin to set
   */
  public ConstEtomoNumber setSectionScaleMin(String scaleSectionMin) {
    return this.sectionScaleMin.set(scaleSectionMin);
  }

  /**
   * Sets the swapYZ.
   * @param swapYZ The swapYZ to set
   */
  public void setSwapYZ(boolean swapYZ) {
    this.swapYZ = swapYZ;
  }

  public void setRotateX(boolean rotateX) {
    this.rotateX = rotateX;
  }

  /**
   * Sets the xMax.
   * @param xMax The xMax to set
   */
  public void setXMax(int xMax) {
    this.xMax = xMax;
  }

  /**
   * Sets the xMin.
   * @param xMin The xMin to set
   */
  public void setXMin(int xMin) {
    this.xMin = xMin;
  }

  /**
   * Sets the yMax.
   * @param yMax The yMax to set
   */
  public void setYMax(int yMax) {
    this.yMax = yMax;
  }

  /**
   * Sets the yMin.
   * @param yMin The yMin to set
   */
  public void setYMin(int yMin) {
    this.yMin = yMin;
  }

  /**
   * Sets the scaleXMin.
   * Converts the X value from the rubberband to a coordinate
   * @param scaleXMax
   */
  public void setScaleXMax(String scaleXMax) {
    this.scaleXMax.set(scaleXMax);
    if (!this.scaleXMax.isNull()) {
      this.scaleXMax.set(this.scaleXMax.getInt() - 1);
    }
  }

  /**
   * Sets the scaleXMin.
   * Converts the X value from the rubberband to a coordinate
   * @param scaleXMin The scaleXin to set
   */
  public void setScaleXMin(String scaleXMin) {
    this.scaleXMin.set(scaleXMin);
    if (!this.scaleXMin.isNull()) {
      this.scaleXMin.set(this.scaleXMin.getInt() - 1);
    }
  }

  /**
   * Sets the scaleYMax.
   * Converts the Y value from the rubberband to a coordinate
   * @param scaleYMax The scaleYMax to set
   */
  public void setScaleYMax(String scaleYMax) {
    this.scaleYMax.set(scaleYMax);
    if (!this.scaleYMax.isNull()) {
      this.scaleYMax.set(this.scaleYMax.getInt() - 1);
    }
  }

  /**
   * Sets the scaleYMin.
   * Converts the Y value from the rubberband to a coordinate
   * @param scaleYMin The scaleYMin to set
   */
  public void setScaleYMin(String scaleYMin) {
    this.scaleYMin.set(scaleYMin);
    if (!this.scaleYMin.isNull()) {
      this.scaleYMin.set(this.scaleYMin.getInt() - 1);
    }
  }

  /**
   * Sets the zMax.
   * @param zMax The zMax to set
   */
  public void setZMax(int zMax) {
    this.zMax = zMax;
  }

  /**
   * Sets the zMin.
   * @param zMin The zMin to set
   */
  public void setZMin(int zMin) {
    this.zMin = zMin;
  }

  /**
   * Sets the range to match the full volume
   * @param fileName The MRC iamge stack file name used to set the range
   */
  public void setDefaultRange() throws InvalidParameterException, IOException {
    setDefaultRange(inputFile);
  }

  public void setDefaultRange(String fileName)
      throws InvalidParameterException, IOException {
    //Don't override existing values
    if (xMin != Integer.MIN_VALUE) {
      return;
    }
    // Get the data size limits from the image stack
    MRCHeader mrcHeader = MRCHeader.getInstance(manager.getPropertyUserDir(),
        fileName, AxisID.ONLY);
    mrcHeader.read();

    xMin = 1;
    xMax = mrcHeader.getNColumns();
    yMin = 1;
    yMax = mrcHeader.getNRows();
    zMin = 1;
    zMax = mrcHeader.getNSections();

    // Check the swapped YZ state or rotateX state to decide which dimension to use for the 
    // section range
    if (swapYZ || rotateX) {
      sectionScaleMin.set(yMax / 3);
      sectionScaleMax.set(yMax * 2 / 3);
    }
    else {
      sectionScaleMin.set(zMax / 3);
      sectionScaleMax.set(zMax * 2 / 3);
    }
  }

  /**
   * 
   * @return
   */
  public String getInputFileName() {
    return inputFile;
  }

  /**
   * 
   * @param axisType
   * @param datasetName
   * @return
   */
  public static String getInputFileName(AxisType axisType, String datasetName) {
    if (axisType == AxisType.SINGLE_AXIS) {
      return datasetName + "_full.rec";
    }
    return "sum.rec";
  }

  /**
   * 
   * @param axisType
   * @param datasetName
   */
  public void setInputFileName(AxisType axisType, String datasetName) {
    inputFile = getInputFileName(axisType, datasetName);
  }

  /**
   * 
   * @return
   */
  public String getOutputFileName() {
    return outputFile;
  }

  public static String getOutputFileName(String datasetName) {
    return datasetName + ".rec";
  }

  /**
   * 
   * @param datasetName
   */
  public void setOutputFileName(String datasetName) {
    outputFile = datasetName + ".rec";
  }

  public boolean getBooleanValue(etomo.comscript.Fields field) {
    if (field == Fields.SWAP_YZ) {
      return swapYZ;
    }
    if (field == Fields.ROTATE_X) {
      return rotateX;
    }
    throw new IllegalArgumentException("field=" + field);
  }

  public double getDoubleValue(etomo.comscript.Fields field) {
    throw new IllegalArgumentException("field=" + field);
  }

  public Hashtable getHashtable(etomo.comscript.Fields field) {
    throw new IllegalArgumentException("field=" + field);
  }

  public String[] getCommandArray() {
    createCommand();
    return commandArray;
  }

  public String getCommandLine() {
    if (commandArray == null) {
      return "";
    }
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < commandArray.length; i++) {
      buffer.append(commandArray[i] + " ");
    }
    return buffer.toString();
  }

  public String getCommandName() {
    return commandName;
  }

  public String getCommand() {
    return commandName;
  }

  public int getIntValue(etomo.comscript.Fields field) {
    throw new IllegalArgumentException("field=" + field);
  }

  public int getCommandMode() {
    return 0;
  }

  public File getCommandOutputFile() {
    return new File(outputFile);
  }

  public static String getName() {
    return commandName;
  }

  /**
   * 
   * @param trim
   * @return
   */
  public boolean equals(TrimvolParam trim) {
    if (xMin != trim.getXMin()) {
      return false;
    }
    if (xMax != trim.getXMax()) {
      return false;
    }
    if (yMin != trim.getYMin()) {
      return false;
    }
    if (yMax != trim.getYMax()) {
      return false;
    }
    if (zMin != trim.getZMin()) {
      return false;
    }
    if (zMax != trim.getZMax()) {
      return false;
    }
    if (convertToBytes != trim.isConvertToBytes()) {
      return false;
    }
    if (fixedScaling != trim.isFixedScaling()) {
      return false;
    }
    if (!sectionScaleMin.equals(trim.sectionScaleMin)) {
      return false;
    }
    if (!sectionScaleMax.equals(trim.sectionScaleMax)) {
      return false;
    }
    if (!fixedScaleMin.equals(trim.fixedScaleMin)) {
      return false;
    }
    if (!fixedScaleMax.equals(trim.fixedScaleMax)) {
      return false;
    }
    if (swapYZ != trim.isSwapYZ()) {
      return false;
    }
    if (rotateX != trim.isRotateX()) {
      return false;
    }
    if (!inputFile.equals(trim.getInputFileName())
        && (inputFile.equals("\\S+") || trim.getInputFileName().equals("\\S+"))) {
      return false;
    }
    if (!outputFile.equals(trim.getCommandOutputFile())
        && (outputFile.equals("\\S+") || trim.getCommandOutputFile().equals(
            "\\S+"))) {
      return false;
    }
    if (!scaleXMin.equals(trim.getScaleXMin())) {
      return false;
    }
    if (!scaleXMax.equals(trim.getScaleXMax())) {
      return false;
    }
    if (!scaleYMin.equals(trim.getScaleYMin())) {
      return false;
    }
    if (!scaleYMax.equals(trim.getScaleYMax())) {
      return false;
    }
    return true;
  }

  public static final class Fields implements etomo.comscript.Fields {
    private Fields() {
    }

    public static final Fields SWAP_YZ = new Fields();
    public static final Fields ROTATE_X = new Fields();
  }
}