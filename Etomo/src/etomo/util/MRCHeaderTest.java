
package etomo.util;

import java.io.File;
import java.io.IOException;

import etomo.EtomoDirector;
import etomo.process.SystemProcessException;

import junit.framework.TestCase;

/**
 * <p>Description: </p>
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
 * <p> Revision 3.11  2004/11/24 22:16:19  sueh
 * <p> bug# 520 Getting the root test directory name from UtilTests.
 * <p>
 * <p> Revision 3.10  2004/11/23 00:37:01  sueh
 * <p> bug# 520 Using get and setPropertyUserDir instead of Property.  Don't
 * <p> use File.separator with propertyUserDir since it may end in "/".  Construct
 * <p> a new file with originalDirectory as the base directory and get the absolute
 * <p> file.
 * <p>
 * <p> Revision 3.9  2004/11/20 00:12:00  sueh
 * <p> bug# 520 merging Etomo_3-4-6_JOIN branch to head.
 * <p>
 * <p> Revision 3.8.4.1  2004/09/03 21:19:14  sueh
 * <p> bug# 520 getting app mgr from EtomoDirector
 * <p>
 * <p> Revision 3.8  2004/04/06 02:49:03  rickg
 * <p> Use TestUtilities methods
 * <p>
 * <p> Revision 3.7  2004/04/02 18:44:43  rickg
 * <p> Uses TestUtilities class
 * <p>
 * <p> Revision 3.6  2004/02/13 00:08:13  rickg
 * <p> Moved checkouts out of setup and into individual tests.
 * <p>
 * <p> Revision 3.5  2004/02/10 04:53:50  rickg
 * <p> Changed CVS commans to export
 * <p>
 * <p> Revision 3.4  2004/01/27 18:07:05  rickg
 * <p> Unset debug mode for tests, too much cruft
 * <p>
 * <p> Revision 3.3  2004/01/16 20:41:22  rickg
 * <p> Open up the application manager in test mode
 * <p>
 * <p> Revision 3.2  2004/01/16 18:26:38  rickg
 * <p> Added checkout of testHeader to appropriate directories
 * <p>
 * <p> Revision 3.1  2004/01/13 22:36:47  rickg
 * <p> Creates it own ApplicationManager for static function access
 * <p> Added a test that includes a space in the directory name
 * <p>
 * <p> Revision 3.0  2003/11/07 23:19:01  rickg
 * <p> Version 1.0.0
 * <p>
 * <p> Revision 2.0  2003/01/24 20:30:31  rickg
 * <p> Single window merge to main branch
 * <p>
 * <p> Revision 1.1.2.1  2003/01/24 18:45:05  rickg
 * <p> Single window GUI layout initial revision
 * <p>
 * <p> Revision 1.1  2002/10/03 18:57:38  rickg
 * <p> Initial revision
 * <p> </p>
 */
public class MRCHeaderTest extends TestCase {
  private static final String testDirectory1 = new String("Test");
  private static final String testDirectory2 = new String("With Spaces");
  private static final String headerTestStack = "headerTest.st";
  MRCHeader emptyFilename = new MRCHeader("");
  MRCHeader badFilename = new MRCHeader(UtilTests.testRoot + testDirectory1
      + "/non_existant_image_file");
  MRCHeader mrcHeader = new MRCHeader(UtilTests.testRoot + testDirectory1
      + "/headerTest.st");
  MRCHeader mrcWithSpaces = new MRCHeader(UtilTests.testRoot + testDirectory2
      + "/headerTest.st");

  /**
   * Constructor for MRCHeaderTest.
   * @param arg0
   */
  public MRCHeaderTest(String arg0) {
    super(arg0);
  }

  /**
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();

  }

  /**
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testMRCHeader() {
  }

  public void testEmptyFilename() throws InvalidParameterException {
    try {
      emptyFilename.read();
      fail("Should rise IOException exception");
    }
    catch (IOException success) {
    }
  }

  public void testReadBadFilename() {
    // First test, should throw an exception because the image stack is not
    // present
    boolean exceptionThrown = false;
    try {
      badFilename.read();
          }
    catch (Exception except) {
      exceptionThrown = true;
      assertEquals("Incorrect exception thrown",
        "etomo.util.InvalidParameterException", except.getClass().getName());
    }
    finally {
      if (!exceptionThrown) {
        fail("Exception not thrown");
      }
    }
  }

  public void testRead() throws IOException, InvalidParameterException {
    //  Create the test directory
    TestUtilites.makeDirectories(UtilTests.testRoot + testDirectory1);

    // Check out the test header stack into the required directories
    try {
      TestUtilites.checkoutVector(new File(EtomoDirector.getInstance()
          .getCurrentPropertyUserDir(), UtilTests.testRoot).getAbsolutePath(),
          testDirectory1, headerTestStack);
    }
    catch (SystemProcessException except) {
      System.err.println(except.getMessage());
      fail("Error checking out test vector:\n" + except.getMessage());
    }

    mrcHeader.read();
    assertEquals("Incorrect column count", 512, mrcHeader.getNColumns());
    assertEquals("Incorrect row count", 512, mrcHeader.getNRows());
    assertEquals("Incorrect section count", 1, mrcHeader.getNSections());
  }

  public void testWithSpaces() throws IOException, InvalidParameterException {
    //  Create the test directory
    TestUtilites.makeDirectories(UtilTests.testRoot + testDirectory2);

    // Check out the test header stack into the required directories
    try {
      TestUtilites.checkoutVector(EtomoDirector.getInstance()
          .getCurrentPropertyUserDir()
          + File.separator + UtilTests.testRoot, testDirectory2,
          "headerTest.st");
    }
    catch (SystemProcessException except) {
      System.err.println(except.getMessage());
      fail("Error checking out test vector:\n" + except.getMessage());
    }

    mrcWithSpaces.read();
    assertEquals("Incorrect column count", 512, mrcWithSpaces.getNColumns());
    assertEquals("Incorrect row count", 512, mrcWithSpaces.getNRows());
    assertEquals("Incorrect section count", 1, mrcWithSpaces.getNSections());
  }

}