package etomo.ui.swing;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import etomo.EtomoDirector;
import etomo.logic.FieldValidator;
import etomo.storage.autodoc.AutodocTokenizer;
import etomo.type.ConstEtomoNumber;
import etomo.type.EtomoNumber;
import etomo.type.UITestFieldType;
import etomo.ui.FieldType;
import etomo.ui.FieldValidationFailedException;
import etomo.ui.UIComponent;
import etomo.util.Utilities;

/**
 * <p>Description: A self-naming, labeled text field.  Implements StateChangeSource with
 * its state equal to whether it has changed since it was checkpointed.</p>
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
 * <p> Revision 1.5  2011/04/25 23:36:21  sueh
 * <p> bug# 1416 Implemented StateChangeActionSource.  Added equals(Object) and equals(Document) so
 * <p> StateChangedReporter can find instances of this class.  Changed isChanged to isDifferentFromCheckpoint.
 * <p> Added getState.
 * <p>
 * <p> Revision 1.4  2011/04/04 17:20:46  sueh
 * <p> bug# 1416 Added savedValue, checkpoint, isChanged.
 * <p>
 * <p> Revision 1.3  2011/03/02 00:00:12  sueh
 * <p> bug# 1452 Removing image rotation conversion between float and
 * <p> double.  Using string where possible.
 * <p>
 * <p> Revision 1.2  2011/02/22 18:13:44  sueh
 * <p> bug# 1437 Reformatting.
 * <p>
 * <p> Revision 1.1  2010/11/13 16:07:34  sueh
 * <p> bug# 1417 Renamed etomo.ui to etomo.ui.swing.
 * <p>
 * <p> Revision 3.36  2009/11/20 17:27:33  sueh
 * <p> bug# 1282 Added prefixes to all of the field names, so that the fields that
 * <p> are actually abstract buttons (radio buttons, etc) won't be activated by a
 * <p> "bn." field command.
 * <p>
 * <p> Revision 3.35  2009/10/15 23:36:45  sueh
 * <p> bug# 1274 in isEmpty, corrected compare, which was using "|" instead of
 * <p> "||".
 * <p>
 * <p> Revision 3.34  2009/04/13 22:56:07  sueh
 * <p> Removed newstuff.
 * <p>
 * <p> Revision 3.33  2009/02/27 03:53:18  sueh
 * <p> bug# 1172 Added experimental automation recording background color
 * <p> (newstuff only).
 * <p>
 * <p> Revision 3.32  2009/01/20 20:12:23  sueh
 * <p> bug# 1102 Changed UITestField to UITestFieldType.  Simplified the name
 * <p> by removing the expanded state portion.
 * <p>
 * <p> Revision 3.31  2008/10/27 20:40:31  sueh
 * <p> bug# 1141 In setToolTipText, added the tooltip to the label.
 * <p>
 * <p> Revision 3.30  2008/05/30 22:32:10  sueh
 * <p> bug# 1102 Isolating the etomo.uitest package so it is not need for
 * <p> running EtomoDirector.
 * <p>
 * <p> Revision 3.29  2008/05/30 21:31:39  sueh
 * <p> bug# 1102 Moved uitest classes to etomo.uitest.
 * <p>
 * <p> Revision 3.28  2008/04/02 19:06:24  sueh
 * <p> bug# 1104 Allow field specific debugging in ToolTipFormatter.
 * <p>
 * <p> Revision 3.27  2007/12/26 22:24:38  sueh
 * <p> bug# 1052 Moved argument handling from EtomoDirector to a separate class.
 * <p>
 * <p> Revision 3.26  2007/09/07 00:27:19  sueh
 * <p> bug# 989 Using a public INSTANCE to refer to the EtomoDirector singleton
 * <p> instead of getInstance and createInstance.
 * <p>
 * <p> Revision 3.25  2007/06/08 22:21:47  sueh
 * <p> bug# 1014 Added clear().
 * <p>
 * <p> Revision 3.24  2007/04/13 18:44:56  sueh
 * <p> bug# 964 Added debug member variable.
 * <p>
 * <p> Revision 3.23  2007/03/27 00:04:07  sueh
 * <p> bug# 964 Removed unused functions.
 * <p>
 * <p> Revision 3.22  2007/03/01 01:39:19  sueh
 * <p> bug# 964 Moved colors from UIUtilities to Colors.
 * <p>
 * <p> Revision 3.21  2007/02/09 00:50:32  sueh
 * <p> bug# 962 Made TooltipFormatter a singleton and moved its use to low-level ui
 * <p> classes.
 * <p>
 * <p> Revision 3.20  2007/02/05 23:39:48  sueh
 * <p> bug# 962 Added setHighlight.
 * <p>
 * <p> Revision 3.19  2006/05/19 19:47:37  sueh
 * <p> bug# 866 Added setText(ConstEtomoNumber)
 * <p>
 * <p> Revision 3.18  2006/05/16 21:36:20  sueh
 * <p> bug# 856 Changing the name whenever the label is changed so that its easy to
 * <p> see what the name is.
 * <p>
 * <p> Revision 3.17  2006/05/11 19:27:44  sueh
 * <p> bug# 838 Added setMinimumWidth
 * <p>
 * <p> Revision 3.16  2006/04/25 19:15:33  sueh
 * <p> bug# 787 Added UITestField, an enum style class which contains the
 * <p> fields found in uitestaxis.adoc files.
 * <p>
 * <p> Revision 3.15  2006/04/06 20:17:04  sueh
 * <p> bug# 808 Moved the function convertLabelToName from UIUtilities to
 * <p> util.Utilities.
 * <p>
 * <p> Revision 3.14  2006/03/16 01:56:38  sueh
 * <p> bug# 828 Added toString() and paramString().
 * <p>
 * <p> Revision 3.13  2006/01/12 17:11:40  sueh
 * <p> bug# 798 Moved the autodoc classes to etomo.storage.autodoc.
 * <p>
 * <p> Revision 3.12  2006/01/11 22:11:56  sueh
 * <p> bug# 675 No longer need to create an instance without a name -
 * <p> removed getNamelessInstance().
 * <p>
 * <p> Revision 3.11  2006/01/04 20:26:02  sueh
 * <p> bug# 675 For printing the name:  putting the type first and making the type
 * <p> as constant.
 * <p>
 * <p> Revision 3.10  2006/01/03 23:40:26  sueh
 * <p> bug# 675 Added a getNamelessInstance() to create an instance without
 * <p> a name.  Added setName().  These functions can be used to create an
 * <p> instances with an "unpublished" name.
 * <p>
 * <p> Revision 3.9  2005/12/23 02:15:43  sueh
 * <p> bug# 675 Named the text field so it can be found by JfcUnit.
 * <p>
 * <p> Revision 3.8  2005/07/01 23:03:50  sueh
 * <p> bug# 619 added setText(long)
 * <p>
 * <p> Revision 3.7  2005/06/13 23:37:28  sueh
 * <p> bug# 675 Added a setName() call to the constructor to try out jfcUnit.
 * <p>
 * <p> Revision 3.6  2005/03/24 17:52:24  sueh
 * <p> Removed unused functions.
 * <p>
 * <p> Revision 3.5  2005/01/05 00:08:00  sueh
 * <p> bug# 567 Added setTextPreferredWidth().
 * <p>
 * <p> Revision 3.4  2004/11/19 23:57:15  sueh
 * <p> bug# 520 merging Etomo_3-4-6_JOIN branch to head.
 * <p>
 * <p> Revision 3.3.4.1  2004/11/19 00:21:58  sueh
 * <p> bug# 520 Added equals(String) to compare a String parameter against
 * <p> getText().
 * <p>
 * <p> Revision 3.3  2004/04/16 02:10:04  sueh
 * <p> bug# 409 added addKeyListener() to allow keystrokes to reacted to
 * <p>
 * <p> Revision 3.2  2004/04/07 21:04:02  rickg
 * <p> Alignment is now set on the panel
 * <p>
 * <p> Revision 3.1  2004/03/24 03:04:41  rickg
 * <p> Added setText(float) methof
 * <p> Fixed setMaximumSize bug
 * <p>
 * <p> Revision 3.0  2003/11/07 23:19:01  rickg
 * <p> Version 1.0.0
 * <p>
 * <p> Revision 2.2  2003/06/03 23:27:05  rickg
 * <p> Comment updates
 * <p> Removed ambiguous methods
 * <p>
 * <p> Revision 2.1  2003/02/24 23:26:14  rickg
 * <p> Added a get label preferred size method
 * <p>
 * <p> Revision 2.0  2003/01/24 20:30:31  rickg
 * <p> Single window merge to main branch
 * <p>
 * <p> Revision 1.4.2.1  2003/01/24 18:43:37  rickg
 * <p> Single window GUI layout initial revision
 * <p>
 * <p> Revision 1.4  2002/12/31 23:13:24  rickg
 * <p> Added possible setalignmentx method
 * <p>
 * <p> Revision 1.3  2002/12/27 05:50:37  rickg
 * <p> Set the text field maximum height to twice the largest of the
 * <p> label and text font size in points.
 * <p>
 * <p> Revision 1.2  2002/12/10 21:34:38  rickg
 * <p> Added get and set size methods
 * <p>
 * <p> Revision 1.1  2002/09/09 22:57:02  rickg
 * <p> Initial CVS entry, basic functionality not including combining
 * <p> </p>
 */
