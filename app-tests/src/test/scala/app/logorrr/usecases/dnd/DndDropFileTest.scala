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

object DndDropFileTest {

  @BeforeAll
  def setUp(): Unit = {
    // necessary for https://github.com/TestFX/TestFX/issues/33
    System.setProperty("testfx.robot", "awt")
  }

}

class DndDropFileTest extends StartEmptyApplicationTest
  with VisibleItemActions
  with CheckTabPaneActions {

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    LogoRRRApp.start(stage, services)
    stage.toFront()

    val dndStage = new Stage()
    val dropBox = new ToolBar(TestFiles.seq.map(p => new DragSourceButton(p)): _*)
    dndStage.setScene(new Scene(dropBox))
    dndStage.show()

  }


  @Test def startupEmpty(): Unit = {
    checkForEmptyTabPane()
    TestFiles.seq.foreach {
      f => drag(DragSourceButton.uiNode(FileId(f)).ref, MouseButton.PRIMARY).dropTo(UiNodes.MainTabPane.ref)
    }
    expectCountOfOpenFiles(TestFiles.seq.size)

  }

}



