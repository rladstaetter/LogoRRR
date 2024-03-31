package app.logorrr.services

import app.logorrr.services.fileservices.LogoRRRFileOpenService
import app.logorrr.services.hostservices.LogoRRRHostServices

case class LogoRRRServices(hostServices: LogoRRRHostServices
                           , fileOpenService: LogoRRRFileOpenService
                           , isUnderTest: Boolean)
