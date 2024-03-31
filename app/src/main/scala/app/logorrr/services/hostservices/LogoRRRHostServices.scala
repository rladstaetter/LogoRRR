package app.logorrr.services.hostservices

/**
 * To wrap native HostServices
 */
trait LogoRRRHostServices {

  /** opens given document */
  def showDocument(url: String): Unit

}