final class LabeledTextField implements UIComponent, SwingComponent {
  public static final String rcsid = "$Id$";

  private final JPanel panel = new JPanel();
  private final JLabel label = new JLabel();
  private final JTextField textField = new JTextField();
  private final EtomoNumber.Type numericType;
  private final FieldType fieldType;
  private final String locationDescr;

  private boolean debug = false;
  private String checkpointValue = null;
  private EtomoNumber nCheckpointValue = null;
  private boolean required = false;

  public String toString() {
    return "[label:" + getLabel() + "]";
  }

  String paramString() {
    return "label=" + label.getText() + ",textField=" + textField.getText();
  }

  public boolean equals(final Object object) {
    return object == textField;
  }

  public boolean equals(final Document document) {
    return textField.getDocument() == document;
  }

  private LabeledTextField(final FieldType fieldType, final String tfLabel,
      final EtomoNumber.Type numericType, final int hgap, final String locationDescr) {
    this.locationDescr = locationDescr;
    this.fieldType = fieldType;
    this.numericType = numericType;
    // set label
    setLabel(tfLabel);

    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(label);
    if (hgap > 0) {
      panel.add(Box.createRigidArea(new Dimension(hgap, 0)));
    }
    panel.add(textField);

    // Set the maximum height of the text field box to twice the
    // font size since it is not set by default
    Dimension maxSize = textField.getMaximumSize();
    if (label.getFont().getSize() > textField.getFont().getSize()) {
      maxSize.setSize(maxSize.getWidth(), 2 * label.getFont().getSize());
    }
    else {
      maxSize.setSize(maxSize.getWidth(), 2 * textField.getFont().getSize());
    }
    textField.setMaximumSize(maxSize);
    if (fieldType == FieldType.FILE) {
      textField.setHorizontalAlignment(JTextField.RIGHT);
    }
  }

