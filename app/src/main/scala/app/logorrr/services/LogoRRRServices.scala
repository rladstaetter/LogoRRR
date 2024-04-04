package app.logorrr.services

import app.logorrr.conf.Settings
import app.logorrr.services.fileservices.LogoRRRFileOpenService
import app.logorrr.services.hostservices.LogoRRRHostServices

case class LogoRRRServices(settings: Settings
,                            hostServices: LogoRRRHostServices
                           , fileOpenService: LogoRRRFileOpenService
                           , isUnderTest: Boolean)
