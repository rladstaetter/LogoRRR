package app.logorrr.usecases.openclose

import app.logorrr.TestFiles
import app.logorrr.steps.CheckTabPaneActions
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.UiNodes
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

import java.nio.file.Path

/**
 * Checks if a file can be opened and closed via it's closing button
 */
class OpenAndCloseFileTestViaTabCloseButtonTest
  extends SingleFileApplicationTest(TestFiles.simpleLog0)
    with CheckTabPaneActions {

  override val path: Path = TestFiles.simpleLog0

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openAndCloseFileTest(): Unit = {
    // wait until file menu is visible
    openFile(path)

    // yields only one tab since there is only one loaded
    val tabsQuery = UiNodes.LogFileHeaderTabs

    val closeButtonQuery = clickOn(lookup(tabsQuery).query[StackPane]()).lookup(UiNodes.LogFileHeaderTabCloseButton)
    waitForVisibility(closeButtonQuery)

    clickOn(closeButtonQuery.queryAs[StackPane](classOf[StackPane]))

    checkForEmptyTabPane()
  }

}