  LabeledTextField(final FieldType fieldType, final String tfLabel) {
    this(fieldType, tfLabel, null, 0, null);
  }

  LabeledTextField(final FieldType fieldType, final String tfLabel,
      final String locationDescr) {
    this(fieldType, tfLabel, null, 0, locationDescr);
  }

  LabeledTextField(final FieldType fieldType, final String tfLabel, final int hgap) {
    this(fieldType, tfLabel, null, hgap, null);
  }

  static LabeledTextField getNumericInstance(final String tfLabel,
      final EtomoNumber.Type numericType) {
    FieldType fieldType = FieldType.INTEGER;
    if (numericType == EtomoNumber.Type.DOUBLE) {
      fieldType = FieldType.FLOATING_POINT;
    }
    return new LabeledTextField(fieldType, tfLabel, numericType, 0, null);
  }

  static LabeledTextField getNumericInstance(final String tfLabel) {
    return getNumericInstance(tfLabel, EtomoNumber.Type.INTEGER);
  }

  private void setName(final String tfLabel) {
    String name = Utilities.convertLabelToName(tfLabel);
    textField.setName(UITestFieldType.TEXT_FIELD.toString()
        + AutodocTokenizer.SEPARATOR_CHAR + name);
    if (EtomoDirector.INSTANCE.getArguments().isPrintNames()) {
      System.out.println(textField.getName() + ' ' + AutodocTokenizer.DEFAULT_DELIMITER
          + ' ');
    }
  }

