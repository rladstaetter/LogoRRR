package app.logorrr.usecases.openclose

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.MultipleFileApplicationTest
import app.logorrr.views.a11y.uinodes.UiNodes
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test
import org.testfx.service.query.NodeQuery

import scala.jdk.CollectionConverters.CollectionHasAsScala

/**
 * Check if multiple files can be opened and then closed again via file menu 'close all'
 */
class OpenAndCloseMultipleFilesViaCloseButtonTest
  extends MultipleFileApplicationTest(TestFiles.seq)
    with CheckTabPaneActions:

  @Test def openFilesAndCloseOneByOneViaTabCloseButton(): Unit =
    files.foreach(openFile)

    val tabCards = lookup(UiNodes.LogFileHeaderTabs).queryAll[StackPane]().asScala

    for n <- tabCards do
      val nodeQuery: NodeQuery = clickOn(n).lookup(UiNodes.LogFileHeaderTabCloseButton)
      waitForVisibility(nodeQuery)
      clickOn(nodeQuery.queryAs[StackPane](classOf[StackPane]))

    checkForEmptyTabPane()


