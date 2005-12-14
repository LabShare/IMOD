package etomo.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JPanel;

import etomo.JoinManager;
import etomo.process.ImodManager;
import etomo.type.AxisID;
import etomo.type.ConstEtomoNumber;
import etomo.type.ConstSectionTableRowData;
import etomo.type.Run3dmodMenuOptions;
import etomo.type.SectionTableRowData;
import etomo.type.SlicerAngles;
import etomo.util.DatasetFiles;

/**
 * <p>Description: Manages the fields, buttons, state, and data of one row of
 * SectionTablePanel.</p>
 * 
 * <p>Copyright: Copyright (c) 2002, 2003, 2004</p>
 *
 *<p>Organization:
 * Boulder Laboratory for 3-Dimensional Electron Microscopy of Cells (BL3DEM),
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 * 
 * <p> $Log$
 * <p> Revision 1.12  2005/11/30 21:19:01  sueh
 * <p> bug# 757 Removed getJoinXMax, YMax, and ZMax().  Added getXMax,
 * <p> YMax, and ZMax().  Get join max when the current tab is join, otherwise
 * <p> get setup max.
 * <p>
 * <p> Revision 1.11  2005/11/29 22:53:23  sueh
 * <p> bug# 757 Split final start and end into setup and join.  Split section into
 * <p> setup and join.  Display setup fields in setup and align tabs.  Display join
 * <p> fields in join tab.  Changed configure to setInUse().
 * <p>
 * <p> Revision 1.10  2005/11/14 22:19:39  sueh
 * <p> bug# 762 Made buttonAction() protected.
 * <p>
 * <p> Revision 1.9  2005/08/10 20:46:49  sueh
 * <p> bug# 711 Removed MultiLineToggleButton.  Making toggling an attribute
 * <p> of MultiLineButton.
 * <p>
 * <p> Revision 1.8  2005/07/29 19:47:55  sueh
 * <p> bug# 692 Changed ConstEtomoNumber.getInteger() to getInt.
 * <p>
 * <p> Revision 1.7  2005/07/01 21:22:48  sueh
 * <p> bug# 619 Changed FieldCell.getText() and setText() to getValue() and
 * <p> setValue().
 * <p>
 * <p> Revision 1.6  2005/04/25 21:38:33  sueh
 * <p> bug# 615 Passing the axis where a command originates to the message
 * <p> functions so that the message will be popped up in the correct window.
 * <p> This requires adding AxisID to many objects.
 * <p>
 * <p> Revision 1.5  2005/01/26 00:06:02  sueh
 * <p> Removing ConstEtomoNumber.displayDefault.  To get the default to
 * <p> display, set displayValue and default the same.
 * <p>
 * <p> Revision 1.4  2004/11/24 01:05:05  sueh
 * <p> bug# 520 removed invalidReason.  Simplified retrieveData().
 * <p>
 * <p> Revision 1.3  2004/11/23 22:37:21  sueh
 * <p> bug# 520 retrieveData(): Display error message if a numeric field is
 * <p> incorrect.   Retrieve all correct fields.  Since retrieveData() is called by
 * <p> different functions, pass flag deciding whether to display an error
 * <p> message.
 * <p>
 * <p> Revision 1.2  2004/11/20 00:03:43  sueh
 * <p> bug# 520 merging Etomo_3-4-6_JOIN branch to head.
 * <p>
 * <p> Revision 1.1.2.20  2004/11/19 00:28:13  sueh
 * <p> bug# 520 Added equals function to check whether the screen fields have
 * <p> changed since meta data was updated.  Added equalsSample to check
 * <p> whether the fields used to create the sample have changed.
 * <p>
 * <p> Revision 1.1.2.19  2004/11/16 02:29:23  sueh
 * <p> bug# 520 Replacing EtomoSimpleType, EtomoInteger, EtomoDouble,
 * <p> EtomoFloat, and EtomoLong with EtomoNumber.
 * <p>
 * <p> Revision 1.1.2.18  2004/11/15 22:26:27  sueh
 * <p> bug# 520 Added setMode().  Moved enabling and disabling to setMode().
 * <p>
 * <p> Revision 1.1.2.17  2004/11/09 16:20:41  sueh
 * <p> bug# 520 Correcting weight distribution for join tab.  Top header is 2, other
 * <p> lines are 1.
 * <p>
 * <p> Revision 1.1.2.16  2004/11/08 22:30:12  sueh
 * <p> bug# 520 Tried setting constraints.weighty to zero to prevent the table
 * <p> from expanding.
 * <p>
 * <p> Revision 1.1.2.15  2004/10/30 02:38:38  sueh
 * <p> bug# 520 Converted rotation angles to EtomoSimpleType.
 * <p>
 * <p> Revision 1.1.2.14  2004/10/29 22:18:33  sueh
 * <p> bug# 520 Added imodRotIndex to manage the 3dmod associated with the
 * <p> .rot file created from section when rotation angles are specified.
 * <p>
 * <p> Revision 1.1.2.13  2004/10/25 23:16:49  sueh
 * <p> bug# 520 Changed table in Align tab:  Removed Sample Slices.  Added
 * <p> Slices in Sample.  Added Chunk table.  Also add xMax and yMax.
 * <p>
 * <p> Revision 1.1.2.12  2004/10/22 21:12:58  sueh
 * <p> bug# 520 Changed SectionTableRow.sampleSampleTop() to
 * <p> setSampleTopNumberSlices().  Removed getSampleBottom().  Fixed
 * <p> displayCurTab().
 * <p>
 * <p> Revision 1.1.2.11  2004/10/22 16:42:41  sueh
 * <p> bug# 520 Fixed displayCurTab: getting sample bottom size from current
 * <p> row.  Changed Chunk to a header.
 * <p>
 * <p> Revision 1.1.2.10  2004/10/22 03:28:33  sueh
 * <p> bug# 520 Added Chunk, Reference section, and Current section to Align
 * <p> tab.
 * <p>
 * <p> Revision 1.1.2.9  2004/10/15 00:52:26  sueh
 * <p> bug# 520 Added toString().
 * <p>
 * <p> Revision 1.1.2.8  2004/10/13 23:15:36  sueh
 * <p> bug# 520 Allowed the ui components of the row to be removed and re-
 * <p> added.  This way the table can look different on different tabs.  Set the
 * <p> state of fields based on the tab.
 * <p>
 * <p> Revision 1.1.2.7  2004/10/08 16:40:22  sueh
 * <p> bug# Using setRowNumber() to change the status of sample slice
 * <p> numbers.  Fixed retrieveData().  Changed getData() to return false when
 * <p> retrieveData() fails.  Added getInvalidReason().
 * <p>
 * <p> Revision 1.1.2.6  2004/10/06 02:31:17  sueh
 * <p> bug# 520 Added Z max
 * <p>
 * <p> Revision 1.1.2.5  2004/10/01 20:07:11  sueh
 * <p> bug# 520 Converted text fields to FieldCells.  Removed color control
 * <p> (done in FieldCell).
 * <p>
 * <p> Revision 1.1.2.4  2004/09/29 19:45:01  sueh
 * <p> bug# 520 View part of the section table row.  Contains section table row
 * <p> screen fields.  Added SectionTableRowData member variable so hold,
 * <p> store, and compare data from the screen.  Added displayData() to display
 * <p> data on the screen.  Added retrieveData() to retrieve data from the
 * <p> screen.  Added an equals function.  Disabled the sectionFile field.
 * <p>
 * <p> Revision 1.1.2.3  2004/09/22 22:17:30  sueh
 * <p> bug# 520 Added set rotation angle functions.  When highlighting, tell the
 * <p> panel when the highlight is being turned off as well as when its being
 * <p> turned on (for button enable/disable).
 * <p>
 * <p> Revision 1.1.2.2  2004/09/21 18:12:04  sueh
 * <p> bug# 520 Added remove(), to remove the row from the table.  Added
 * <p> imodIndex - the vector index of the 3dmod in ImodManager.  Added
 * <p> create(), to create the row in the table for the first time.  Added add(), to
 * <p> added the rows back into the table.
 * <p>
 * <p> Revision 1.1.2.1  2004/09/17 21:48:41  sueh
 * <p> bug# 520 Handles row display, state, and data.  Can highlight all of its
 * <p> fields.  Can expand the section field
 * <p> </p>
 */
