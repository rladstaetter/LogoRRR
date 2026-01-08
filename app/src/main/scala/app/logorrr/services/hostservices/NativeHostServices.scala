package app.logorrr.services.hostservices

import app.logorrr.OsxBridge
import app.logorrr.views.a11y.UiNode
import javafx.application.HostServices
import net.ladstatt.util.os.OsUtil


class NativeHostServices(hostServices: => HostServices) extends LogoRRRHostServices:

  override def showDocument(uiNode: UiNode, url: String): Unit =
    // hostServices.showDocument doesn't work with Entitlements / Gatekeeper
    // delegate to native method
    if OsUtil.isMac then
      OsxBridge.openUrl(url)
    else
      hostServices.showDocument(url)


