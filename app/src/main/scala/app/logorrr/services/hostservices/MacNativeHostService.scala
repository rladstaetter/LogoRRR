package app.logorrr.services.hostservices

import app.logorrr.OsxBridge
import app.logorrr.views.UiNode

/**
 * Delegate opening urls directly to native code on mac (because of apples entitlements / security system)
 */
class MacNativeHostService extends LogoRRRHostServices {
  /** opens given document */
  override def showDocument(uiNode: UiNode, url: String): Unit = {
    OsxBridge.openUrl(url)
  }
}
