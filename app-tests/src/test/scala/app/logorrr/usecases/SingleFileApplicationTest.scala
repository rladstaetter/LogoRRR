package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.io.FileId
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenSingleFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.CanStartApplication

import java.nio.file.Path


/**
 * Test which work with a single file can extend this test
 */
class SingleFileApplicationTest(val path: Path)
  extends TestFxBaseApplicationTest
    with CanStartApplication {

  protected val fileId = FileId(path)

  final def services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new OpenSingleFileService(Option(path))
    , isUnderTest = true)


}

