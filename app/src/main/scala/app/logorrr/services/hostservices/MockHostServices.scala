package app.logorrr.services.hostservices

import app.logorrr.util.CanLog

class MockHostServices extends LogoRRRHostServices with CanLog {

  override def showDocument(url: String): Unit = logTrace(s"showing '$url'.")

}
