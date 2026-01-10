package app.logorrr.services.hostservices

import app.logorrr.views.a11y.UiNode
import javafx.collections.{FXCollections, ObservableList}
import net.ladstatt.util.log.TinyLog

class MockHostServices extends LogoRRRHostServices with TinyLog:

  /** used for tests */
  val visitedUrls: ObservableList[String] = FXCollections.observableArrayList[String]()

  override def showDocument(uiNode: UiNode, url: String): Unit =
    visitedUrls.add(url)
    logTrace(s"showing '$url'.")

