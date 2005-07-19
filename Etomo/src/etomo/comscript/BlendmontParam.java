package etomo.comscript;

import java.io.File;

import etomo.EtomoDirector;
import etomo.type.AxisID;
import etomo.type.ConstEtomoNumber;
import etomo.type.EtomoBoolean2;
import etomo.type.EtomoNumber;
import etomo.type.ScriptParameter;

/**
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright (c) 2005</p>
 *
 *<p>Organization:
 * Boulder Laboratory for 3-Dimensional Electron Microscopy of Cells (BL3DEM),
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 */
public class BlendmontParam implements CommandParam {
  public static final String rcsid = "$Id$";

  public static final String GOTO_LABEL = "doblend";
  public static final String COMMAND_NAME = "blendmont";
  public static final int XCORR_MODE = -1;
  public static final int PREBLEND_MODE = -2;
  public static final int BLEND_MODE = -3;
  public static final int UNDISTORT_MODE = -4;
  public static final int WHOLE_TOMOGRAM_SAMPLE_MODE = -5;
  public static final int LINEAR_INTERPOLATION_ORDER = 1;
  public static final String OUTPUT_FILE_EXTENSION = ".ali";
  public static final String DISTORTION_CORRECTED_STACK_EXTENSION = ".dcst";
  public static final String BLENDMONT_STACK_EXTENSION = ".bl";

  public static final String IMAGE_OUTPUT_FILE_KEY = "ImageOutputFile";

  private AxisID axisID;
  private String datasetName;
  private EtomoBoolean2 readInXcorrs;
  private EtomoBoolean2 oldEdgeFunctions;
  private ScriptParameter interpolationOrder;
  private EtomoBoolean2 justUndistort;
  private String imageOutputFile;
  private int mode = XCORR_MODE;
  private ScriptParameter binByFactor;

  public BlendmontParam(String datasetName, AxisID axisID) {
    this(datasetName, axisID, XCORR_MODE);
  }

  public BlendmontParam(String datasetName, AxisID axisID, int mode) {
    this.datasetName = datasetName;
    this.axisID = axisID;
    this.mode = mode;
    readInXcorrs = new EtomoBoolean2("ReadInXcorrs");
    readInXcorrs.setDisplayAsInteger(true);
    oldEdgeFunctions = new EtomoBoolean2("OldEdgeFunctions");
    oldEdgeFunctions.setDisplayAsInteger(true);
    interpolationOrder = new ScriptParameter(EtomoNumber.INTEGER_TYPE,
        "InterpolationOrder");
    justUndistort = new EtomoBoolean2("JustUndistort");
    imageOutputFile = null;
    binByFactor = new ScriptParameter(EtomoNumber.INTEGER_TYPE, "BinByFactor");
    // Only explcitly write out the binning if its value is something other than
    // the default of 1 to keep from cluttering up the com script  
    binByFactor.setDefault(1);
  }

  public void parseComScriptCommand(ComScriptCommand scriptCommand)
      throws BadComScriptException, InvalidParameterException,
      FortranInputSyntaxException {
    reset();
    readInXcorrs.parse(scriptCommand);
    oldEdgeFunctions.parse(scriptCommand);
    interpolationOrder.parse(scriptCommand);
    justUndistort.parse(scriptCommand);
    imageOutputFile = scriptCommand.getValue(IMAGE_OUTPUT_FILE_KEY);
    binByFactor.parse(scriptCommand);
  }

  public void updateComScriptCommand(ComScriptCommand scriptCommand)
      throws BadComScriptException {
    readInXcorrs.updateComScript(scriptCommand);
    oldEdgeFunctions.updateComScript(scriptCommand);
    interpolationOrder.updateComScript(scriptCommand);
    justUndistort.updateComScript(scriptCommand);
    scriptCommand.setValue(IMAGE_OUTPUT_FILE_KEY, imageOutputFile);
    binByFactor.updateComScript(scriptCommand);
  }

  private void reset() {
    readInXcorrs.reset();
    oldEdgeFunctions.reset();
    interpolationOrder.reset();
    justUndistort.reset();
    imageOutputFile = null;
    binByFactor.reset();
  }

