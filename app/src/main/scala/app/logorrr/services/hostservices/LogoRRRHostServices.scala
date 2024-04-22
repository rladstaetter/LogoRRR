package app.logorrr.services.hostservices

import app.logorrr.views.UiNode

/**
 * To wrap native HostServices
 */
trait LogoRRRHostServices {

  /** opens given document */
  def showDocument(uiNode: UiNode, url: String): Unit

}