public class SectionTableRow {
  public static final String rcsid = "$Id$";

  private final FieldCell setupSection = new FieldCell();
  private final FieldCell joinSection = new FieldCell();
  private final FieldCell sampleBottomStart = new FieldCell();
  private final FieldCell sampleBottomEnd = new FieldCell();
  private final FieldCell sampleTopStart = new FieldCell();
  private final FieldCell sampleTopEnd = new FieldCell();
  private final FieldCell slicesInSample = new FieldCell();
  private final HeaderCell currentChunk = new HeaderCell();
  private final FieldCell referenceSection = new FieldCell();
  private final FieldCell currentSection = new FieldCell();
  private final FieldCell setupFinalStart = new FieldCell();
  private final FieldCell setupFinalEnd = new FieldCell();
  private final FieldCell joinFinalStart = new FieldCell();
  private final FieldCell joinFinalEnd = new FieldCell();
  private final FieldCell rotationAngleX = new FieldCell();
  private final FieldCell rotationAngleY = new FieldCell();
  private final FieldCell rotationAngleZ = new FieldCell();
  private final SectionTableRowActionListener actionListener = new SectionTableRowActionListener(
      this);

  private final JoinManager manager;
  private final SectionTablePanel table;
  private final MultiLineButton highlighterButton;

