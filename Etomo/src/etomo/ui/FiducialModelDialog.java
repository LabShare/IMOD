package etomo.ui;

import java.awt.Component;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import etomo.ApplicationManager;
import etomo.process.ImodManager;
import etomo.type.AxisID;
import etomo.type.AxisType;
import etomo.type.BaseScreenState;
import etomo.type.ConstEtomoNumber;
import etomo.type.ConstMetaData;
import etomo.type.DialogType;
import etomo.type.InvalidEtomoNumberException;
import etomo.type.MetaData;
import etomo.type.ProcessResultDisplay;
import etomo.type.ProcessResultDisplayFactory;
import etomo.type.ReconScreenState;
import etomo.type.Run3dmodMenuOptions;
import etomo.util.DatasetFiles;
import etomo.util.EnvironmentVariable;
import etomo.comscript.BeadtrackParam;
import etomo.comscript.RunraptorParam;
import etomo.comscript.TransferfidParam;
import etomo.comscript.FortranInputSyntaxException;

/**
 * <p>Description: The dialog box for creating the fiducial model(s).</p>
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
 * <p> $Log$
 * <p> Revision 3.48  2009/05/22 21:28:47  sueh
 * <p> bug# 1216 Spaced raptor panel, added tooltips, improved button titles.
 * <p>
 * <p> Revision 3.47  2009/05/04 16:46:44  sueh
 * <p> bug# 1216 In FiducialModelDialog using AxisType to prevent the displaying
 * <p> of the RAPTOR interface unless the dataset is dual axis.  Make .preali the
 * <p> default file to use with RAPTOR.
 * <p>
 * <p> Revision 3.46  2009/05/02 01:26:07  sueh
 * <p> bug# 1216 For B axis, hiding the pick panel and not setting/getting raptor
 * <p> data from metadata.
 * <p>
 * <p> Revision 3.45  2009/05/02 01:13:28  sueh
 * <p> bug# 1216 Added the raptor panel.
 * <p>
 * <p> Revision 3.44  2009/01/20 20:00:49  sueh
 * <p> bug# 1102 Changed labeled panels to type EtomoPanel so that they can name themselves.
 * <p>
 * <p> Revision 3.43  2008/11/20 01:41:16  sueh
 * <p> bug# 1147 Changed ApplicationManager.imodSeedFiducials to
 * <p> imodSeedModel.
 * <p>
 * <p> Revision 3.42  2008/10/16 21:20:51  sueh
 * <p> bug# 1141 Removed fixRootPanel because it doesn't do anything.
 * <p>
 * <p> Revision 3.41  2008/05/13 23:01:50  sueh
 * <p> bug# 847 Adding a right click menu for deferred 3dmods to some
 * <p> process buttons.
 * <p>
 * <p> Revision 3.40  2008/05/07 00:01:32  sueh
 * <p> bug#847 Running deferred 3dmods by using the button that usually calls
 * <p> them.  This avoids having to duplicate the calls and having a
 * <p> startNextProcess function just for 3dmods.  This requires that the 3dmod
 * <p> button be passed to the function that starts the process.  Make transfer
 * <p> fid panel responsible for its own actions.
 * <p>
 * <p> Revision 3.39  2008/05/03 00:49:48  sueh
 * <p> bug# 847 Passing null for ProcessSeries to process funtions.
 * <p>
 * <p> Revision 3.38  2007/12/26 22:23:54  sueh
 * <p> bug# 1052 Return true when done() completes successfully.
 * <p>
 * <p> Revision 3.37  2007/09/10 20:42:35  sueh
 * <p> bug# 925 Should only load button states once.  Changed
 * <p> ProcessResultDisplayFactory to load button states immediately, so removing
 * <p> button state load in the dialogs.
 * <p>
 * <p> Revision 3.36  2007/07/27 16:54:50  sueh
 * <p> bug# 979 Moved "Fix Fiducial Model" to BeadtrackPanel.  Using getInstance to
 * <p> construct FiducialModelDialog because it uses action listeners, which shouldn't
 * <p> be created during construction.
 * <p>
 * <p> Revision 3.35  2007/02/09 00:48:55  sueh
 * <p> bug# 962 Made TooltipFormatter a singleton and moved its use to low-level ui
 * <p> classes.
 * <p>
 * <p> Revision 3.34  2006/07/31 16:36:32  sueh
 * <p> bug# 902 changed "View Fiducial Model" label to "View Seed Model"
 * <p>
 * <p> Revision 3.33  2006/07/19 20:12:47  sueh
 * <p> bug# 902 Added updateDisplay() to change the label of btnSeed when seeding is
 * <p> done.
 * <p>
 * <p> Revision 3.32  2006/07/05 23:26:02  sueh
 * <p> Get fine alignment fix fiducials to set the right mode.
 * <p>
 * <p> Revision 3.31  2006/07/04 20:41:42  sueh
 * <p> bug# 898 Don't remove action listeners unless the done dialog function
 * <p> succeeds.
 * <p>
 * <p> Revision 3.30  2006/07/04 18:47:37  sueh
 * <p> bug# 893 Calling updateAdvanced(boolean) in panels to change the
 * <p> headers when the advanced button is pressed.
 * <p>
 * <p> Revision 3.29  2006/07/04 05:18:49  mast
 * <p> Fix typo transferid in popup menu
 * <p>
 * <p> Revision 3.28  2006/06/30 20:01:47  sueh
 * <p> bug# 877 Calling all the done dialog functions from the dialog.done() function,
 * <p> which is called by the button action functions and saveAction() in
 * <p> ProcessDialog.  Removed the button action function overides.  Set displayed to
 * <p> false after the done dialog function is called.
 * <p>
 * <p> Revision 3.27  2006/06/28 18:44:50  sueh
 * <p> bug# 889 done():  Calling beadtrack done.
 * <p>
 * <p> Revision 3.26  2006/06/27 23:12:16  sueh
 * <p> bug# 887 getParameters(BaseScreenState):  fixed null pointer exception.
 * <p>
 * <p> Revision 3.25  2006/06/21 15:52:51  sueh
 * <p> bug# 581 Passing axis to ContextPopup, so that imodqtassist can be run.
 * <p>
 * <p> Revision 3.24  2006/06/16 15:25:10  sueh
 * <p> bug# 734 Moved track and use buttons from fiducial model dialog to beadtracker
 * <p> dialog.
 * <p>
 * <p> Revision 3.23  2006/06/07 22:25:47  sueh
 * <p> bug# 766 ApplicationManager.imodFixFiducials():  turning off auto center when
 * <p> fix fiducials is first run.
 * <p>
 * <p> Revision 3.22  2006/05/23 21:07:28  sueh
 * <p> bug# 617 Sharing button label text, so it can be used in messages.
 * <p>
 * <p> Revision 3.21  2006/03/30 21:25:38  sueh
 * <p> bug# 809 Do fix fiducials with auto center on.
 * <p>
 * <p> Revision 3.20  2006/02/06 21:20:54  sueh
 * <p> bug# 521 Getting toggle buttons through ProcessResultDisplayFactory.
 * <p>
 * <p> Revision 3.19  2006/01/26 22:04:33  sueh
 * <p> bug# 401 For MultiLineButton toggle buttons:  save the state and keep
 * <p> the buttons turned on each they are run, unless the process fails or is
 * <p> killed.
 * <p>
 * <p> Revision 3.18  2005/11/14 22:03:06  sueh
 * <p> bug# 762 Made buttonAction() protected.
 * <p>
 * <p> Revision 3.17  2005/08/30 19:05:28  sueh
 * <p> bug# 718 fixed a null pointer bug that happended when
 * <p> btnTransferFiducials is not set.
 * <p>
 * <p> Revision 3.16  2005/08/11 23:50:31  sueh
 * <p> bug# 711  Change enum Run3dmodMenuOption to
 * <p> Run3dmodMenuOptions, which can turn on multiple options at once.
 * <p> This allows ImodState to combine input from the context menu and the
 * <p> pulldown menu.  Get rid of duplicate code by running the 3dmods from a
 * <p> private function called run3dmod(String, Run3dmodMenuOptions).  It can
 * <p> be called from run3dmod(Run3dmodButton, Run3dmodMenuOptions) and
 * <p> the action function.
 * <p>
 * <p> Revision 3.15  2005/08/10 20:42:49  sueh
 * <p> bug# 711 Removed MultiLineToggleButton.  Making toggling an attribute
 * <p> of MultiLineButton.
 * <p>
 * <p> Revision 3.14  2005/08/09 20:22:13  sueh
 * <p> bug# 711  Implemented Run3dmodButtonContainer:  added run3dmod().
 * <p> Changed 3dmod buttons to Run3dmodButton.  No longer inheriting
 * <p> MultiLineButton from JButton.
 * <p>
 * <p> Revision 3.13  2005/08/04 20:09:47  sueh
 * <p> bug# 532  Centralizing fit window functionality by placing fitting functions
 * <p> in UIHarness.  Removing packMainWindow from the manager.  Sending
 * <p> the manager to UIHarness.pack() so that packDialogs() can be called.
 * <p>
 * <p> Revision 3.12  2005/07/29 00:54:05  sueh
 * <p> bug# 709 Going to EtomoDirector to get the current manager is unreliable
 * <p> because the current manager changes when the user changes the tab.
 * <p> Passing the manager where its needed.
 * <p>
 * <p> Revision 3.11  2005/05/10 03:26:54  sueh
 * <p> bug# 658 Using BeadtrackParam in place of ConstBeadtrackParam in
 * <p> setBeadtrackParams().  Throwing InvalidEtomoNumberException in
 * <p> getBeadtrackParams().
 * <p>
 * <p> Revision 3.10  2005/04/21 20:33:55  sueh
 * <p> bug# 615 Pass axisID to packMainWindow so it can pack only the frame
 * <p> that requires it.
 * <p>
 * <p> Revision 3.9  2005/04/16 01:55:06  sueh
 * <p> bug# 615 Moved the adding of exit buttons to the base class.
 * <p>
 * <p> Revision 3.8  2005/01/21 23:43:06  sueh
 * <p> bug# 509 bug# 591  Passing axisID to TransferfidPanel contructor so it
 * <p> can set a value for center view when the .rawtlt file is not in use.
 * <p>
 * <p> Revision 3.7  2005/01/14 03:07:26  sueh
 * <p> bug# 511 Added DialogType to super constructor.
 * <p>
 * <p> Revision 3.6  2004/12/02 20:39:29  sueh
 * <p> bug# 566 ContextPopup can specify an anchor in both the tomo guide and
 * <p> the join guide.  Need to specify the guide to anchor.
 * <p>
 * <p> Revision 3.5  2004/11/19 23:53:35  sueh
 * <p> bug# 520 merging Etomo_3-4-6_JOIN branch to head.
 * <p>
 * <p> Revision 3.4.4.1  2004/10/11 02:12:56  sueh
 * <p> bug# 520 Passed the manager to the ContextPopup object in order to get
 * <p> the propertyUserDir.
 * <p>
 * <p> Revision 3.4  2004/05/05 21:21:40  sueh
 * <p> bug# 430 moving Use fid as seed button
 * <p>
 * <p> Revision 3.3  2004/03/15 23:13:16  sueh
 * <p> progress button names changed to "btn"
 * <p>
 * <p> Revision 3.2  2004/03/15 20:23:06  sueh
 * <p> bug# 276 Moved Use Model as Seed to be next to the Seed button.  Placed Use
 * <p> Model as Seed in Advanced.
 * <p>
 * <p> Revision 3.1  2004/02/16 18:52:01  sueh
 * <p> bug# 276 Added Use Fiducial Model as Seed button with
 * <p> action = call makeFiducialModelSeedModel() and untoggle
 * <p> Track button.
 * <p>
 * <p> Revision 3.0  2003/11/07 23:19:01  rickg
 * <p> Version 1.0.0
 * <p>
 * <p> Revision 2.11  2003/10/30 21:09:41  rickg
 * <p> Bug# 340 Added context menu entry for transferfid man page
 * <p> JToggleButton -> MultilineToggleButton
 * <p>
 * <p> Revision 2.10  2003/10/30 01:43:44  rickg
 * <p> Bug# 338 Remapped context menu entries
 * <p>
 * <p> Revision 2.9  2003/10/28 23:35:48  rickg
 * <p> Bug# 336 Context menu label capitalization
 * <p>
 * <p> Revision 2.8  2003/10/20 20:08:37  sueh
 * <p> Bus322 corrected labels
 * <p>
 * <p> Revision 2.7  2003/10/15 01:34:20  sueh
 * <p> Bug277 added tooltips
 * <p>
 * <p> Revision 2.6  2003/10/10 23:17:01  sueh
 * <p> bug251 removing marks
 * <p>
 * <p> Revision 2.5  2003/10/09 22:49:42  sueh
 * <p> bug251 fixed some null reference problems with transferfid
 * <p> panel in single axis mode
 * <p>
 * <p> Revision 2.4  2003/10/07 22:43:13  sueh
 * <p> bug251 moved transferfid from fine alignment dialog
 * <p> to fiducial model dialog
 * <p>
 * <p> Revision 2.3  2003/05/19 04:31:36  rickg
 * <p> Toggle button for Fix Model
 * <p>
 * <p> Revision 2.2  2003/05/07 17:50:37  rickg
 * <p> Added beadtrack and track.log to context menu
 * <p>
 * <p> Revision 2.1  2003/04/28 23:25:25  rickg
 * <p> Changed visible imod references to 3dmod
 * <p>
 * <p> Revision 2.0  2003/01/24 20:30:31  rickg
 * <p> Single window merge to main branch
 * <p>
 * <p> Revision 1.7.2.1  2003/01/24 18:43:37  rickg
 * <p> Single window GUI layout initial revision
 * <p>
 * <p> Revision 1.7  2002/12/19 17:45:22  rickg
 * <p> Implemented advanced dialog state processing
 * <p> including:
 * <p> default advanced state set on start up
 * <p> advanced button management now handled by
 * <p> super class
 * <p>
 * <p> Revision 1.6  2002/12/19 06:02:57  rickg
 * <p> Implementing advanced parameters handling
 * <p>
 * <p> Revision 1.5  2002/12/19 00:30:05  rickg
 * <p> app manager and root pane moved to super class
 * <p>
 * <p> Revision 1.4  2002/11/14 21:18:37  rickg
 * <p> Added anchors into the tomoguide
 * <p>
 * <p> Revision 1.3  2002/10/17 22:39:42  rickg
 * <p> Added fileset name to window title
 * <p> this reference removed applicationManager messages
 * <p>
 * <p> Revision 1.2  2002/10/07 22:31:18  rickg
 * <p> removed unused imports
 * <p> reformat after emacs trashed it
 * <p>
 * <p> Revision 1.1  2002/09/09 22:57:02  rickg
 * <p> Initial CVS entry, basic functionality not including combining
 * <p> </p>
 */
