package app.logorrr.usecases.stg

import app.logorrr.TestFiles
import app.logorrr.io.FileId
import app.logorrr.steps.ChoiceBoxActions
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.stg._
import org.testfx.api.FxRobotInterface

class SearchTermGroupEditorTest extends SingleFileApplicationTest(TestFiles.simpleLog0)
  with ChoiceBoxActions {

  def lookupStgListView(fileId: FileId): StgListView = lookup[StgListView](StgListView.uiNode(fileId))

  def nthCell(clv: StgListView, cellIndex: Int): StgEditorListViewCell = {
    from(clv).lookup(".list-cell").nth(cellIndex).query[StgEditorListViewCell]
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