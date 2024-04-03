package app.logorrr.usecases.openclose

import app.logorrr.io.FileId
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.{SingleFileApplicationTest, TestFiles}
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane
import org.junit.jupiter.api.Test

/**
 * Checks if a file can be opened and closed via it's closing button
 */
class OpenAndCloseFileTestViaTabCloseButtonTest extends SingleFileApplicationTest(TestFiles.simpleLog0) {

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openAndCloseFileTest(): Unit = {
    // wait until file menu is visible
    waitForVisibility(LogoRRRNodes.FileMenu)
    clickOnNode(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOnNode(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(FileId(path)))

    // yields only one tab since there is only one loaded
    val tabsQuery = LogoRRRNodes.LogFileHeaderTabs

    val closeButtonQuery = clickOn(lookup(tabsQuery).query[StackPane]()).lookup(LogoRRRNodes.LogFileHeaderTabCloseButton)
    waitForVisibility(closeButtonQuery)

    clickOn(closeButtonQuery.queryAs[StackPane](classOf[StackPane]))


    waitForPredicate[TabPane](LogoRRRNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }

}
