package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.io.FileId
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.MockFileIdService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{CanStartApplication, VisibleItemActions}

/**
 * Extend this class if you have tests which involve more than one file
 *
 * @param files files which are supported for this test
 */
class MultipleFileApplicationTest(val files: Seq[FileId])
  extends TestFxBaseApplicationTest
    with CanStartApplication
    with VisibleItemActions {

  lazy val services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new MockFileIdService(files)
    , isUnderTest = true)

}
