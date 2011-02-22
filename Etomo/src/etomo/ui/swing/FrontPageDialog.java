package etomo.ui.swing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;

import etomo.EtomoDirector;
import etomo.PeetManager;
import etomo.type.AxisID;
import etomo.type.ToolType;

/**
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright 2010</p>
 *
 * <p>Organization:
 * Boulder Laboratory for 3-Dimensional Electron Microscopy of Cells (BL3DEMC),
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 * 
 * <p> $Log$
 * <p> Revision 1.2  2011/02/03 06:22:16  sueh
 * <p> bug# 1422 Control of the processing method has been centralized in the
 * <p> processing method mediator class.  Implementing ProcessInterface.
 * <p> Supplying processes with the current processing method.
 * <p>
 * <p> Revision 1.1  2010/11/13 16:07:34  sueh
 * <p> bug# 1417 Renamed etomo.ui to etomo.ui.swing.
 * <p>
 * <p> Revision 1.10  2010/09/08 19:17:57  sueh
 * <p> bug# 1401 Added reconActionForAutomation
 * <p>
 * <p> Revision 1.9  2010/06/30 21:02:38  sueh
 * <p> bug# 1387 Debuging btnRecon.
 * <p>
 * <p> Revision 1.8  2010/03/27 05:05:48  sueh
 * <p> bug# 1316 Add volume flatten tool button to front page.
 * <p>
 * <p> Revision 1.7  2010/02/17 05:03:12  sueh
 * <p> bug# 1301 Using manager instead of manager key for popping up messages.
 * <p>
 * <p> Revision 1.6  2009/12/29 18:50:31  sueh
 * <p> bug# 1297 Matched button labels to "New >" menu item labels.
 * <p>
 * <p> Revision 1.5  2009/11/24 00:44:33  sueh
 * <p> bug# 1289 On PEET button being pressed, calling
 * <p> PeetManager.isInterfaceAvaiable before opening PEET interface.
 * <p>
 * <p> Revision 1.4  2009/11/23 17:52:21  sueh
 * <p> bug# 1289 Popping up a message instead of opening the PEET interface if
 * <p> PARTICLE_DIR doesn't exist.
 * <p>
 * <p> Revision 1.3  2009/10/28 17:47:16  sueh
 * <p> bug# 1275 Moved responsibility for closing FrontPageManager to
 * <p> EtomoDirector.
 * <p>
 * <p> Revision 1.2  2009/10/27 20:41:58  sueh
 * <p> bug# 1275 Made class a top-level dialog for FrontPageManager.
 * <p>
 * <p> Revision 1.1  2009/10/23 19:46:48  sueh
 * <p> bug# 1275 Default display.  Contains buttons for choosing one of five
 * <p> interfaces.
 * <p> </p>
 */

public final class FrontPageDialog {
  public static final String rcsid = "$Id$";

  private final SpacedPanel pnlRoot = SpacedPanel.getInstance(true);
  private final MultiLineButton btnRecon = MultiLineButton.getDebugInstance("New "
      + EtomoMenu.RECON_LABEL);
  private final MultiLineButton btnJoin = new MultiLineButton("New "
      + EtomoMenu.JOIN_LABEL);
  private final MultiLineButton btnNad = new MultiLineButton("New " + EtomoMenu.NAD_LABEL);
  private final MultiLineButton btnGeneric = new MultiLineButton("New "
      + EtomoMenu.GENERIC_LABEL);
  private final MultiLineButton btnPeet = new MultiLineButton("New "
      + EtomoMenu.PEET_LABEL);
  private final MultiLineButton btnFlattenVolume = new MultiLineButton("Flatten Volume");

  private FrontPageDialog() {
  }

  public static FrontPageDialog getInstance() {
    FrontPageDialog instance = new FrontPageDialog();
    instance.createPanel();
    instance.setTooltips();
    instance.addListeners();
    return instance;
  }