public final class FiducialModelDialog extends ProcessDialog implements
    ContextMenu, Run3dmodButtonContainer {
  public static final String rcsid = "$Id$";

  private static final String SEEDING_NOT_DONE_LABEL = "Seed Fiducial Model";
  private static final String SEEDING_DONE_LABEL = "View Seed Model";
  private static final String MARK_LABEL = "# of beads to choose";
  private static final String DIAM_LABEL = "Unbinned Bead diameter";

  private final SpacedPanel pnlFiducialModel = SpacedPanel.getInstance();
  private final FiducialModelActionListener actionListener = new FiducialModelActionListener(
      this);

  final Run3dmodButton btnSeed;
  private final BeadtrackPanel pnlBeadtrack;
  private final TransferfidPanel pnlTransferfid;

  private final JPanel pnlPick = new JPanel();
  private final ButtonGroup bgPick = new ButtonGroup();
  private final RadioButton rbPickSeed = new RadioButton("Make seed and track",
      bgPick);
  private final RadioButton rbPickRaptor = new RadioButton(
      "Run RAPTOR and fix", bgPick);

  private final SpacedPanel pnlRaptor = SpacedPanel.getInstance();
  private final ButtonGroup bpRaptorInput = new ButtonGroup();
  private final RadioButton rbRaptorInputPreali = new RadioButton(
      "Run against the coarse aligned stack", bpRaptorInput);
  private final RadioButton rbRaptorInputRaw = new RadioButton(
      "Run against the raw stack", bpRaptorInput);
  private final Run3dmodButton btnOpenStack = Run3dmodButton.get3dmodInstance(
      "Open Stack in 3dmod", this);
  private final LabeledTextField ltfMark = new LabeledTextField(MARK_LABEL
      + ": ");
  private final LabeledTextField ltfDiam = new LabeledTextField(DIAM_LABEL
      + " (in pixels): ");
  private final Run3dmodButton btnOpenRaptorResult = Run3dmodButton
      .get3dmodInstance("Open RAPTOR Model in 3dmod", this);

  private final Run3dmodButton btnRaptor;
  private final MultiLineButton btnUseRaptorResult;

  private boolean transferfidEnabled = false;

  private FiducialModelDialog(final ApplicationManager appMgr,
      final AxisID axisID, final AxisType axisType) {
    super(appMgr, axisID, DialogType.FIDUCIAL_MODEL);
    //initialize final member variables
    ProcessResultDisplayFactory displayFactory = appMgr
        .getProcessResultDisplayFactory(axisID);
    btnSeed = (Run3dmodButton) displayFactory.getSeedFiducialModel();
    btnRaptor = (Run3dmodButton) displayFactory.getRaptor();
    btnUseRaptorResult = (MultiLineButton) displayFactory.getUseRaptor();
    pnlBeadtrack = BeadtrackPanel.getInstance(appMgr, axisID, dialogType);
    //root panel
    rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
    rootPanel.add(pnlFiducialModel.getContainer());
    //fiducial model panel
    pnlFiducialModel.setBoxLayout(BoxLayout.Y_AXIS);
    pnlFiducialModel.setBorder(new BeveledBorder("Fiducial Model Generation")
        .getBorder());
    if (applicationManager.isDualAxis()) {
      pnlTransferfid = TransferfidPanel.getInstance(applicationManager, axisID,
          dialogType, this);
      pnlFiducialModel.add(pnlTransferfid.getContainer());
      pnlFiducialModel.add(Box.createRigidArea(FixedDim.x0_y5));
    }
    else {
      pnlTransferfid = null;
    }
    pnlFiducialModel.add(pnlPick);
    pnlFiducialModel.add(pnlRaptor.getContainer());
    pnlFiducialModel.add(btnSeed.getComponent());
    pnlFiducialModel.add(Box.createRigidArea(FixedDim.x0_y5));
    pnlFiducialModel.add(pnlBeadtrack.getContainer());
    pnlFiducialModel.add(Box.createRigidArea(FixedDim.x0_y5));
    //transfer fiducials panel
    if (pnlTransferfid != null) {
      pnlTransferfid.setDeferred3dmodButtons();
    }
    //Pick seed or RAPTOR panel
    pnlPick.setLayout(new BoxLayout(pnlPick, BoxLayout.Y_AXIS));
    pnlPick.setBorder(BorderFactory.createEtchedBorder());
    pnlPick.setAlignmentX(Box.CENTER_ALIGNMENT);
    pnlPick.add(rbPickSeed.getComponent());
    pnlPick.add(rbPickRaptor.getComponent());
    //RAPTOR panel
    pnlRaptor.setBoxLayout(BoxLayout.Y_AXIS);
    pnlRaptor.setBorder(new EtchedBorder("Run RAPTOR").getBorder());
    pnlRaptor.setAlignmentX(Box.CENTER_ALIGNMENT);
    JPanel pnlRaptorInput = new JPanel();
    pnlRaptor.add(pnlRaptorInput);
    pnlRaptor.add(btnOpenStack.getComponent());
    pnlRaptor.add(ltfMark.getContainer());
    pnlRaptor.add(ltfDiam.getContainer());
    SpacedPanel pnlRaptorButtons = SpacedPanel.getInstance();
    pnlRaptor.add(pnlRaptorButtons);
    //RAPTOR input source panel
    pnlRaptorInput.setLayout(new BoxLayout(pnlRaptorInput, BoxLayout.Y_AXIS));
    pnlRaptorInput.setBorder(BorderFactory.createEtchedBorder());
    pnlRaptorInput.setAlignmentX(Box.CENTER_ALIGNMENT);
    pnlRaptorInput.add(rbRaptorInputPreali.getComponent());
    pnlRaptorInput.add(rbRaptorInputRaw.getComponent());
    //RAPTOR button panel
    pnlRaptorButtons.setBoxLayout(BoxLayout.X_AXIS);
    pnlRaptorButtons.add(btnRaptor.getComponent());
    pnlRaptorButtons.add(btnOpenRaptorResult.getComponent());
    pnlRaptorButtons.add(btnUseRaptorResult.getComponent());
    //exit button panel
    addExitButtons();
    //set initial values
    rbPickSeed.setSelected(true);
    rbRaptorInputPreali.setSelected(true);
    btnRaptor.setSize();
    btnOpenRaptorResult.setSize();
    btnUseRaptorResult.setSize();
    btnOpenStack.setSize();
    btnOpenStack.setAlignmentX(Box.CENTER_ALIGNMENT);
    //raptor button
    btnRaptor.setContainer(this);
    btnRaptor.setDeferred3dmodButton(btnOpenRaptorResult);
    //seed button
    btnSeed.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnSeed.setSize();
    btnSeed.setContainer(this);
    //
    btnExecute.setText("Done");
    //tool tips
    setToolTipText();
    //set dialog display state

    if (axisType != AxisType.DUAL_AXIS || axisID == AxisID.SECOND) {
      turnOffRaptor();
    }
    else {
      File raptorBin = new File("/usr/local/RAPTOR/bin");
      //RAPTOR_BIN environment variable overrides the default location of RAPTOR.
      String envVar = "RAPTOR_BIN";
      String raptorBinEnvVar = EnvironmentVariable.INSTANCE.getValue(appMgr
          .getPropertyUserDir(), envVar, axisID, appMgr.getManagerKey());
      if (raptorBinEnvVar != null && !raptorBinEnvVar.matches("\\s*")) {
        raptorBin = new File(raptorBinEnvVar);
      }
      if (!raptorBin.exists() || !raptorBin.isDirectory()) {
        System.err.println("WARNING:  " + raptorBin.getAbsolutePath()
            + " cannot be found.  The environment variable " + envVar
            + " may be incorrect.");
        turnOffRaptor();
      }
    }
    updateAdvanced(isAdvanced);
    updateEnabled();
    updateDisplay();
  }

  private void turnOffRaptor() {
    pnlPick.setVisible(false);
    updatePick();
  }

  public static FiducialModelDialog getInstance(
      final ApplicationManager appMgr, final AxisID axisID,
      final AxisType axisType) {
    FiducialModelDialog instance = new FiducialModelDialog(appMgr, axisID,
        axisType);
    instance.addListeners();
    return instance;
  }

  private void addListeners() {
    pnlFiducialModel.addMouseListener(new GenericMouseAdapter(this));
    rbPickSeed.addActionListener(actionListener);
    rbPickRaptor.addActionListener(actionListener);
    btnOpenStack.addActionListener(actionListener);
    btnRaptor.addActionListener(actionListener);
    btnOpenRaptorResult.addActionListener(actionListener);
    btnUseRaptorResult.addActionListener(actionListener);
    btnSeed.addActionListener(actionListener);
  }

  public static ProcessResultDisplay getRaptorDisplay() {
    return Run3dmodButton.getDeferredToggle3dmodInstance("Run RAPTOR",
        DialogType.FIDUCIAL_MODEL);
  }

  public static ProcessResultDisplay getUseRaptorDisplay() {
    return MultiLineButton.getToggleButtonInstance(
        "Use RAPTOR Result as Fiducial Model", DialogType.FIDUCIAL_MODEL);
  }

  public void updateDisplay() {
    if (applicationManager.getState().isSeedingDone(axisID)) {
      btnSeed.setText(SEEDING_DONE_LABEL);
    }
    else {
      btnSeed.setText(SEEDING_NOT_DONE_LABEL);
    }
  }

  public static ProcessResultDisplay getTransferFiducialsDisplay() {
    return TransferfidPanel
        .getTransferFiducialsDisplay(DialogType.FIDUCIAL_MODEL);
  }

  public static ProcessResultDisplay getSeedFiducialModelDisplay() {
    return Run3dmodButton.getToggle3dmodInstance(SEEDING_NOT_DONE_LABEL,
        DialogType.FIDUCIAL_MODEL);
  }

  public static ProcessResultDisplay getTrackFiducialsDisplay() {
    return BeadtrackPanel.getTrackFiducialsDisplay(DialogType.FIDUCIAL_MODEL);
  }

  public static ProcessResultDisplay getFixFiducialModelDisplay() {
    return Run3dmodButton.getToggle3dmodInstance("Fix Fiducial Model",
        DialogType.FIDUCIAL_MODEL);
  }

  /**
   * Set the advanced state for the dialog box
   */
  private void updateAdvanced(final boolean state) {
    pnlBeadtrack.updateAdvanced(state);
    if (pnlTransferfid != null) {
      pnlTransferfid.updateAdvanced(state);
    }
    UIHarness.INSTANCE.pack(axisID, applicationManager);
  }

  public void updateEnabled() {
    if (pnlTransferfid != null) {
      pnlTransferfid.setEnabled(transferfidEnabled);
    }
  }

  /**
   * Set the parameters for the specified beadtrack panel
   */
  public void setBeadtrackParams(final/*Const*/BeadtrackParam beadtrackParams) {
    ltfDiam.setText(Math.round(beadtrackParams.getBeadDiameter().getDouble()));
    pnlBeadtrack.setParameters(beadtrackParams);
  }

  public void setTransferFidParams() {
    if (pnlTransferfid != null) {
      pnlTransferfid.setParameters();
    }
  }

  /**
   * Get the parameters for the specified beadtrack command
   */
  public void getBeadtrackParams(final BeadtrackParam beadtrackParams)
      throws FortranInputSyntaxException, InvalidEtomoNumberException {
    pnlBeadtrack.getParameters(beadtrackParams);
  }

  public void getParameters(final BaseScreenState screenState) {
    pnlBeadtrack.getParameters(screenState);
    if (pnlTransferfid != null) {
      pnlTransferfid.getParameters(screenState);
    }
  }

  public boolean getParameters(final RunraptorParam param) {
    param.setUseRawStack(rbRaptorInputRaw.isSelected());
    String errorMessage = param.setMark(ltfMark.getText());
    if (errorMessage != null) {
      UIHarness.INSTANCE.openMessageDialog("Error in " + MARK_LABEL + ": "
          + errorMessage, "Entry Error", axisID, applicationManager
          .getManagerKey());
      return false;
    }
    errorMessage = param.setDiam(ltfDiam.getText(), rbRaptorInputPreali
        .isSelected());
    if (errorMessage != null) {
      UIHarness.INSTANCE.openMessageDialog("Error in " + DIAM_LABEL + ": "
          + errorMessage, "Entry Error", axisID, applicationManager
          .getManagerKey());
      return false;
    }
    return true;
  }

  public void getParameters(final MetaData metaData) {
    if (axisID == AxisID.FIRST) {
      metaData.setTrackUseRaptor(rbPickRaptor.isSelected());
      metaData.setTrackRaptorUseRawStack(rbRaptorInputRaw.isSelected());
      metaData.setTrackRaptorMark(ltfMark.getText());
      metaData.setTrackRaptorDiam(ltfDiam.getText());
    }
  }

  public void setParameters(final ConstMetaData metaData) {
    if (axisID == AxisID.FIRST) {
      rbPickRaptor.setSelected(metaData.getTrackUseRaptor());
      if (metaData.getTrackRaptorUseRawStack()) {
        rbRaptorInputRaw.setSelected(true);
      }
      else {
        rbRaptorInputPreali.setSelected(true);
      }
      ltfMark.setText(metaData.getTrackRaptorMark());
      ConstEtomoNumber diam = metaData.getTrackRaptorDiam();
      if (!diam.isNull()) {
        ltfDiam.setText(diam);
      }
    }
    updatePick();
  }

  public final void setParameters(final ReconScreenState screenState) {
    if (pnlTransferfid != null) {
      pnlTransferfid.setParameters(screenState);
    }
    //btnSeed.setButtonState(screenState.getButtonState(btnSeed
    //   .getButtonStateKey()));
    pnlBeadtrack.setParameters(screenState);
  }

  public void getTransferFidParams() {
    if (pnlTransferfid != null) {
      pnlTransferfid.getParameters();
    }
  }

  public void getTransferFidParams(final TransferfidParam transferFidParam) {
    if (pnlTransferfid != null) {
      pnlTransferfid.getParameters(transferFidParam);
    }
  }

  public void setTransferfidEnabled(final boolean fileExists) {
    transferfidEnabled = fileExists;
  }

  /**
   * Right mouse button context menu
   */
  public void popUpContextMenu(final MouseEvent mouseEvent) {
    String[] manPagelabel = { "Transferfid", "Beadtrack", "3dmod" };
    String[] manPage = { "transferfid.html", "beadtrack.html", "3dmod.html" };

    String[] logFileLabel = { "Track", "Transferfid" };
    String[] logFile = new String[2];
    logFile[0] = "track" + axisID.getExtension() + ".log";
    logFile[1] = "transferfid.log";

    //    ContextPopup contextPopup =
    new ContextPopup(pnlFiducialModel.getContainer(), mouseEvent,
        "GETTING FIDUCIAL", ContextPopup.TOMO_GUIDE, manPagelabel, manPage,
        logFileLabel, logFile, applicationManager, axisID);
  }

  /**
   * Tooltip string initialization
   */
  private void setToolTipText() {
    rbPickSeed
        .setToolTipText("Create a seed model and use beadtracker to generate the "
            + "fiducial model.");
    rbPickRaptor.setToolTipText("Use RAPTOR to create the fiducial model.");
    btnSeed.setToolTipText("Open new or existing seed model in 3dmod.");
    rbRaptorInputPreali
        .setToolTipText("Run RAPTOR against the coarsely aligned stack.");
    rbRaptorInputRaw.setToolTipText("Run RAPTOR against the raw stack.");
    btnOpenStack
        .setToolTipText("Opens the file that RAPTOR will be run against.");
    ltfMark.setToolTipText("Number of markers to track.");
    ltfDiam.setToolTipText("Bead diameter in pixels.");
    btnRaptor.setToolTipText("Runs the runraptor script");
    btnOpenRaptorResult
        .setToolTipText("Opens the model generated by RAPTOR and the file that RAPTOR was run against.");
    btnUseRaptorResult
        .setToolTipText("Copies the model generated by RAPTOR to the .fid file.");
  }

  public void action(final Run3dmodButton button,
      final Run3dmodMenuOptions run3dmodMenuOptions) {
    buttonAction(button.getActionCommand(), button.getDeferred3dmodButton(),
        run3dmodMenuOptions);
  }

  //  Action function for buttons
  private void buttonAction(final String command,
      Deferred3dmodButton deferred3dmodButton,
      final Run3dmodMenuOptions run3dmodMenuOptions) {
    if (command.equals(rbPickSeed.getActionCommand())) {
      updatePick();
    }
    else if (command.equals(rbPickRaptor.getActionCommand())) {
      updatePick();
    }
    else if (command.equals(btnOpenStack.getActionCommand())) {
      if (rbRaptorInputRaw.isSelected()) {
        applicationManager.imodRawStack(axisID, run3dmodMenuOptions);
      }
      else {
        applicationManager.imodCoarseAlign(axisID, run3dmodMenuOptions);
      }
    }
    else if (command.equals(btnRaptor.getActionCommand())) {
      applicationManager.runraptor(btnRaptor, null, deferred3dmodButton,
          run3dmodMenuOptions, DialogType.FIDUCIAL_MODEL, axisID);
    }
    else if (command.equals(btnOpenRaptorResult.getActionCommand())) {
      applicationManager.imodRunraptorResult(axisID, run3dmodMenuOptions);
    }
    else if (command.equals(btnUseRaptorResult.getActionCommand())) {
      applicationManager.useRunraptorResult(btnUseRaptorResult, axisID,
          DialogType.FIDUCIAL_MODEL);
    }
    else if (command.equals(btnSeed.getActionCommand())) {
      applicationManager.imodSeedModel(axisID, run3dmodMenuOptions, btnSeed,
          ImodManager.COARSE_ALIGNED_KEY, DatasetFiles.getSeedFileName(
              applicationManager, axisID), DatasetFiles.getRawTiltFile(
              applicationManager, axisID));
    }
  }

  private void updatePick() {
    if (rbPickSeed.isSelected() || !pnlPick.isVisible()) {
      pnlRaptor.setVisible(false);
      btnSeed.setVisible(true);
      pnlBeadtrack.pickSeed();
    }
    else {
      pnlRaptor.setVisible(true);
      btnSeed.setVisible(false);
      pnlBeadtrack.pickRaptor();
    }
    UIHarness.INSTANCE.pack(axisID, applicationManager);
  }

  boolean done() {
    if (!applicationManager.doneFiducialModelDialog(axisID)) {
      return false;
    }
    if (pnlTransferfid != null) {
      pnlTransferfid.done();
    }
    btnRaptor.removeActionListener(actionListener);
    btnOpenRaptorResult.removeActionListener(actionListener);
    btnSeed.removeActionListener(actionListener);
    pnlBeadtrack.done();
    setDisplayed(false);
    return true;
  }

  public void buttonAdvancedAction(final ActionEvent event) {
    super.buttonAdvancedAction(event);
    updateAdvanced(isAdvanced);
  }

  //
  //	Action listener adapters
  //
  private final class FiducialModelActionListener implements ActionListener {

    private final FiducialModelDialog adaptee;

    private FiducialModelActionListener(final FiducialModelDialog adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(final ActionEvent event) {
      adaptee.buttonAction(event.getActionCommand(), null, null);
    }
  }
}
