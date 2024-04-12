package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.OpenMultipleFilesService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{CanStartApplication, VisibleItemActions}

import java.nio.file.Path

/**
 * Extend this class if you have tests which involve more than one file
 *
 * @param files files which are supported for this test
 */
class MultipleFileApplicationTest(val files: Seq[Path])
  extends TestFxBaseApplicationTest
    with CanStartApplication
    with VisibleItemActions{

  val services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new OpenMultipleFilesService(files)
    , isUnderTest = true)

}
