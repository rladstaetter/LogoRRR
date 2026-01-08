package app.logorrr.usecases.stg

import app.logorrr.conf.{FileId, Settings}
import app.logorrr.steps.ChoiceBoxActions
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.search.stg._
import org.testfx.api.FxRobotInterface

trait StgEditorActions extends ChoiceBoxActions {
  self: SingleFileApplicationTest =>

  def addGroup(newGroupName: String): Unit =
    openFile(fileId)

    openStgEditor(fileId)

    createGroup(fileId, newGroupName)

    closeStgEditor(fileId)

    matchItems[String](StgChoiceBox.uiNode(fileId), (Settings.DefaultSearchTermGroups.map(_.name) ++ Seq[String](newGroupName)).sorted)

  def addExistingGroupToGlobalGroup(groupToAdd: String): Unit =
    openFile(fileId)

    openStgEditor(fileId)

    val listView = lookupStgListView(fileId)

    // find appropriate cell and click on 'like'
    for i <- 0 to listView.getItems.size yield {
      val cell = nthCell(listView, i)
      Option(cell.getItem) match
        case Some(item) if item.name == groupToAdd => Option(cell)
        case _ => None
    }.foreach(c => clickOn(c.globalStgButton))

    closeStgEditor(fileId)



  def lookupStgListView(fileId: FileId): StgListView = lookup[StgListView](StgListView.uiNode(fileId))

  def nthCell(clv: StgListView, cellIndex: Int): StgEditorListViewCell =
    from(clv).lookup(".list-cell").nth(cellIndex).query[StgEditorListViewCell]

  protected def createGroup(fileId: FileId, groupName: String): FxRobotInterface =
    clickOn(StgNameTextField.uiNode(fileId)).write(groupName)
    clickOn(CreateStgButton.uiNode(fileId))

  protected def closeStgEditor(fileId: FileId): FxRobotInterface =
    waitForVisibility(CloseStgEditorButton.uiNode(fileId))
    clickOn(CloseStgEditorButton.uiNode(fileId))

  def openStgEditor(fileId: FileId): Unit =
    waitForVisibility(OpenStgEditorButton.uiNode(fileId))
    clickOn(OpenStgEditorButton.uiNode(fileId))

    waitForVisibility(StgNameTextField.uiNode(fileId))
    waitForVisibility(CreateStgButton.uiNode(fileId))

}