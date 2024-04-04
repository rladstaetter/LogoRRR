package app.logorrr.usecases.openclose

import app.logorrr.views.UiNodes
import app.logorrr.TestFiles
import app.logorrr.usecases.MultipleFileApplicationTest
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test
import org.testfx.service.query.NodeQuery

import scala.jdk.CollectionConverters.CollectionHasAsScala

/**
 * Check if multiple files can be opened and then closed again via file menu 'close all'
 */
class OpenAndCloseMultipleFilesViaCloseButtonTest extends MultipleFileApplicationTest(TestFiles.seq) {

  @Test def openFilesAndCloseOneByOneViaTabCloseButton(): Unit = {
    TestFiles.seq.foreach {
      p => openFile(p)
    }

    val tabCards = lookup(UiNodes.LogFileHeaderTabs).queryAll[StackPane]().asScala

    for (n <- tabCards) {
      val nodeQuery: NodeQuery = clickOn(n).lookup(UiNodes.LogFileHeaderTabCloseButton)
      waitForVisibility(nodeQuery)
      clickOn(nodeQuery.queryAs[StackPane](classOf[StackPane]))
    }

    waitForPredicate[TabPane](UiNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }


}
