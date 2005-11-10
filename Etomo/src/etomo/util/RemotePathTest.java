package etomo.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import etomo.BaseManager;
import etomo.EtomoDirector;
import etomo.process.SystemProcessException;
import etomo.type.AxisID;
import etomo.ui.Autodoc;
import etomo.ui.AutodocTokenizer;
import etomo.ui.ProcessorTable;

import junit.framework.TestCase;

/**
 * <p>Description: JUnit tests for RemotePath.</p>
 * 
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organization:
 * Boulder Laboratory for 3-Dimensional Electron Microscopy of Cells (BL3DEM),
 * University of Colorado</p>
 * 
 * @author $Author$
 * 
 * @version $Revision$
 */
public class RemotePathTest extends TestCase {
  public static final String rcsid = "$Id$";

  private static final BaseManager MANAGER = EtomoDirector.getInstance()
      .getCurrentTestManager();
  private static final File TEST_DIR = new File(UtilTests.TEST_ROOT_DIR,
      "RemotePath");
  private static final String TEST_FILE_NAME = DatasetFiles
      .getAutodocName(RemotePath.AUTODOC);
  //end of each local path
  private static final String PATH = "/dir/embedded space dir";
  //path to test failure to find rule
  private static final String UNKNOWN_LOCAL_PATH = "/unknown/var/automount/home"
      + PATH;
  //rule indices
  private static final int MOUNT_NAME_RULE0 = 0;//remote has %mountname
  private static final int MOUNT_NAME_RULE1 = 1;//remote has %mountname
  private static final int MOUNT_NAME_RULE2 = 2;//remote has %mountname
  private static final int SPECIFIC_RULE = 3;//local is /private/var/automount/home
  private static final int LESS_SPECIFIC_RULE = 4;//local is /private/var/automount
  private static final int GENERAL_RULE = 5;//local is /private/var
  //local rules
  private static final String[] LOCAL_MOUNT_RULES = { "/localscratcha",
      "/localscratchb", "/localscratch", "/private/var/automount/home",
      "/private/var/automount", "/private/var" };
  //start and end strings for remote rules using %mountname
  private static final String[] START_REMOTE_MOUNT_NAME_RULES = { "/scratch/",
      "/", "/scratch/" };
  private static final String[] END_REMOTE_MOUNT_NAME_RULES = { "", "",
      "/scratch" };
  //remote rules
  private static final String[] REMOTE_MOUNT_RULES = {
      START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE0]
          + RemotePath.MOUNT_NAME_TAG
          + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE0],
      START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE1]
          + RemotePath.MOUNT_NAME_TAG
          + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE1],
      START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE2]
          + RemotePath.MOUNT_NAME_TAG
          + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE2], "/home", "/scratch",
      "/gen" };

  private String oldAutodocDirProperty = null;
  private String sectionName = null;
  private String mountName = null;
  private String hostName = null;
  private String strippedHostName = null;

  /**
   * Writes to a new a cpu.adoc file in a subdirectory of TEST_DIR.  Changes the
   * format of the file based on the parameters.
   * 
   * Mount rule possibilities:
   * Only global rules:  globalRules = true, sectionRules = false
   * Global and section rules - globalRules = true, sectionRules = true
   * Only section rules - globalRules = false, sectionRules = true
   * 
   * Section possibilities:
   * Included:  section = true
   * Not included:  section = false
   * 
   * Section name possibilities:
   * Computer name:  computerName = true
   * Localhost:  computerName = false
   * Full computer name:  computerName = true, fullSectionName = true
   * Stripped computer name:  computerName = true, fullSectionName = false
   * 
   * Mount name possibilities:
   * Mount name:  mountName = true
   * No mount name:  mountName = false
   * 
   * @param testDirName - subdirectory for testing
   * @param ruleStartNumber - starting number for mount rules
   * @param globalRules
   * @param sectionRules
   * @param section
   * @param computerName
   * @param fullSectionName
   * @param mountName
   * @throws IOException
   */
  private final void writeFile(String testDirName, int ruleStartNumber,
      boolean globalRules, boolean sectionRules, boolean section,
      boolean computerName, boolean fullSectionName, boolean mountName)
      throws IOException {
    int ruleNumber = ruleStartNumber;
    BufferedWriter bufferedWriter = setUpTestFile(testDirName);
    if (globalRules) {
      //add global rules
      //always add the third mount name rule here, if there are global rules
      if (!sectionRules) {
        //add the first two mount name rules here, if there are no sections
        //the specific rule should be checked before the general rule
        addMountRule(bufferedWriter, MOUNT_NAME_RULE0, ruleNumber++);
        addMountRule(bufferedWriter, MOUNT_NAME_RULE1, ruleNumber++);
      }
      //always add the third mountname rule here, if there are global rules
      addMountRule(bufferedWriter, MOUNT_NAME_RULE2, ruleNumber++);
      if (!sectionRules) {
        //add the specific mount rules here, if there are no sections
        //the specific rule should be checked before the general rule
        addMountRule(bufferedWriter, SPECIFIC_RULE, ruleNumber++);
        addMountRule(bufferedWriter, LESS_SPECIFIC_RULE, ruleNumber++);
      }
      //always add the general mount rule here, if there are global rules
      addMountRule(bufferedWriter, GENERAL_RULE, ruleNumber++);
    }
    if (section && sectionRules) {
      addSection(bufferedWriter, computerName, fullSectionName, mountName);
    }
    //add section level rules
    if (sectionRules) {
      ruleNumber = ruleStartNumber;
      //always add the first two mount name rules here, if sectionRules is true
      //this tests whether the section rules are applied before the global rules
      addMountRule(bufferedWriter, MOUNT_NAME_RULE0, ruleNumber++);
      addMountRule(bufferedWriter, MOUNT_NAME_RULE1, ruleNumber++);
      if (!globalRules) {
        //add the third mount name rule to the section, if there are no global
        //rules
        addMountRule(bufferedWriter, MOUNT_NAME_RULE2, ruleNumber++);
      }
      //always add the specific rule to the section, if sectionRules is true
      //this tests whether the section rules are applied before the global rules
      addMountRule(bufferedWriter, SPECIFIC_RULE, ruleNumber++);
      addMountRule(bufferedWriter, LESS_SPECIFIC_RULE, ruleNumber++);
      if (!globalRules) {
        //add the general mount rule to the section, if there are no global rules
        addMountRule(bufferedWriter, GENERAL_RULE, ruleNumber++);
      }
    }
    //add the section here, if there are no section level rules
    if (section && !sectionRules) {
      addSection(bufferedWriter, computerName, fullSectionName, mountName);
    }
    bufferedWriter.close();
  }

  /**
   * @see writeFile(String, int, boolean, boolean, boolean, boolean, boolean, 
   *                boolean)
   * @param testDirName
   * @param globalRules
   * @param sectionRules
   * @param section
   * @param computerName
   * @param fullSectionName
   * @param mountName
   * @throws IOException
   */
  private final void writeFile(String testDirName, boolean globalRules,
      boolean sectionRules, boolean section, boolean computerName,
      boolean fullSectionName, boolean mountName) throws IOException {
    writeFile(testDirName, 1, globalRules, sectionRules, section, computerName,
        fullSectionName, mountName);
  }

  /**
   * Writes to a new a cpu.adoc file in a subdirectory of TEST_DIR with entries
   * that will cause a global mount rule to be overridden.

   * @param testDirName
   * @throws IOException
   */
  private final void writeOverrideFile(String testDirName) throws IOException {
    BufferedWriter bufferedWriter = setUpTestFile(testDirName);
    //add global mount rule
    addMountRule(bufferedWriter, MOUNT_NAME_RULE2, 1);
    //start section
    addSection(bufferedWriter, true, false, false);
    //add override of global mount rule
    addMountRule(bufferedWriter, MOUNT_NAME_RULE2, 1, LOCAL_MOUNT_RULES,
        RemotePath.LOCAL);
    addMountRule(bufferedWriter, MOUNT_NAME_RULE2, 1, LOCAL_MOUNT_RULES,
        RemotePath.REMOTE);
    bufferedWriter.close();
  }

  /**
   * Writes to a new a cpu.adoc file in a subdirectory of TEST_DIR with entries
   * that will cause RemotePath to fail.
   * 
   * @throws IOException
   * @param testDirName
   */
  private final void writeBadFile(String testDirName) throws IOException {
    BufferedWriter bufferedWriter = setUpTestFile(testDirName);
    //should fail - local rule must exist
    int index = MOUNT_NAME_RULE0;
    addMountRule(bufferedWriter, index, index + 1, REMOTE_MOUNT_RULES,
        RemotePath.REMOTE);
    //should fail - remote rule must exist
    index = MOUNT_NAME_RULE1;
    addMountRule(bufferedWriter, index, index + 1, LOCAL_MOUNT_RULES,
        RemotePath.LOCAL);
    //should fail - local rule must not have an empty value
    index = MOUNT_NAME_RULE2;
    bufferedWriter.write(RemotePath.MOUNT_RULE + '.'
        + String.valueOf(index + 1) + '.' + RemotePath.LOCAL + ' '
        + AutodocTokenizer.DEFAULT_DELIMITER);
    bufferedWriter.newLine();
    addMountRule(bufferedWriter, index, index + 1, REMOTE_MOUNT_RULES,
        RemotePath.REMOTE);
    //should fail - remote rule must not have an empty value
    index = SPECIFIC_RULE;
    addMountRule(bufferedWriter, index, index + 1, LOCAL_MOUNT_RULES,
        RemotePath.LOCAL);
    bufferedWriter.write(RemotePath.MOUNT_RULE + '.'
        + String.valueOf(index + 1) + '.' + RemotePath.REMOTE + ' '
        + AutodocTokenizer.DEFAULT_DELIMITER);
    bufferedWriter.newLine();
    //should fail - local rule must be an absolute path
    index = LESS_SPECIFIC_RULE;
    bufferedWriter.write(RemotePath.MOUNT_RULE + '.'
        + String.valueOf(index + 1) + '.' + RemotePath.LOCAL + ' '
        + AutodocTokenizer.DEFAULT_DELIMITER + ' '
        + LOCAL_MOUNT_RULES[index].substring(1));
    bufferedWriter.newLine();
    addMountRule(bufferedWriter, index, index + 1, REMOTE_MOUNT_RULES,
        RemotePath.REMOTE);
    //should fail - remote rule must be an absolute path
    index = GENERAL_RULE;
    addMountRule(bufferedWriter, index, index + 1, LOCAL_MOUNT_RULES,
        RemotePath.LOCAL);
    bufferedWriter.write(RemotePath.MOUNT_RULE + '.'
        + String.valueOf(index + 1) + '.' + RemotePath.REMOTE + ' '
        + AutodocTokenizer.DEFAULT_DELIMITER + ' '
        + REMOTE_MOUNT_RULES[index].substring(1));
    bufferedWriter.newLine();
    addSection(bufferedWriter, true, false, false);
    bufferedWriter.close();
  }

  /**
   * Adds a local mountrule attribute and a remote mountrule attribute to
   * bufferedWriter.  RuleOrder is the mountrule number.
   * Example:
   * mountrule.1.local = /localscratch
   * mountrule.1.romote = /scratch/%mountname
   * 
   * @param bufferedWriter
   * @param index - index to LOCAL_MOUNT_RULES and REMOTE_MOUNT_RULES
   * @param ruleOrder
   * @throws IOException
   */
  private final void addMountRule(BufferedWriter bufferedWriter, int index,
      int ruleOrder) throws IOException {
    //add local mount rule
    addMountRule(bufferedWriter, index, ruleOrder, LOCAL_MOUNT_RULES,
        RemotePath.LOCAL);
    //add remote mount rule
    addMountRule(bufferedWriter, index, ruleOrder, REMOTE_MOUNT_RULES,
        RemotePath.REMOTE);
  }

  /**
   * Adds a local or remote mountrule attribute to bufferedWriter.  RuleOrder is
   * the mountrule number.
   * Example:
   * mountrule.1.local = /localscratch
   * mountrule.1.romote = /scratch/%mountname
   * 
   * @param bufferedWriter
   * @param index - index to LOCAL_MOUNT_RULES and REMOTE_MOUNT_RULES
   * @param ruleOrder
   * @param mountRules
   * @param type
   * @throws IOException
   */
  private final void addMountRule(BufferedWriter bufferedWriter, int index,
      int ruleOrder, String[] mountRules, String type) throws IOException {
    bufferedWriter.write(RemotePath.MOUNT_RULE + '.' + ruleOrder + '.' + type
        + ' ' + AutodocTokenizer.DEFAULT_DELIMITER + ' ' + mountRules[index]);
    bufferedWriter.newLine();
  }

  /**
   * Adds a Computer section the bufferWriter for the current host computer.
   * Changes the format of the file based on the parameters.
   * Example:
   * [Computer = frodo.colorado.edu]
   * mountname = frodo
   * 
   * @see writeFile()
   * @param bufferedWriter
   * @param computerName
   * @param fullSectionName
   * @param mountName
   * @throws IOException
   */
  private final void addSection(BufferedWriter bufferedWriter,
      boolean computerName, boolean fullSectionName, boolean mountName)
      throws IOException {
    //set the hostname
    hostName = RemotePath.INSTANCE.getHostName_test(MANAGER, AxisID.ONLY);
    strippedHostName = hostName.substring(0, hostName.indexOf('.'));
    //set the section name
    if (computerName) {
      if (fullSectionName) {
        sectionName = hostName;
      }
      else {
        sectionName = strippedHostName;
      }
    }
    else {
      sectionName = RemotePath.LOCAL_HOST;
    }
    //write the section
    bufferedWriter.write(AutodocTokenizer.OPEN_CHAR
        + ProcessorTable.SECTION_TYPE + ' '
        + AutodocTokenizer.DEFAULT_DELIMITER + ' ' + sectionName
        + AutodocTokenizer.CLOSE_CHAR);
    bufferedWriter.newLine();
    if (!mountName) {
      this.mountName = null;
      return;
    }
    //set the mount name
    this.mountName = strippedHostName;
    //write the mount name
    bufferedWriter.write(RemotePath.MOUNT_NAME + +' '
        + AutodocTokenizer.DEFAULT_DELIMITER + ' ' + this.mountName);
    bufferedWriter.newLine();
  }

  /**
   * Makes sure the test directory exists and does not contain a cpu.adoc file.
   * Resets RemotePath and Autodoc.CPU.  Sets the test directory in Autodoc.
   * 
   * @throws Exception
   */
  protected void setUp() throws Exception {
    super.setUp();
    setUpDirectory(TEST_DIR);
    RemotePath.INSTANCE.reset_test();
    Autodoc.resetInstance_test(RemotePath.AUTODOC);
  }

  /**
   * Creates a the directory dir and makes sure it is correctly set up to be a
   * test directory.
   * @param dir
   */
  private void setUpDirectory(File dir) {
    if (dir.isFile()) {
      dir.delete();
    }
    if (!dir.exists()) {
      dir.mkdirs();
    }
    assertTrue(dir.exists() && dir.isDirectory() && dir.canRead()
        && dir.canWrite());
  }

  /**
   * Creates a subdirectory and makes sure it is correctly set up to be a test
   * directory.  Sets the test directory in Autodoc.
   * @param testDirName
   * @throws IOException
   */
  private final File setUpTestDirectory(String testDirName) throws IOException {
    File testDir = new File(TEST_DIR, testDirName);
    setUpDirectory(testDir);
    Autodoc.setTestDir(testDir.getAbsolutePath());
    return testDir;
  }

  /**
   * Creates a subdirectory and makes sure it is correctly set up to be a test
   * directory.  Sets the test directory in Autodoc.  Creates a cpu.adoc and
   * returns a BufferedWriter for it. 
   * @param testDirName
   * @return
   * @throws IOException
   */
  private final BufferedWriter setUpTestFile(String testDirName)
      throws IOException {
    File testDir = setUpTestDirectory(testDirName);
    File testFile = new File(testDir, TEST_FILE_NAME);
    testFile.delete();
    return new BufferedWriter(new FileWriter(testFile));
  }

  /**
   * Tests RemotePath().
   * RemotePath doesn't load mount rules
   */
  public final void test_RemotePath() {
    assertFalse(RemotePath.INSTANCE.isMountRulesLoaded_test());
    assertTrue(RemotePath.INSTANCE.localMountRulesIsNull_test());
    assertTrue(RemotePath.INSTANCE.remoteMountRulesIsNull_test());
  }

  /**
   * Tests getRemotePath().
   * getRemotePath only loads rules once
   */
  public final void test_getRemotePath_onlyLoadRulesOnce() throws IOException {
    setUpTestFile("test_getRemotePath_onlyLoadRulesOnce");
    assertNoRulesLoaded();
    writeFile("test_getRemotePath_onlyLoadRulesOnce", true, true, true, true,
        false, false);
    assertNoRulesLoaded();
  }

  /**
   * Tests getRemotePath().
   * getRemotePath returns null when no autodoc
   * 
   * getRemotePath does not throw an exception when no autodoc
   */
  public final void test_getRemotePath_noAutodoc() throws IOException {
    setUpTestDirectory("test_getRemotePath_noAutodoc");
    assertNoRulesLoaded();
  }

  /**
   * Asserts the no rules have been loaded and running getRemotePath() fails.
   */
  private final void assertNoRulesLoaded() {
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[GENERAL_RULE] + PATH, AxisID.ONLY));
    assertTrue(RemotePath.INSTANCE.isMountRulesLoaded_test());
    assertFalse(RemotePath.INSTANCE.localMountRulesIsNull_test());
    assertEquals(RemotePath.INSTANCE.getLocalMountRulesSize_test(), 0);
    assertFalse(RemotePath.INSTANCE.remoteMountRulesIsNull_test());
    assertEquals(RemotePath.INSTANCE.getRemoteMountRulesSize_test(), 0);
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[LESS_SPECIFIC_RULE] + PATH, AxisID.ONLY));
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[SPECIFIC_RULE] + PATH, AxisID.ONLY));
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE0] + PATH, AxisID.ONLY));
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE1] + PATH, AxisID.ONLY));
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + PATH, AxisID.ONLY));
  }

  /**
   * Tests getRemotePath().
   * getRemotePath returns null when no rules in autodoc
   * 
   * getRemotePath does not throw an exception when no rules in autodoc
   */
  public final void test_getRemotePath_noRules() throws IOException {
    writeFile("test_getRemotePath_noRules", false, false, true, true, false,
        false);
    assertNoRulesLoaded();
  }

  /**
   * Tests getRemotePath().
   * getRemotePath returns null when fails to translate the path
   */
  public final void test_getRemotePath_unknownPath()
      throws InvalidParameterException, SystemProcessException, IOException {
    writeFile("test_getRemotePath_unknownPath", true, false, true, true, false,
        false);
    //check general rule
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER, UNKNOWN_LOCAL_PATH,
        AxisID.ONLY));
    assertRulesLoaded();
  }

  /**
   * Asserts that mount rules have been loaded into RemotePath.
   */
  private final void assertRulesLoaded() {
    assertTrue(RemotePath.INSTANCE.isMountRulesLoaded_test());
    assertFalse(RemotePath.INSTANCE.localMountRulesIsNull_test());
    assertFalse(RemotePath.INSTANCE.remoteMountRulesIsNull_test());
    assertTrue(RemotePath.INSTANCE.getLocalMountRulesSize_test() > 0
        || RemotePath.INSTANCE.getRemoteMountRulesSize_test() > 0);
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can find global rules
   * 
   * getRemotePath can search for section with stripped hostname
   * getRemotePath returns remote path when local path is found
   * getRemotePath returns section name as mount name
   */
  public final void test_getRemotePath_globalRules() throws IOException {
    writeFile("test_getRemotePath_globalRules", true, false, true, true, false,
        false);
    assertPathsFound();
    assertMountNameFound(strippedHostName);
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can't use %mountname if there is no section
   * 
   * getRemotePath can find global rules
   * getRemotePath can search for section with stripped hostname
   * getRemotePath returns remote path when local path is found
   */
  public final void test_getRemotePath_globalRulesNoSection()
      throws IOException {
    writeFile("test_getRemotePath_globalRulesNoSection", true, false, false,
        true, false, false);
    assertPathsFound();
    assertMountNameFailed();
  }

  /**
   * Asserts that paths which do not contain %mountname can be found and turned
   * into remote paths.
   * 
   * getRemotePath can find and replace remote paths with local paths
   * The last directory in path can be a partial match:
   *  local = /localscratch
   *  remote = /scratch
   *  result for /localscratch1 is /scratch1
   */
  private final void assertPathsFound() {
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[GENERAL_RULE] + PATH, AxisID.ONLY),
        REMOTE_MOUNT_RULES[GENERAL_RULE] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[GENERAL_RULE] + "10" + PATH, AxisID.ONLY),
        REMOTE_MOUNT_RULES[GENERAL_RULE] + "10" + PATH);
    assertRulesLoaded();
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[LESS_SPECIFIC_RULE] + PATH, AxisID.ONLY),
        REMOTE_MOUNT_RULES[LESS_SPECIFIC_RULE] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[LESS_SPECIFIC_RULE] + "100" + PATH, AxisID.ONLY),
        REMOTE_MOUNT_RULES[LESS_SPECIFIC_RULE] + "100" + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[SPECIFIC_RULE] + PATH, AxisID.ONLY),
        REMOTE_MOUNT_RULES[SPECIFIC_RULE] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[SPECIFIC_RULE] + "1000" + PATH, AxisID.ONLY),
        REMOTE_MOUNT_RULES[SPECIFIC_RULE] + "1000" + PATH);
  }

  /**
   * Asserts that paths which contain %mountname can be found and turned into
   * remote paths.
   * 
   * getRemotePath can find and substitute mount names
   * The last directory in path can be a partial match:
   *  local = /localscratch
   *  remote = /%mountname
   *  result for /localscratch1 is /bebop1
   * 
   * @param mountName - expected mountName
   */
  private final void assertMountNameFound(String mountName) {
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE0] + PATH, AxisID.ONLY),
        START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE0] + mountName
            + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE0] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE0] + "10" + PATH, AxisID.ONLY),
        START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE0] + mountName
            + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE0] + "10" + PATH);

    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE1] + PATH, AxisID.ONLY),
        START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE1] + mountName
            + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE1] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE1] + "100" + PATH, AxisID.ONLY),
        START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE1] + mountName
            + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE1] + "100" + PATH);

    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + PATH, AxisID.ONLY),
        START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE2] + mountName
            + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE2] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + "1000" + PATH, AxisID.ONLY),
        START_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE2] + mountName
            + END_REMOTE_MOUNT_NAME_RULES[MOUNT_NAME_RULE2] + "1000" + PATH);
  }

  /**
   * Asserts that paths which contain %mountname cannot be found.
   */
  private final void assertMountNameFailed() {
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE0] + PATH, AxisID.ONLY));
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE1] + PATH, AxisID.ONLY));
    assertNull(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + PATH, AxisID.ONLY));
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can find section-level rules
   * 
   * getRemotePath can search for section with stripped hostname
   * getRemotePath returns remote path when local path is found
   * getRemotePath returns section name as mount name
   */
  public final void test_getRemotePath_sectionRules() throws IOException {
    System.out.println();
    writeFile("test_getRemotePath_sectionRules", false, true, true, true,
        false, false);
    assertPathsFound();
    assertMountNameFound(strippedHostName);
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can find section-level rules
   * getRemotePath can search on hostname
   * 
   * getRemotePath returns remote path when local path is found
   * getRemotePath returns section name as mount name
   */
  public final void test_getRemotePath_sectionRulesFullHostName()
      throws IOException {
    writeFile("test_getRemotePath_sectionRulesFullHostName", false, true, true,
        true, true, false);
    assertPathsFound();
    assertMountNameFound(hostName);
  }

  /**
   * Tests getRemotePath().
   * getRemotePath tests local rules before global rules
   * 
   * getRemotePath can find global rules
   * getRemotePath can find section-level rules
   * getRemotePath can search for section with stripped hostname
   * getRemotePath returns remote path when local path is found
   * getRemotePath returns section name as mount name
   */
  public final void test_getRemotePath_globalAndSectionRules()
      throws IOException {
    writeFile("test_getRemotePath_globalAndSectionRules", true, true, true,
        true, false, false);
    assertPathsFound();
    assertMountNameFound(strippedHostName);
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can search for section with localhost if the hostname is not
   * found
   * 
   * getRemotePath can find global rules
   * getRemotePath can find section-level rules
   * getRemotePath returns remote path when local path is found
   * getRemotePath returns section name as mount name
   * getRemotePath tests local rules before global rules
   */
  public final void test_getRemotePath_localHost() throws IOException {
    writeFile("test_getRemotePath_localHost", true, true, true, false, false,
        true);
    assertPathsFound();
    assertMountNameFound(strippedHostName);
  }

  /**
   * Tests getRemotePath().
   * A localhost section must have a mountname attribute to use %mountname
   * 
   * getRemotePath can search for section with localhost if the hostname is not
   * found
   * getRemotePath can find global rules
   * getRemotePath can find section-level rules
   * getRemotePath returns remote path when local path is found
   * getRemotePath tests local rules before global rules
   */
  public final void test_getRemotePath_localHostWithoutMountName()
      throws IOException {
    writeFile("test_getRemotePath_localHostWithoutMountName", true, true, true,
        false, false, false);
    assertPathsFound();
    assertMountNameFailed();
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can search on hostname
   * getRemotePath returns section name as mount name
   * 
   * getRemotePath can find global rules
   * getRemotePath can find section-level rules
   * getRemotePath returns remote path when local path is found
   * getRemotePath tests local rules before global rules
   */
  public final void test_getRemotePath_fullSectionName() throws IOException {
    writeFile("test_getRemotePath_fullSectionName", true, true, true, true,
        true, false);
    assertPathsFound();
    assertMountNameFound(hostName);
  }

  /**
   * Tests getRemotePath().
   * getRemotePath can find and return mountname
   * 
   * getRemotePath can find global rules
   * getRemotePath can find section-level rules
   * getRemotePath can search on hostname
   * getRemotePath tests local rules before global rules
   */
  public final void test_getRemotePath_mountName() throws IOException {
    writeFile("test_getRemotePath_mountName", true, true, true, true, true,
        true);
    assertPathsFound();
    assertMountNameFound(strippedHostName);
  }

  /**
   * Tests getRemotePath().
   * bad mount rules are not loaded
   */
  public final void test_getRemotePath_badRules() throws IOException {
    writeBadFile("test_getRemotePath_badRules");
    assertNoRulesLoaded();
  }

  /**
   * Tests getRemotePath().
   * a global mount rule can be overridden by a section-level mount rule
   */
  public final void test_getRemotePath_overrideMountRule() throws IOException {
    writeOverrideFile("test_getRemotePath_overrideMountRule");
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + PATH, AxisID.ONLY),
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + PATH);
    assertEquals(RemotePath.INSTANCE.getRemotePath(MANAGER,
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + "10" + PATH, AxisID.ONLY),
        LOCAL_MOUNT_RULES[MOUNT_NAME_RULE2] + "10" + PATH);
  }

  /**
   * Tests getRemotePath().
   * mount rule numbers must start from 1 in each area
   */
  public final void test_getRemotePath_ruleNumbers() throws IOException {
    writeFile("test_getRemotePath_ruleNumbers", 2, true, true, true, true,
        true, true);
    assertNoRulesLoaded();
  }
}
/**
 * <p> $Log$ </p>
 */