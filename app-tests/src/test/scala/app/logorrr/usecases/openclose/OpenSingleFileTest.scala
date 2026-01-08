package app.logorrr.usecases.openclose

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import org.junit.jupiter.api.Test

/**
 * Checks if LogoRRR can open a file via the 'open file' menu
 */
class OpenSingleFileTest extends SingleFileApplicationTest(TestFiles.simpleLog0):

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openFileTest(): Unit = openFile(fileId)


