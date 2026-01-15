package app.logorrr.usecases

import app.logorrr.conf.{DefaultSearchTermGroups, FileId, Settings, TestSettings}
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.SingleFileIdService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.AppActions

/**
 * Test which work with a single file can extend this test
 */
class SingleFileApplicationTest(val fileId: FileId)
  extends TestFxBaseApplicationTest
    with AppActions:

  protected lazy val settings: Settings = TestSettings.Default


  final def services: LogoRRRServices = {
    // overriding default search term groups for tests
    DefaultSearchTermGroups.cpResource = TestSettings.cpResource
    LogoRRRServices(settings
      , new MockHostServices
      , new SingleFileIdService(fileId)
      , isUnderTest = true)
  }