  private SectionTableRowData data;
  private HeaderCell rowNumber;

  private int imodIndex = -1;
  private int imodRotIndex = -1;
  private boolean sectionExpanded = false;
  private boolean valid = true;

  /**
   * Create colors, fields, and buttons.  Add the row to the table
   * @param table
   * @param rowNumber
   */
  public SectionTableRow(JoinManager manager, SectionTablePanel table,
      int rowNumber, File tomogram, boolean sectionExpanded) {
    this(manager, table, sectionExpanded);
    data = new SectionTableRowData(manager, rowNumber);
    data.setSetupSection(tomogram);
    init();
  }

  public SectionTableRow(JoinManager manager, SectionTablePanel table,
      SectionTableRowData data, boolean sectionExpanded) {
    this(manager, table, sectionExpanded);
    this.data = new SectionTableRowData(manager, data);
    this.sectionExpanded = sectionExpanded;
    init();
  }

  private SectionTableRow(JoinManager manager, SectionTablePanel table,
      boolean sectionExpanded) {
    this.manager = manager;
    this.table = table;
    this.sectionExpanded = sectionExpanded;
    highlighterButton = table.createToggleButton("=>",
        FixedDim.highlighterWidth);
    //configure
    highlighterButton.addActionListener(actionListener);
    setupSection.setEnabled(false);
    joinSection.setEnabled(false);
    slicesInSample.setEnabled(false);
    referenceSection.setEnabled(false);
    currentSection.setEnabled(false);
  }

  public String toString() {
    return getClass().getName() + "[" + paramString() + "]";
  }

  protected String paramString() {
    return "setupSection=" + setupSection + ",joinSection=" + joinSection
        + ",\nsampleBottomStart=" + sampleBottomStart + ",sampleBottomEnd="
        + sampleBottomEnd + ",\nsampleTopStart=" + sampleTopStart
        + ",\nsampleTopEnd=" + sampleTopEnd + ",slicesInSample="
        + slicesInSample + ",\ncurrentChunk=" + currentChunk
        + ",\nreferenceSection=" + referenceSection + ",currentSection="
        + currentSection + ",\nsetupFinalStart=" + setupFinalStart
        + ",\nsetupFinalEnd=" + setupFinalEnd + ",joinFinalStart="
        + joinFinalStart + ",\njoinFinalEnd=" + joinFinalEnd
        + ",\nrotationAngleX=" + rotationAngleX + ",rotationAngleY="
        + rotationAngleY + ",\nrotationAngleZ=" + rotationAngleZ
        + ",\nhighlighterButton=" + highlighterButton + ",rowNumber="
        + rowNumber + ",\nimodIndex=" + imodIndex + ",imodRotIndex="
        + imodRotIndex + ",\nsectionExpanded=" + sectionExpanded + ",valid="
        + valid + "," + super.toString();
  }

