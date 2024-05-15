package app.logorrr.usecases.dnd

import app.logorrr.io.FileId
import app.logorrr.steps.{CheckTabPaneActions, VisibleItemActions}
import app.logorrr.usecases.StartEmptyApplicationTest
import app.logorrr.views.UiNodes
import app.logorrr.{LogoRRRApp, TestFiles}
import javafx.scene.Scene
import javafx.scene.control.ToolBar
import javafx.scene.input.MouseButton
import javafx.stage.Stage
import org.junit.jupiter.api.{BeforeAll, Test}

object DndDropZipFileTest {

  @BeforeAll
  def setUp(): Unit = {
    // necessary for https://github.com/TestFX/TestFX/issues/33
    System.setProperty("testfx.robot", "awt")
  }

}

class DndDropZipFileTest extends StartEmptyApplicationTest
  with VisibleItemActions
  with CheckTabPaneActions {

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    LogoRRRApp.start(stage, services)

    val dndStage = new Stage()
    val dropBox = new ToolBar(Seq(new DragSourceButton(TestFiles.zipFileContaining10Files)): _*)
    dndStage.setScene(new Scene(dropBox))
    dndStage.show()

  }


  @Test def testOpeningAZipFileWith10Files(): Unit = {
    checkForEmptyTabPane()
    drag(DragSourceButton.uiNode(FileId(TestFiles.zipFileContaining10Files)).ref, MouseButton.PRIMARY).dropTo(UiNodes.MainTabPane.ref)
    expectCountOfOpenFiles(10)
  }

}