  /**
   * Saves the current text as the checkpoint.
   */
  void checkpoint() {
    checkpointValue = getText();
    if (numericType != null) {
      if (nCheckpointValue == null) {
        nCheckpointValue = new EtomoNumber(numericType);
      }
      nCheckpointValue.set(checkpointValue);
    }
  }

  /**
   * Saves value as the checkpoint.
   */
  void checkpoint(final int value) {
    checkpointValue = new Integer(value).toString();
    if (numericType != null) {
      if (nCheckpointValue == null) {
        nCheckpointValue = new EtomoNumber(numericType);
      }
      nCheckpointValue.set(checkpointValue);
    }
  }

  /**
   * Saves value as the checkpoint.
   */
  void checkpoint(final ConstEtomoNumber value) {
    checkpointValue = value.toString();
    if (numericType != null) {
      if (nCheckpointValue == null) {
        nCheckpointValue = new EtomoNumber(numericType);
      }
      nCheckpointValue.set(checkpointValue);
    }
  }

  /**
   * Saves value as the checkpoint.
   */
  void checkpoint(final double value) {
    checkpointValue = new Double(value).toString();
    if (numericType != null) {
      if (nCheckpointValue == null) {
        nCheckpointValue = new EtomoNumber(numericType);
      }
      nCheckpointValue.set(checkpointValue);
    }
  }

  /**
   * Saves value as the checkpoint.
   */
  void checkpoint(final String value) {
    checkpointValue = value;
    if (numericType != null) {
      if (nCheckpointValue == null) {
        nCheckpointValue = new EtomoNumber(numericType);
      }
      nCheckpointValue.set(checkpointValue);
    }
  }

  /**
   * Resets to checkpointValue if checkpointValue has been set.  Otherwise has no effect.
   */
  void resetToCheckpoint() {
    if (checkpointValue == null) {
      return;
    }
    setText(checkpointValue);
  }

  public void addActionListener(ActionListener listener) {
    textField.addActionListener(listener);
  }

  /**
   * If the field is disabled then return false because its value doesn't matter.
   * First it returns true if the checkpoint has not been done; the checkpoint value is
   * from an outside value, so the current value must be different from a non-existant
   * checkpoint value.  After eliminating this possibility, it returns a boolean based on
   * the difference between the text field value and the checkpointed value.
   * @return
   */
  boolean isDifferentFromCheckpoint() {
    return isDifferentFromCheckpoint(false);
  }

  /**
   * 
   * @param alwaysCheck - check for difference even when the field is disables or invisible
   * @return
   */
  boolean isDifferentFromCheckpoint(final boolean alwaysCheck) {
    if (!alwaysCheck && (!isEnabled() || !isVisible())) {
      return false;
    }
    if (checkpointValue == null) {
      return true;
    }
    if (numericType == null) {
      return !checkpointValue.equals(textField.getText());
    }
    return !nCheckpointValue.equals(textField.getText());
  }

  void clear() {
    textField.setText("");
  }

  boolean equals(final String thatText) {
    String text = getText();
    if (text == null) {
      if (thatText == null) {
        return true;
      }
      return false;
    }
    if (thatText == null) {
      return false;
    }
    if (text.trim().equals(thatText.trim())) {
      return true;
    }
    return false;
  }

  void setHighlight(final boolean highlight) {
    if (highlight) {
      textField.setBackground(Colors.HIGHLIGHT_BACKGROUND);
    }
    else {
      textField.setBackground(Colors.BACKGROUND);
    }
  }

  public SwingComponent getUIComponent() {
    return this;
  }

  public Component getComponent() {
    return panel;
  }

  Container getContainer() {
    return panel;
  }

  String getLabel() {
    return label.getText();
  }