  public void initializeDefaults() {
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public int getMode() {
    return mode;
  }

  /**
   * Sets the state of blendmont parameters based on the .edc and .xef files
   * @return true if blendmont needs to be run, false if blendmont does not need
   * to be run
   */
  public boolean setBlendmontState() {
    if (mode == UNDISTORT_MODE) {
      imageOutputFile = datasetName + axisID.getExtension()
          + DISTORTION_CORRECTED_STACK_EXTENSION;
      justUndistort.set(true);
      return true;
    }
    else {
      justUndistort.set(false);
      if (mode == XCORR_MODE) {
        imageOutputFile = datasetName + axisID.getExtension()
        + BLENDMONT_STACK_EXTENSION;
      }
      else if (mode == PREBLEND_MODE) {
        imageOutputFile = datasetName + axisID.getExtension()
        + ".preali";
      }
      else if (mode == BLEND_MODE || mode == WHOLE_TOMOGRAM_SAMPLE_MODE) {
        imageOutputFile = datasetName + axisID.getExtension()
        + ".ali";
      }
    }
    File ecdFile = new File(EtomoDirector.getInstance()
        .getCurrentPropertyUserDir(), datasetName + axisID.getExtension()
        + ".ecd");
    File xefFile = new File(EtomoDirector.getInstance()
        .getCurrentPropertyUserDir(), datasetName + axisID.getExtension()
        + ".xef");
    File yefFile = new File(EtomoDirector.getInstance()
        .getCurrentPropertyUserDir(), datasetName + axisID.getExtension()
        + ".yef");
    File stackFile = new File(EtomoDirector.getInstance()
        .getCurrentPropertyUserDir(), datasetName + axisID.getExtension()
        + ".st");
    File blendFile = new File(EtomoDirector.getInstance()
        .getCurrentPropertyUserDir(), datasetName + axisID.getExtension()
        + BLENDMONT_STACK_EXTENSION);
    //Read in xcorr output if it exists.  Turn on for preblend and blend.
    readInXcorrs.set(mode == PREBLEND_MODE || mode == BLEND_MODE
        || mode == WHOLE_TOMOGRAM_SAMPLE_MODE || ecdFile.exists());
    //Use existing edge functions, if they are up to date.  Turn on for blend.
    oldEdgeFunctions.set(mode == BLEND_MODE
        || mode == WHOLE_TOMOGRAM_SAMPLE_MODE
        || (xefFile.exists() && yefFile.exists()
            && ecdFile.lastModified() <= xefFile.lastModified() && ecdFile
            .lastModified() <= yefFile.lastModified()));
    //If xcorr output exists and the edge functions are up to date, then don't
    //run blendmont, as long as the blendmont output is more recent then the
    //stack.
    if (readInXcorrs.is() && oldEdgeFunctions.is() && blendFile.exists()
        && stackFile.lastModified() < blendFile.lastModified()) {
      return false;
    }
    else {
      return true;
    }
  }

  public static String getCommandFileName(int mode) {
    switch (mode) {
    case PREBLEND_MODE:
      return "preblend";
    case BLEND_MODE:
      return "blend";
    case UNDISTORT_MODE:
      return "undistort";
    case XCORR_MODE:
      return "xcorr";
    case WHOLE_TOMOGRAM_SAMPLE_MODE:
      return "blend";
    default:
      throw new IllegalArgumentException("mode=" + mode);
    }
  }

  public String getCommandFileName() {
    return getCommandFileName(mode);
  }

  public static File getDistortionCorrectedFile(String workingDir,
      String datasetName, AxisID axisID) {
    return new File(workingDir, datasetName + axisID.getExtension()
        + DISTORTION_CORRECTED_STACK_EXTENSION);
  }

  public boolean isLinearInterpolation() {
    return interpolationOrder.getInteger() == LINEAR_INTERPOLATION_ORDER;
  }

  public void setLinearInterpolation(boolean linearInterpolation) {
    if (linearInterpolation) {
      interpolationOrder.set(LINEAR_INTERPOLATION_ORDER);
    }
    else {
      interpolationOrder.reset();
    }
  }

public final void setBinByFactor(int binByFactor) {
    this.binByFactor.set(binByFactor);
  }  public final ConstEtomoNumber getBinByFactor() {
    return binByFactor;
  }
}
/**
 * <p> $Log$
 * <p> Revision 1.9  2005/07/18 22:02:17  sueh
 * <p> bug# 688 Setting imageOutputFile and justUndistort when mode is not
 * <p> undistort.
 * <p>
 * <p> Revision 1.8  2005/07/14 21:58:42  sueh
 * <p> bug# 626 Added binByFactor, defaulted to 1.
 * <p> Added WHOLE_TOMOGRAM_SAMPLE_MODE.
 * <p>
 * <p> Revision 1.7  2005/05/09 22:43:39  sueh
 * <p> bug# 658 Changed ScriptParameter. and EtomoBoolean2.setInScript() to
 * <p> updateComScript() to standardize function names.
 * <p>
 * <p> Revision 1.6  2005/04/07 21:49:05  sueh
 * <p> bug# 626 Added undistort mode to write a blendmont command to
 * <p> undistort.com.  Undistort mode is used to create a new stack with
 * <p> distortion correction.
 * <p>
 * <p> Revision 1.5  2005/03/29 19:52:20  sueh
 * <p> bug# 623 Adding the extension for the output file created by blendmont.
 * <p>
 * <p> Revision 1.4  2005/03/11 01:32:20  sueh
 * <p> bug# 533 Added interpolationOrder
 * <p>
 * <p> Revision 1.3  2005/03/09 17:59:41  sueh
 * <p> bug# 533 Added a mode for blendmont in the blend.com script.  In this
 * <p> mode readInXcorrs and oldEdgeFunctions are always true.
 * <p>
 * <p> Revision 1.2  2005/03/08 00:44:12  sueh
 * <p> bug# 533 Added a mode because the rules for setting readInXcorrs are
 * <p> different in xcorr and preblend.  Changed set...State functions to set
 * <p> readInXcorrs correctly.
 * <p>
 * <p> Revision 1.1  2005/03/04 00:07:03  sueh
 * <p> bug# 533 Param object for the blendmont command.
 * <p> </p>
 */