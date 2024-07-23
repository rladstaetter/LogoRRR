package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.io.FileId
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.SingleFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.CanStartApplication

/**
 * Test which work with a single file can extend this test
 */
class SingleFileApplicationTest(val fileId: FileId)
  extends TestFxBaseApplicationTest
    with CanStartApplication {

  final def services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new SingleFileService(fileId)
    , isUnderTest = true)


}

