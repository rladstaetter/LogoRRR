package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenMultipleFilesService
import app.logorrr.services.hostservices.MockHostServices
import javafx.stage.Stage

import java.nio.file.Path

/**
 * Extend this class if you have tests which involve more than one file
 *
 * @param files files which are supported for this test
 */
class MultipleFileApplicationTest(val files: Seq[Path]) extends TestFxBaseApplicationTest {

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    val services = LogoRRRServices(new MockHostServices
      , new OpenMultipleFilesService(files)
      , isUnderTest = true)
    LogoRRRApp.start(stage, Settings.Default, services)
    stage.toFront()
  }

}
