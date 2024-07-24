package app.logorrr.services

import app.logorrr.conf.Settings
import app.logorrr.services.file.FileIdService
import app.logorrr.services.hostservices.LogoRRRHostServices

/**
 * Groups settings, acts as a kind of service provider interface
 *
 * @param settings      application settings
 * @param hostServices  services which need native interfaces which in turn need special capabilities which
 *                      have to be declared on packaging
 * @param fileIdService service to lookup file ids, also needed because of security/packaging
 * @param isUnderTest   helper flag which tells the application it runs in test mode
 */
case class LogoRRRServices(settings: Settings
                           , hostServices: LogoRRRHostServices
                           , fileIdService: FileIdService
                           , isUnderTest: Boolean)
