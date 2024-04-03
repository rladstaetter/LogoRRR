package app.logorrr.usecases.openclose

import app.logorrr.io.FileId
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.{MultipleFileApplicationTest, TestFiles}
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
      p => openFile(FileId(p))
    }

    val tabCards = lookup(LogoRRRNodes.LogFileHeaderTabs).queryAll[StackPane]().asScala

    for (n <- tabCards) {
      val nodeQuery: NodeQuery = clickOn(n).lookup(LogoRRRNodes.LogFileHeaderTabCloseButton)
      waitForVisibility(nodeQuery)
      clickOn(nodeQuery.queryAs[StackPane](classOf[StackPane]))
    }

    waitForPredicate[TabPane](LogoRRRNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }

  private def openFile(fileId: FileId): Unit = {
    waitForVisibility(LogoRRRNodes.FileMenu)
    clickOnNode(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOnNode(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(fileId))
  }
}
