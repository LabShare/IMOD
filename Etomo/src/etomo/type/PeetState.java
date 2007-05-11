package etomo.type;

import java.util.Properties;

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
* 
* <p> $Log$ </p>
*/
public final class PeetState implements BaseState {
  public static  final String  rcsid =  "$Id$";
  
  private static final String KEY = "PeetState";
  private static final String PARSER_KEY="Parser";
  private static final String ITERATION_LIST_SIZE_KEY = "IterationListSize";
  private static final String LST_THRESHOLDS_KEY = "LstThresholds";
  
  private final EtomoNumber parserIterationListSize = new EtomoNumber(PARSER_KEY+"."+ITERATION_LIST_SIZE_KEY);
  private final IntKeyList parserLstThresholdsArray = IntKeyList.getStringInstance(PARSER_KEY+"."+LST_THRESHOLDS_KEY);
  private final EtomoNumber iterationListSize = new EtomoNumber(ITERATION_LIST_SIZE_KEY);
  private final IntKeyList lstThresholdsArray = IntKeyList.getStringInstance(LST_THRESHOLDS_KEY);
  
  public void store(Properties props) {
    store(props,"");
  }
  public void load(Properties props) {
    load(props,"");
  }
  
  public void setParserLstThresholdsArray(final String[] input) {
    parserLstThresholdsArray.reset();
    parserLstThresholdsArray.set(input);
  }
  
  public ConstIntKeyList getParserLstThresholdsArray() {
    return parserLstThresholdsArray;
  }
  
  public void setLstThresholdsArray(final ConstIntKeyList input) {
    lstThresholdsArray.reset();
    lstThresholdsArray.set(input);
  }
  
  public void setParserIterationListSize(final int input) {
    parserIterationListSize.set(input);
  }
  
  public int getParserIterationListSize() {
    return parserIterationListSize.getInt();
  }
  
  public void setIterationListSize(final int input) {
    iterationListSize.set(input);
  }
  
  public int getIterationListSize() {
    return iterationListSize.getInt();
  }
  
  public IntKeyList.Walker getLstThresholds(){
    return lstThresholdsArray.getWalker();
  }
  
  private void reset() {
    parserIterationListSize.reset();
    parserLstThresholdsArray.reset();
    iterationListSize.reset();
    lstThresholdsArray.reset();
  }
  
  private void store(final Properties props, String prepend) {
    prepend = createPrepend(prepend);
    parserIterationListSize.store(props,prepend);
    parserLstThresholdsArray.store(props,prepend);
    iterationListSize.store(props,prepend);
    lstThresholdsArray.store(props,prepend);
  }


  private void load(final Properties props, String prepend) {
    reset();
    prepend = createPrepend(prepend);
    parserIterationListSize.load(props,prepend);
    parserLstThresholdsArray.load(props,prepend);
    iterationListSize.load(props,prepend);
    lstThresholdsArray.load(props,prepend);
  }
  
  private String createPrepend(final String prepend) {
    if (prepend == "") {
      return KEY;
    }
    return prepend + "." + KEY;
  }
}
