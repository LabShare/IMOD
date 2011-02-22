package etomo.ui.swing;

import javax.swing.JCheckBoxMenuItem;

import etomo.EtomoDirector;
import etomo.storage.autodoc.AutodocTokenizer;
import etomo.type.UITestFieldType;
import etomo.util.Utilities;

/**
* <p>Description: </p>
* 
* <p>Copyright: Copyright 2008</p>
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
* <p> Revision 1.1  2010/11/13 16:07:34  sueh
* <p> bug# 1417 Renamed etomo.ui to etomo.ui.swing.
* <p>
* <p> Revision 1.2  2009/11/20 17:02:22  sueh
* <p> bug# 1282 Added prefixes to all of the field names, so that the fields that
* <p> are actually abstract buttons (radio buttons, etc) won't be activated by a
* <p> "bn." field command.
* <p>
* <p> Revision 1.1  2009/01/20 19:50:21  sueh
* <p> bug# 1102 A self-naming JCheckBoxMenuItem.
* <p> </p>
*/
final class CheckBoxMenuItem extends JCheckBoxMenuItem {
  public static final String rcsid = "$Id$";

  public CheckBoxMenuItem() {
    super();
  }

  public CheckBoxMenuItem(String text) {
    super(text);
  }

  public void setText(String text) {
    super.setText(text);
    setName(text);
  }

  public void setName(String text) {
    String name = Utilities.convertLabelToName(text);
    super.setName(UITestFieldType.CHECK_BOX_MENU_ITEM.toString()
        + AutodocTokenizer.SEPARATOR_CHAR + name);
    if (EtomoDirector.INSTANCE.getArguments().isPrintNames()) {
      System.out.println(getName() + ' ' + AutodocTokenizer.DEFAULT_DELIMITER + ' ');
    }
  }

}
