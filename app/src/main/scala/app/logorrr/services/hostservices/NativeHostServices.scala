package app.logorrr.services.hostservices

import javafx.application.HostServices

class NativeHostServices(hostServices: => HostServices) extends LogoRRRHostServices {

  override def showDocument(url: String): Unit = hostServices.showDocument(url)

}
