package app.logorrr.usecases

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.file.EmptyFileIdService
import app.logorrr.services.hostservices.MockHostServices

/**
 * Start LogoRRR without any log files loaded
 */
class StartEmptyApplicationTest extends TestFxBaseApplicationTest:

  final def services: LogoRRRServices = LogoRRRServices(Settings.Default
    , new MockHostServices
    , new EmptyFileIdService
    , isUnderTest = true)



