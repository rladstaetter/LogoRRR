package app.logorrr.services.hostservices

import net.ladstatt.util.log.CanLog
import app.logorrr.views.a11y.UiNode
import javafx.collections.{FXCollections, ObservableList}

class MockHostServices extends LogoRRRHostServices with CanLog {

  /** used for tests */
  val visitedUrls: ObservableList[String] = FXCollections.observableArrayList[String]()

  override def showDocument(uiNode: UiNode, url: String): Unit = {
    visitedUrls.add(url)
    logTrace(s"showing '$url'.")
  }

}
