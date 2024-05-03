package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.EmptyFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.steps.CanStartApplication


/**
 * Start LogoRRR empty
 */
class StartEmptyApplicationTest
  extends TestFxBaseApplicationTest
    with CanStartApplication {

  final def services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new EmptyFileService
    , isUnderTest = true)


}