  private void createPanel() {
    //local panels
    SpacedPanel pnlButtonRow1 = SpacedPanel.getInstance();
    SpacedPanel pnlButtonRow2 = SpacedPanel.getInstance();
    SpacedPanel pnlButtonRow3 = SpacedPanel.getInstance();
    //initialize
    btnRecon.setSize();
    btnJoin.setSize();
    btnNad.setSize();
    btnGeneric.setSize();
    btnPeet.setSize();
    btnFlattenVolume.setSize();
    //root panel
    pnlRoot.setBoxLayout(BoxLayout.Y_AXIS);
    pnlRoot.add(pnlButtonRow1);
    pnlRoot.add(pnlButtonRow2);
    pnlRoot.add(pnlButtonRow3);
    //button row 1 panel
    pnlButtonRow1.setBoxLayout(BoxLayout.X_AXIS);
    pnlButtonRow1.add(btnRecon.getComponent());
    pnlButtonRow1.add(btnJoin.getComponent());
    //button row 2 panel
    pnlButtonRow2.setBoxLayout(BoxLayout.X_AXIS);
    pnlButtonRow2.add(btnNad.getComponent());
    pnlButtonRow2.add(btnGeneric.getComponent());
    //button row 3 panel
    pnlButtonRow3.setBoxLayout(BoxLayout.X_AXIS);
    pnlButtonRow3.add(btnPeet.getComponent());
    pnlButtonRow3.add(btnFlattenVolume.getComponent());
  }

  private void addListeners() {
    ActionListener actionListener = new FrontPageActionListener(this);
    btnRecon.addActionListener(actionListener);
    btnJoin.addActionListener(actionListener);
    btnNad.addActionListener(actionListener);
    btnGeneric.addActionListener(actionListener);
    btnPeet.addActionListener(actionListener);
    btnFlattenVolume.addActionListener(actionListener);
  }

  public Container getContainer() {
    return pnlRoot.getContainer();
  }

  public void reconActionForAutomation() {
    EtomoDirector.INSTANCE.openTomogramAndDoAutomation(true, AxisID.ONLY);
  }

  private void action(ActionEvent actionEvent) {
    String actionCommand = actionEvent.getActionCommand();
    if (actionCommand.equals(btnRecon.getActionCommand())) {
      EtomoDirector.INSTANCE.openTomogram(true, AxisID.ONLY);
    }
    else if (actionCommand.equals(btnJoin.getActionCommand())) {
      EtomoDirector.INSTANCE.openJoin(true, AxisID.ONLY);
    }
    else if (actionCommand.equals(btnNad.getActionCommand())) {
      EtomoDirector.INSTANCE.openAnisotropicDiffusion(true, AxisID.ONLY);
    }
    else if (actionCommand.equals(btnGeneric.getActionCommand())) {
      EtomoDirector.INSTANCE.openGenericParallel(true, AxisID.ONLY);
    }
    else if (actionCommand.equals(btnPeet.getActionCommand())) {
      if (PeetManager.isInterfaceAvailable()) {
        EtomoDirector.INSTANCE.openPeet(true, AxisID.ONLY);
      }
    }
    else if (actionCommand.equals(btnFlattenVolume.getActionCommand())) {
      EtomoDirector.INSTANCE.openTools(AxisID.ONLY, ToolType.FLATTEN_VOLUME);
    }
  }

  private void setTooltips() {
    btnRecon.setToolTipText("Start a new tomographic reconstruction.");
    btnJoin.setToolTipText("Stack tomograms.");
    btnNad.setToolTipText("Run a nonlinear anisotropic diffusion process on a "
        + "tomogram.");
    btnGeneric.setToolTipText("Run a generic parallel process.");
    btnPeet.setToolTipText("Start the interface for the PEET particle averaging "
        + "package.");
  }

  private static final class FrontPageActionListener implements ActionListener {
    private final FrontPageDialog listenee;

    private FrontPageActionListener(FrontPageDialog listenee) {
      this.listenee = listenee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      listenee.action(actionEvent);
    }
  }
}
