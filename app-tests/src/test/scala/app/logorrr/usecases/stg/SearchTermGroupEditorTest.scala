package app.logorrr.usecases.stg

import app.logorrr.TestFiles
import app.logorrr.io.FileId
import app.logorrr.steps.ChoiceBoxActions
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.stg._
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobotInterface

class SearchTermGroupEditorTest extends SingleFileApplicationTest(TestFiles.simpleLog0)
  with ChoiceBoxActions {

  def lookupStgListView(fileId: FileId): StgListView = {
    lookup(StgListView.uiNode(fileId).ref).query[StgListView]
  }

  def nthCell(clv: StgListView, cellIndex: Int): StgEditorListviewCell = {
    from(clv).lookup(".list-cell").nth(cellIndex).query[StgEditorListviewCell]
  }


  protected def createGroup(fileId: FileId, groupName: String): FxRobotInterface = {
    clickOn(StgNameTextField.uiNode(fileId)).write(groupName)
    clickOn(CreateStgButton.uiNode(fileId))
  }

  protected def closeStgEditor(fileId: FileId): FxRobotInterface = {
    waitForVisibility(CloseStgEditorButton.uiNode(fileId))
    clickOn(CloseStgEditorButton.uiNode(fileId))
  }

  def openStgEditor(fileId: FileId): Unit = {
    waitForVisibility(OpenStgEditorButton.uiNode(fileId))
    clickOn(OpenStgEditorButton.uiNode(fileId))

    waitForVisibility(StgNameTextField.uiNode(fileId))
    waitForVisibility(CreateStgButton.uiNode(fileId))
  }

}

class StgCheckEmptyTest extends SearchTermGroupEditorTest {

  @Test def checkChoiceBoxEmptyOnStart(): Unit = {
    // open file such that search term group editor icon appears
    openFile(fileId)

    // wait for visibility
    waitForVisibility(StgChoiceBox.uiNode(fileId))
    // trivial check - choicebox is empty
    matchItems[String](StgChoiceBox.uiNode(fileId), Seq[String]())
  }
}

class StgHelloWorldTest extends SearchTermGroupEditorTest {

  @Test def createNewGroupAndTestChoiceBox(): Unit = {
    openFile(fileId)

    openStgEditor(fileId)

    createGroup(fileId, "Test Group")

    closeStgEditor(fileId)

    matchItems[String](StgChoiceBox.uiNode(fileId), Seq[String]("Test Group"))
  }
}

class StgHelloScenario1 extends SearchTermGroupEditorTest {
  @Test def scenario1(): Unit = {
    openFile(fileId)

    openStgEditor(fileId)
    val expected = Seq("a", "b", "c")
    expected.foreach(createGroup(fileId, _))

    val view = lookupStgListView(fileId)

    assert(view.getItems.size() == 3)
    clickOn(nthCell(view, 0).deleteButton)
    assert(view.getItems.size() == 2)

    closeStgEditor(fileId)

    matchItems[String](StgChoiceBox.uiNode(fileId), expected.tail)
  }


}