  /**
   * construction work that depends on data
   */
  private final void init() {
    rowNumber = new HeaderCell(data.getRowNumber().toString(),
        FixedDim.rowNumberWidth);
    displayData();
  }

  void setInUse() {
    if (table.isSetupTab()) {
      int rowNumber = data.getRowNumber().getInt();
      boolean bottomInUse = rowNumber > 1;
      boolean topInUse = rowNumber < table.getTableSize();
      boolean finalInuse = table.isJoinTab();
      sampleBottomStart.setInUse(bottomInUse);
      sampleBottomEnd.setInUse(bottomInUse);
      sampleTopStart.setInUse(topInUse);
      sampleTopEnd.setInUse(topInUse);
      setupFinalStart.setInUse(finalInuse);
      setupFinalEnd.setInUse(finalInuse);
    }
    else if (table.isJoinTab()) {
      joinFinalStart.setInUse(true);
      joinFinalEnd.setInUse(true);
    }
  }

  final boolean isRotated() {
    return DatasetFiles.isRotatedTomogram(data.getJoinSection());
  }

  void remove() {
    rowNumber.remove();
    table.removeCell(highlighterButton.getComponent());
    setupSection.remove();
    sampleBottomStart.remove();
    sampleBottomEnd.remove();
    sampleTopStart.remove();
    sampleTopEnd.remove();
    setupFinalStart.remove();
    setupFinalEnd.remove();
    joinFinalStart.remove();
    joinFinalEnd.remove();
    rotationAngleX.remove();
    rotationAngleY.remove();
    rotationAngleZ.remove();
    //align
    slicesInSample.remove();
    currentChunk.remove();
    referenceSection.remove();
    currentSection.remove();
    //join
    joinSection.remove();
    joinFinalStart.remove();
    joinFinalEnd.remove();
  }

  final void removeImod() {
    manager.imodRemove(ImodManager.TOMOGRAM_KEY, imodIndex);
    manager.imodRemove(ImodManager.ROT_TOMOGRAM_KEY, imodRotIndex);
  }

  void setMode(int mode) {
    switch (mode) {
    case JoinDialog.SAMPLE_PRODUCED_MODE:
      sampleBottomStart.setEnabled(false);
      sampleBottomEnd.setEnabled(false);
      sampleTopStart.setEnabled(false);
      sampleTopEnd.setEnabled(false);
      rotationAngleX.setEnabled(false);
      rotationAngleY.setEnabled(false);
      rotationAngleZ.setEnabled(false);
      return;
    case JoinDialog.SETUP_MODE:
    case JoinDialog.SAMPLE_NOT_PRODUCED_MODE:
    case JoinDialog.CHANGING_SAMPLE_MODE:
      sampleBottomStart.setEnabled(true);
      sampleBottomEnd.setEnabled(true);
      sampleTopStart.setEnabled(true);
      sampleTopEnd.setEnabled(true);
      rotationAngleX.setEnabled(true);
      rotationAngleY.setEnabled(true);
      rotationAngleZ.setEnabled(true);
      return;
    default:
      throw new IllegalStateException("mode=" + mode);
    }
  }

  int displayCurTab(JPanel panel, int prevSlice) {
    remove();
    add(panel);
    //Set align display only fields
    if (table.isAlignTab()) {
      int start;
      int chunkSize = data.getChunkSize(table.getTableSize()).getInt();
      if (chunkSize > 0) {
        start = prevSlice + 1;
        prevSlice += chunkSize;
        slicesInSample.setValue(Integer.toString(start) + " - "
            + Integer.toString(prevSlice));
      }
      else {
        referenceSection.setValue("");
      }
    }
    return prevSlice;
  }

