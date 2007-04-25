package etomo.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;

import etomo.EtomoDirector;
import etomo.storage.autodoc.AutodocTokenizer;
import etomo.type.UITestField;
import etomo.util.Utilities;

/**
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright 2006</p>
 *
 * <p>Organization:
 * Boulder Laboratory for 3-Dimensional Electron Microscopy of Cells (BL3DEMC),
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 */
final class TextField {
  public static final String rcsid = "$Id$";

  private final JTextField textField = new JTextField();

  TextField(String reference) {
    setName(reference);
    // Set the maximum height of the text field box to twice the
    // font size since it is not set by default
    JLabel label = new JLabel(reference);
    Dimension maxSize = textField.getMaximumSize();
    if (label.getFont().getSize() > textField.getFont().getSize()) {
      maxSize.setSize(maxSize.getWidth(), 2 * label.getFont().getSize());
    }
    else {
      maxSize.setSize(maxSize.getWidth(), 2 * textField.getFont().getSize());
    }
    textField.setMaximumSize(maxSize);
  }

  void setToolTipText(String text) {
    textField.setToolTipText(TooltipFormatter.INSTANCE.format(text));
  }

  Component getComponent() {
    return textField;
  }

  void setAlignmentX(float alignmentX) {
    textField.setAlignmentX(alignmentX);
  }

  void setEnabled(boolean enabled) {
    textField.setEnabled(enabled);
  }

  void setEditable(boolean editable) {
    textField.setEditable(editable);
  }

  void setText(String text) {
    textField.setText(text);
  }

  String getText() {
    return textField.getText();
  }

  void setSize(Dimension size) {
    textField.setPreferredSize(size);
    textField.setMaximumSize(size);
  }
  
  Dimension getPreferredSize(){
    return textField.getPreferredSize();
  }
  
  String getName() {
    return textField.getName();
  }
  
  boolean isEnabled() {
    return textField.isEnabled();
  }

  private void setName(String reference) {
    String name = Utilities.convertLabelToName(reference);
    textField.setName(name);
    if (EtomoDirector.getInstance().isPrintNames()) {
      System.out.println(UITestField.TEXT_FIELD.toString()
          + AutodocTokenizer.SEPARATOR_CHAR + name + ' '
          + AutodocTokenizer.DEFAULT_DELIMITER + ' ');
    }
  }
}
/**
 * <p> $Log$
 * <p> Revision 1.2  2007/02/09 00:53:33  sueh
 * <p> bug# 962 Made TooltipFormatter a singleton and moved its use to low-level ui
 * <p> classes.
 * <p>
 * <p> Revision 1.1  2006/09/13 23:58:14  sueh
 * <p> bug# 924 Added TextField:  extends JTextField and automatically names itself.
 * <p> </p>
 */
