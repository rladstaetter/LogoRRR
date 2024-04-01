package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenSingleFileService
import app.logorrr.services.hostservices.MockHostServices
import javafx.stage.Stage

import java.nio.file.Path

/**
 * Extend this class which setups environment for LogoRRR and provides various helper methods
 */
class SingleFileApplicationTest(val path: Path) extends TestFxBaseApplicationTest {

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    val services = LogoRRRServices(new MockHostServices
      , new OpenSingleFileService(Option(path))
      , isUnderTest = true)
    LogoRRRApp.start(stage, Settings.Default, services)
    stage.toFront()
  }

}