  int displayCurTabChunkTable(JPanel panel, int prevSlice,
      int nextSampleBottomNumberSlices) {
    if (table.isAlignTab()) {
      if (nextSampleBottomNumberSlices == -1) {
        currentChunk.setText("");
        referenceSection.setValue("");
        currentSection.setValue("");
      }
      else {
        ConstEtomoNumber rowNumber = data.getRowNumber();
        currentChunk.setText(Integer.toString(rowNumber.getInt() + 1));
        int start;
        int sampleTopNumberSlices = data.getSampleTopNumberSlices();
        if (sampleTopNumberSlices > 0) {
          start = prevSlice + 1;
          prevSlice += sampleTopNumberSlices;
          referenceSection.setValue(Integer.toString(start) + " - "
              + Integer.toString(prevSlice));
        }
        else {
          referenceSection.setValue("");
        }

        if (nextSampleBottomNumberSlices > 0) {
          start = prevSlice + 1;
          prevSlice += nextSampleBottomNumberSlices;
          currentSection.setValue(Integer.toString(start) + " - "
              + Integer.toString(prevSlice));
        }
        else {
          currentSection.setValue("");
        }
      }
    }
    return prevSlice;
  }

  void add(JPanel panel) {
    if (table.isSetupTab()) {
      addSetup(panel);
    }
    else if (table.isAlignTab()) {
      addAlign(panel);
    }
    else if (table.isJoinTab()) {
      addJoin(panel);
    }
  }

  private void addSetup(JPanel panel) {
    GridBagLayout layout = table.getTableLayout();
    GridBagConstraints constraints = table.getTableConstraints();
    constraints.weightx = 0.0;
    constraints.weighty = 0.0;
    constraints.gridwidth = 1;
    rowNumber.add(panel, layout, constraints);
    table.addCell(highlighterButton.getComponent());
    constraints.gridwidth = 2;
    setupSection.add(panel, layout, constraints);
    constraints.gridwidth = 1;
    sampleBottomStart.add(panel, layout, constraints);
    sampleBottomEnd.add(panel, layout, constraints);
    sampleTopStart.add(panel, layout, constraints);
    sampleTopEnd.add(panel, layout, constraints);
    setupFinalStart.add(panel, layout, constraints);
    setupFinalEnd.add(panel, layout, constraints);
    rotationAngleX.add(panel, layout, constraints);
    rotationAngleY.add(panel, layout, constraints);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    rotationAngleZ.add(panel, layout, constraints);
  }

  private void addAlign(JPanel panel) {
    GridBagLayout layout = table.getTableLayout();
    GridBagConstraints constraints = table.getTableConstraints();
    constraints.weightx = 0.0;
    constraints.weighty = 0.0;
    constraints.gridwidth = 1;
    rowNumber.add(panel, layout, constraints);
    constraints.gridwidth = 2;
    setupSection.add(panel, layout, constraints);
    constraints.gridwidth = 1;
    slicesInSample.add(panel, layout, constraints);
    currentChunk.add(panel, layout, constraints);
    referenceSection.add(panel, layout, constraints);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    currentSection.add(panel, layout, constraints);
  }

  private void addJoin(JPanel panel) {
    GridBagLayout layout = table.getTableLayout();
    GridBagConstraints constraints = table.getTableConstraints();
    constraints.weightx = 0.0;
    constraints.weighty = 0.0;
    constraints.gridwidth = 1;
    rowNumber.add(panel, layout, constraints);
    table.addCell(highlighterButton.getComponent());
    constraints.gridwidth = 2;
    joinSection.add(panel, layout, constraints);
    constraints.gridwidth = 1;
    joinFinalStart.add(panel, layout, constraints);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    joinFinalEnd.add(panel, layout, constraints);
  }

  /**
   * Copy field from data to the screen.
   * Copy all fields stored in data that can be displayed on the screen
   *
   */
  private void displayData() {
    rowNumber.setText(data.getRowNumber().toString());
    setSectionText();
    sampleBottomStart.setValue(data.getSampleBottomStart().toString());
    sampleBottomEnd.setValue(data.getSampleBottomEnd().toString());
    sampleTopStart.setValue(data.getSampleTopStart().toString());
    sampleTopEnd.setValue(data.getSampleTopEnd().toString());
    setupFinalStart.setValue(data.getSetupFinalStart().toString());
    setupFinalEnd.setValue(data.getSetupFinalEnd().toString());
    joinFinalStart.setValue(data.getJoinFinalStart().toString());
    joinFinalEnd.setValue(data.getJoinFinalEnd().toString());
    rotationAngleX.setValue(data.getRotationAngleX().toString());
    rotationAngleY.setValue(data.getRotationAngleY().toString());
    rotationAngleZ.setValue(data.getRotationAngleZ().toString());
  }

