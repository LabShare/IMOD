package etomo.storage.autodoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import etomo.BaseManager;
import etomo.EtomoDirector;
import etomo.JUnitTests;
import etomo.process.SystemProcessException;
import etomo.storage.LogFile;
import etomo.type.AxisID;
import etomo.util.InvalidParameterException;
import etomo.util.TestUtilites;
import etomo.util.Utilities;
import junit.framework.TestCase;

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
public final class AutodocTest extends TestCase {
  public static final String rcsid = "$Id$";

  private static final String TEST_DIR_NAME = "Autodoc";

  private BaseManager manager;

  public AutodocTest(String test) {
    super(test);
  }

  public void setUp() throws Exception {
    super.setUp();
    File testDir = new File(AutodocTests.TEST_ROOT_DIR, TEST_DIR_NAME);
    testDir.mkdirs();
    EtomoDirector.createInstance_test(JUnitTests.ETOMO_ARGUMENTS);
    manager = (BaseManager) EtomoDirector.getInstance()
        .getCurrentManager_test();
    Autodoc.resetAbsoluteDir();
  }

  public void testCpu() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites
        .getVector(manager, AutodocTests.TEST_ROOT_DIR.getAbsolutePath(),
            TEST_DIR_NAME, "cpu.adoc"));
    assertFalse(autodoc.isError());
  }

  public void testMatlabParamFile() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getMatlabInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "master.prm"), false);
    //only use with getTestInstance
    //autodoc.runInternalTest(Autodoc.InternalTestType.PARSER,false,false);
    //only use with getInstance
    autodoc.printStoredData();
    assertFalse(autodoc.isError());
  }

  public void testBuild() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "build.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testBuilda() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "builda.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testBuildb() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "buildb.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testFineAlign() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "fine-align.adoc", true));
    //only use with getTestInstance
    //autodoc.runInternalTest(Autodoc.InternalTestType.PARSER,false,false);
    //only use with getInstance
    //autodoc.printStoredData();
    assertFalse(autodoc.isError());
  }

  public void testSetupRecon() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "setup-recon.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testSimple() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "simple.adoc"));
    //only use with getTestInstance
    //autodoc.runInternalTest(Autodoc.InternalTestType.PARSER,false,false);
    //only use with getInstance
    //autodoc.printStoredData();
    assertFalse(autodoc.isError());
  }

  public void testPreProc() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "pre-proc.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testPostProc() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "post-proc.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testUitest() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "uitest.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testTomoGen() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "tomo-gen.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testTomoPos() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "tomo-pos.adoc", true));
    assertFalse(autodoc.isError());
  }
/*
  public void testCleanUp() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "clean-up.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testFidModel() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "fid-model.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testTestb() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "testb.adoc", true));
    assertFalse(autodoc.isError());
  }

  public void testCombine() throws LogFile.ReadException, IOException,
      SystemProcessException, InvalidParameterException {
    Autodoc autodoc = Autodoc.getInstance(TestUtilites.getVector(manager,
        AutodocTests.TEST_ROOT_DIR.getAbsolutePath(), TEST_DIR_NAME,
        "combine.adoc", true));
    assertFalse(autodoc.isError());
  }
*/
  public void testBeadtrack() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    //TEMP
    if (Utilities.isWindowsOS()) {
      return;
    }
    Autodoc autodoc = Autodoc.getInstance(Autodoc.BEADTRACK, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testCcderaser() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.CCDERASER, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testCombineFft() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.COMBINE_FFT, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testDensmatch() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.DENS_MATCH, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testMtfFilter() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.MTF_FILTER, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testSolvematch() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.SOLVEMATCH, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testTiltalign() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.TILTALIGN, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }

  public void testTiltxcorr() throws FileNotFoundException, IOException,
      LogFile.ReadException {
    Autodoc autodoc = Autodoc.getInstance(Autodoc.TILTXCORR, AxisID.ONLY);
    assertFalse(autodoc.isError());
  }
}
/**
 * <p> $Log$
 * <p> Revision 1.12  2007/03/08 21:55:59  sueh
 * <p> bug# 964 Testing directly from the IMOD/Etomo/tests directory.  Adding more
 * <p> tests of .adoc files.
 * <p>
 * <p> Revision 1.11  2007/03/07 21:06:41  sueh
 * <p> bug# 964 Fixed printing.  Made internal tests runnable from unit tests.
 * <p>
 * <p> Revision 1.10  2007/03/05 21:28:55  sueh
 * <p> bug# 964 Added tests for cpu.adoc and master.prm.
 * <p>
 * <p> Revision 1.9  2007/03/01 01:19:05  sueh
 * <p> bug# 964 Added LogFile to PrimativeTokenizer.
 * <p>
 * <p> Revision 1.8  2006/11/18 01:16:36  sueh
 * <p> bug# 956 Temporarily not running problem tests on Windows.
 * <p>
 * <p> Revision 1.7  2006/11/16 23:42:46  sueh
 * <p> bug# 872 Set autodoc test dir to null to avoid changes made by previous test
 * <p> classes.
 * <p>
 * <p> Revision 1.6  2006/06/15 18:46:12  sueh
 * <p> bug# 876 Removed unnecessary commented out functions.
 * <p>
 * <p> Revision 1.5  2006/06/15 17:55:14  sueh
 * <p> bug# 876 Remove test against cpu.adoc because cpu.adoc is optional.
 * <p>
 * <p> Revision 1.4  2006/06/15 16:19:02  sueh
 * <p> bug# 876 testCpu():  cpu.adoc is optional so catch FileNotFoundException.
 * <p>
 * <p> Revision 1.3  2006/06/14 16:26:04  sueh
 * <p> bug# 852 Fixed problem with tests
 * <p>
 * <p> Revision 1.2  2006/06/14 00:31:25  sueh
 * <p> bug# 852 Added a test for setup-recon.adoc.
 * <p>
 * <p> Revision 1.1  2006/06/14 00:23:02  sueh
 * <p> bug# 852 Tests for Autodoc.  Parses autodocs and then checks the parser for an
 * <p> error.
 * <p> </p>
 */
