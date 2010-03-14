/*
 * rangedialog.h - Header for RangeDialog class
 *
 *  $Id$
 *
 *  $Log$
 *  Revision 1.4  2009/08/10 22:34:39  mast
 *  General reworking of program
 *
 *
 */
#ifndef RANGEDIALOG_H
#define RANGEDIALOG_H

#include<QDialog>

class QLabel;
class QLineEdit;
class QPushButton;
class QGroupBox;
class QRadioButton;
class QCheckBox;
class QSpinBox;

class RangeDialog :public QDialog
{
  Q_OBJECT
  public:
    RangeDialog(QWidget *parent=0);
signals:
    void range(double lowX, double highX, double, double );
    void x1MethodChosen(int );
    void x2MethodChosen(int);
private slots:
    void rangeSetted();
    void enableApplyButton(const QString &text);
    void x1LinearChecked();
    void x1SimplexChecked();
    void x2LinearChecked();
    void x2SimplexChecked();
    void zeroMethodClicked(int which);
    void fitPowerClicked(bool state);
    void orderChanged(int value);
 private:
    void showHideWidget(QWidget *widget, bool state);
    void manageWidgets(int which);
    QLabel *mX1_label_1;
    QLabel *mX1_label_2;
    QGroupBox *mX1Group; 
    QRadioButton *mX1LinearRadio;
    QRadioButton *mX1SimplexRadio;
    QLabel *mX2_label_1;
    QLabel *mX2_label_2;
    QLineEdit *mX1_edit_1;
    QLineEdit *mX1_edit_2;
    QLineEdit *mX2_edit_1;
    QLineEdit *mX2_edit_2;
    QGroupBox *mX2Group;
    QRadioButton *mX2LinearRadio;
    QRadioButton *mX2SimplexRadio;
    QCheckBox *mPowerCheckBox;
    QLabel *mOrderLabel;
    QSpinBox *mOrderSpinBox;
    QPushButton *mApplyButton;
    QPushButton *mCloseButton;
};
#endif
