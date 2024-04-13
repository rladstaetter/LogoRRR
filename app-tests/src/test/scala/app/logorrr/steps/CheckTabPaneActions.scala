package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.UiNodes
import javafx.scene.control.TabPane


trait CheckTabPaneActions {
  self: TestFxBaseApplicationTest =>

  def expectCountOfOpenFiles(expectedCount : Int): Unit = {
    waitForPredicate[TabPane](UiNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.size == expectedCount
    })
  }

  def checkForEmptyTabPane(): Unit = {
    waitForPredicate[TabPane](UiNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })
  }

  def checkForNonEmptyTabPane(): Unit = {
    waitForPredicate[TabPane](UiNodes.MainTabPane, classOf[TabPane], tabPane => {
      !tabPane.getTabs.isEmpty
    })
  }

}
