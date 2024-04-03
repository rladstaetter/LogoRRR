package app.logorrr.steps

import app.logorrr.io.FileId
import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.logfiletab.LogFileTab

import java.nio.file.Path

/**
 * Mix in if you need to be able to open a file in your test
 */
trait CanOpenFile {
  self: TestFxBaseApplicationTest =>

  protected def openFile(path: Path): Unit = {
    waitForVisibility(LogoRRRNodes.FileMenu)
    clickOnNode(LogoRRRNodes.FileMenu)
    waitForVisibility(LogoRRRNodes.FileMenuOpenFile)
    clickOnNode(LogoRRRNodes.FileMenuOpenFile)
    waitForVisibility(LogFileTab.idFor(FileId(path)))
  }

}