  /**
   * Copy data from screen to data.
   * Copies all fields that can be modified on the screen and are stored in data
   * Checks for errors.  Prints a error message based on the first error found
   * and returns false.
   * Tries to retrieve all values, regardless of errors.
   * @return
   */
  private boolean retrieveData(boolean displayErrorMessage) {
    valid = true;
    String errorTitle = "Invalid number in row " + rowNumber.getText();
    if (!data.setSampleBottomStart(sampleBottomStart.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setSampleBottomEnd(sampleBottomEnd.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setSampleTopStart(sampleTopStart.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setSampleTopEnd(sampleTopEnd.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setSetupFinalStart(setupFinalStart.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setSetupFinalEnd(setupFinalEnd.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setJoinFinalStart(joinFinalStart.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setJoinFinalEnd(joinFinalEnd.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setRotationAngleX(rotationAngleX.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setRotationAngleY(rotationAngleY.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    if (!data.setRotationAngleZ(rotationAngleZ.getValue()).isValid(
        displayErrorMessage && valid, errorTitle, AxisID.ONLY)) {
      valid = false;
    }
    return valid;
  }

  boolean isValid() {
    return valid;
  }

  /**
   * Toggle the setup section between absolute path when expand is true, and
   * name when expand is false.
   * @param expand
   */
  void expandSection(boolean expand) {
    sectionExpanded = expand;
    setSectionText();
  }

  private void setSectionText() {
    File section = data.getSetupSection();
    if (section != null) {
      if (sectionExpanded) {
        setupSection.setValue(section.getAbsolutePath());
      }
      else {
        setupSection.setValue(section.getName());
      }
    }
    section = data.getJoinSection();
    if (section == null) {
      return;
    }
    if (sectionExpanded) {
      joinSection.setValue(section.getAbsolutePath());
    }
    else {
      joinSection.setValue(section.getName());
    }
  }

  void setRowNumber(int rowNumber, boolean maxRow) {
    data.setRowNumber(rowNumber);
    this.rowNumber.setText("<html><b>" + Integer.toString(rowNumber) + "</b>");
  }

  void setImodIndex(int imodIndex) {
    this.imodIndex = imodIndex;
  }

  void setImodRotIndex(int imodRotIndex) {
    this.imodRotIndex = imodRotIndex;
  }

  void setRotationAngles(SlicerAngles slicerAngles) {
    rotationAngleX.setValue(slicerAngles.getXText());
    rotationAngleY.setValue(slicerAngles.getYText());
    rotationAngleZ.setValue(slicerAngles.getZText());
  }

  /**
   * Toggle the highlighter button based on the highlighted parameter.
   * Change the foreground and background for all the fields in the row.  Do
   * nothing of the highlighter button matches the highlight parameter.
   * @param highlighted
   */
  void setHighlight(boolean highlight) {
    if (highlight == highlighterButton.isSelected()) {
      return;
    }
    highlighterButton.setSelected(highlight);
    highlight();
  }

  /**
   * Change the foreground and background for all the fields in the row based
   * on whether the highlighter button is selected.
   *
   */
  private void highlight() {
    boolean highlight = highlighterButton.isSelected();
    setupSection.setHighlighted(highlight);
    joinSection.setHighlighted(highlight);
    sampleBottomStart.setHighlighted(highlight);
    sampleBottomEnd.setHighlighted(highlight);
    sampleTopStart.setHighlighted(highlight);
    sampleTopEnd.setHighlighted(highlight);
    slicesInSample.setHighlighted(highlight);
    setupFinalStart.setHighlighted(highlight);
    setupFinalEnd.setHighlighted(highlight);
    joinFinalStart.setHighlighted(highlight);
    joinFinalEnd.setHighlighted(highlight);
    rotationAngleX.setHighlighted(highlight);
    rotationAngleY.setHighlighted(highlight);
    rotationAngleZ.setHighlighted(highlight);
  }

  private void highlighterButtonAction() {
    table.msgHighlighting(data.getRowIndex(), highlighterButton.isSelected());
    highlight();
  }

  boolean isHighlighted() {
    return highlighterButton.isSelected();
  }

  File getSetupSectionFile() {
    return data.getSetupSection();
  }

  File getJoinSectionFile() {
    return data.getJoinSection();
  }

  String getSetupSectionText() {
    return setupSection.getValue();
  }

  int getXMax() {
    if (table.isJoinTab()) {
      return data.getJoinXMax();
    }
    return data.getSetupXMax();
  }

  int getYMax() {
    if (table.isJoinTab()) {
      return data.getJoinYMax();
    }
    return data.getSetupYMax();
  }

  int getZMax() {
    if (table.isJoinTab()) {
      return data.getJoinZMax();
    }
    return data.getSetupZMax();
  }

  int getSampleBottomNumberSlices() {
    return data.getSampleBottomNumberSlices();
  }

  ConstSectionTableRowData getData() {
    retrieveData(true);
    return data;
  }

  String getInvalidReason() {
    return data.getInvalidReason();
  }

  public boolean equalsSetupSection(File section) {
    if (data.getSetupSection().getAbsolutePath().equals(
        section.getAbsolutePath())) {
      return true;
    }
    return false;
  }

  public boolean equalsJoinSection(File section) {
    if (data.getJoinSection().getAbsolutePath().equals(
        section.getAbsolutePath())) {
      return true;
    }
    return false;
  }

  public boolean equals(SectionTableRow that) {
    retrieveData(false);
    return data.equals(that.data);
  }

  public boolean equals(ConstSectionTableRowData thatData) {
    retrieveData(false);
    return data.equals(thatData);
  }

  public boolean equalsSample(ConstSectionTableRowData thatData) {
    retrieveData(false);
    return data.equalsSample(thatData);
  }

  /**
   * Handle button actions
   * @param event
   */
  protected void buttonAction(ActionEvent event) {
    String command = event.getActionCommand();

    if (command.equals(highlighterButton.getActionCommand())) {
      highlighterButtonAction();
    }
  }

  final void imodOpenSetupSectionFile(int binning) {
    imodIndex = manager.imodOpen(ImodManager.TOMOGRAM_KEY, imodIndex, data
        .getSetupSection(), binning, new Run3dmodMenuOptions());
  }

  final void imodOpenJoinSectionFile(int binning) {
    File joinSection = data.getJoinSection();
    if (DatasetFiles.isRotatedTomogram(joinSection)) {
      imodRotIndex = manager.imodOpen(ImodManager.ROT_TOMOGRAM_KEY,
          imodRotIndex, joinSection, binning, new Run3dmodMenuOptions());
    }
    else {
      imodIndex = manager.imodOpen(ImodManager.TOMOGRAM_KEY, imodIndex,
          joinSection, binning, new Run3dmodMenuOptions());
    }
  }

  final boolean imodGetAngles() {
    if (imodIndex == -1) {
      UIHarness.INSTANCE.openMessageDialog(
          "Open in 3dmod and use the Slicer to change the angles.",
          "Open 3dmod", AxisID.ONLY);
      return false;
    }
    SlicerAngles slicerAngles = manager.imodGetSlicerAngles(
        ImodManager.TOMOGRAM_KEY, imodIndex);
    if (slicerAngles == null || !slicerAngles.isComplete()) {
      return false;
    }
    setRotationAngles(slicerAngles);
    return true;
  }

  final void synchronizeSetupToJoin() {
    retrieveData(true);
    data.synchronizeSetupToJoin();
    displayData();
  }

  final void synchronizeJoinToSetup() {
    retrieveData(true);
    data.synchronizeJoinToSetup();
    displayData();
  }

  /**
   *  Action listener for SectionTableRow
   */
  class SectionTableRowActionListener implements ActionListener {

    SectionTableRow adaptee;

    SectionTableRowActionListener(SectionTableRow adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent event) {
      adaptee.buttonAction(event);
    }
  }
}