  String getQuotedLabel() {
    return Utilities.quoteLabel(label.getText());
  }

  void setLabel(final String label) {
    this.label.setText(label);
    setName(label);
  }

  void setRequired(final boolean required) {
    this.required = required;
  }

  String getText(final boolean doValidation) throws FieldValidationFailedException {
    String text = textField.getText();
    if (doValidation && textField.isEnabled()) {
      text = FieldValidator.validateText(text, fieldType, this, getQuotedLabel()
          + (locationDescr == null ? "" : " in " + locationDescr), required);
    }
    return text;
  }

  /**
   * return text without validation
   */
  String getText() {
    return textField.getText();
  }

  boolean isEmpty() {
    String text = textField.getText();
    return text == null || text.matches("\\s*");
  }

  void setText(final File file) {
    textField.setText(file.getAbsolutePath());
  }

  void setText(ConstEtomoNumber text) {
    if (text == null) {
      textField.setText("");
    }
    else {
      textField.setText(text.toString());
    }
  }

  void setText(final String text) {
    textField.setText(text);
  }

  void setText(final int value) {
    textField.setText(String.valueOf(value));
  }

  void setText(final long value) {
    textField.setText(String.valueOf(value));
  }

  void setText(final double value) {
    textField.setText(Double.toString(value));
  }

  void setEnabled(final boolean isEnabled) {
    textField.setEnabled(isEnabled);
    label.setEnabled(isEnabled);
  }

  boolean isEnabled() {
    return (textField.isEnabled());
  }

  boolean isEditable() {
    return (textField.isEditable());
  }

  boolean isVisible() {
    return panel.isVisible();
  }

  void setVisible(final boolean isVisible) {
    panel.setVisible(isVisible);
  }

  void setEditable(final boolean editable) {
    textField.setEditable(editable);
  }

  void setDebug(final boolean debug) {
    this.debug = debug;
  }

  public void addDocumentListener(final DocumentListener listener) {
    textField.getDocument().addDocumentListener(listener);
  }

  void setTextPreferredSize(final Dimension size) {
    textField.setPreferredSize(size);
    textField.setMaximumSize(size);
  }

  void setPreferredWidth(final int width) {
    Dimension dim = textField.getPreferredSize();
    dim.width = width * (int) Math.round(UIParameters.INSTANCE.getFontSizeAdjustment());
    textField.setPreferredSize(dim);
    textField.setMaximumSize(dim);
  }

  void setTextPreferredWidth(final double minWidth) {
    Dimension prefSize = textField.getPreferredSize();
    prefSize.setSize(minWidth, prefSize.getHeight());
    textField.setPreferredSize(prefSize);
  }

  void setMinimumWidth(final double minWidth) {
    Dimension prefSize = textField.getPreferredSize();
    prefSize.setSize(minWidth, prefSize.getHeight());
    textField.setMinimumSize(prefSize);
  }

  /**
   * Set the absolute preferred size of the panel
   * @param size

   public void setPreferredSize(Dimension size) {
   panel.setPreferredSize(size);
   }*/

  /**
   * Set the absolute maximum size of the panel
   * @param size
   
   public void setMaximumSize(Dimension size) {
   panel.setMaximumSize(size);
   }*/

  Dimension getLabelPreferredSize() {
    return label.getPreferredSize();
  }

  void setColumns(final int columns) {
    textField.setColumns(columns);
  }

  void setAlignmentX(final float alignment) {
    panel.setAlignmentX(alignment);
  }

  void setToolTipText(final String text) {
    boolean setDebug = debug && !TooltipFormatter.INSTANCE.isDebug();
    if (setDebug) {
      TooltipFormatter.INSTANCE.setDebug(debug);
    }
    String tooltip = TooltipFormatter.INSTANCE.format(text);
    if (setDebug) {
      TooltipFormatter.INSTANCE.setDebug(false);
    }
    panel.setToolTipText(tooltip);
    textField.setToolTipText(tooltip);
    label.setToolTipText(tooltip);
  }

  void addMouseListener(final MouseListener listener) {
    panel.addMouseListener(listener);
    label.addMouseListener(listener);
    textField.addMouseListener(listener);
  }
}