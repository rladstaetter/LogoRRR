package app.logorrr.services.hostservices

import app.logorrr.util.CanLog
import app.logorrr.views.UiNode
import javafx.collections.{FXCollections, ObservableList}

class MockHostServices extends LogoRRRHostServices with CanLog {

  /** used for tests */
  val visitedUrls: ObservableList[String] = FXCollections.observableArrayList[String]()

  override def showDocument(uiNode: UiNode, url: String): Unit = {
    visitedUrls.add(url)
    logTrace(s"showing '$url'.")
  }

}
