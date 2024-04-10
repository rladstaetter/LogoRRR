package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.{EmptyFileService, OpenSingleFileService}
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{CanOpenFile, CanStartApplication}

import java.nio.file.Path


/**
 * Test which work with a single file can extend this test
 */
class SingleFileApplicationTest(val path: Path)
  extends TestFxBaseApplicationTest
    with CanStartApplication
    with CanOpenFile {

  final def services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new OpenSingleFileService(Option(path))
    , isUnderTest = true)


}

class ShowAboutDialogTest extends TestFxBaseApplicationTest with CanStartApplication {

  final def services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new EmptyFileService
    , isUnderTest = true)

}