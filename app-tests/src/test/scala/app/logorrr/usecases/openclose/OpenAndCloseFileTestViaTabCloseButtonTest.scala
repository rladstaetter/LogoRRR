package app.logorrr.usecases.openclose

import app.logorrr.io.FileId
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.{SingleFileApplicationTest, TestFiles}
import javafx.scene.control.TabPane
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
    clickOn(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOn(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(FileId(path)))

    // hack to access close button of first tab
    val tabCloseButtonQuery = s"${LogoRRRNodes.MainTabPane.ref} > .tab-header-area > .headers-region > .tab > .tab-container > .tab-close-button"
    waitForVisibility(tabCloseButtonQuery)

    clickOn(tabCloseButtonQuery)

    waitForPredicate[TabPane](LogoRRRNodes.MainTabPane, classOf[TabPane], tabPane => {
      tabPane.getTabs.isEmpty
    })

  }

}
