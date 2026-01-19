package app.logorrr.usecases

import app.logorrr.conf.{FileId, Settings, TestSettings}
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.MockFileIdService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.{AppActions, VisibleItemActions}

/**
 * Extend this class if you have tests which involve more than one file
 *
 * @param files files which are supported for this test
 */
class MultipleFileApplicationTest(val files: Seq[FileId])
  extends TestFxBaseApplicationTest
    with AppActions
    with VisibleItemActions:

  lazy val services: LogoRRRServices = LogoRRRServices(TestSettings.Default
    , new MockHostServices
    , new MockFileIdService(files)
    , isUnderTest = true)

