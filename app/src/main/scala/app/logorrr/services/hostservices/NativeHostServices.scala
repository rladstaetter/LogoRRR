package app.logorrr.services.hostservices

import app.logorrr.OsxBridge
import app.logorrr.util.OsUtil
import javafx.application.HostServices

class NativeHostServices(hostServices: => HostServices) extends LogoRRRHostServices {

  override def showDocument(url: String): Unit = {
    // hostServices.showDocument doesn't work with Entitlements / Gatekeeper
    // delegate to native method
    if (OsUtil.isMac) {
      OsxBridge.openUrl(url)
    } else {
      hostServices.showDocument(url)
    }
  }

}
