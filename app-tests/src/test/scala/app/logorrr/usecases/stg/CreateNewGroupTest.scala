package app.logorrr.usecases.stg

import app.logorrr.conf.Settings
import app.logorrr.views.search.stg.StgChoiceBox
import org.junit.jupiter.api.Test

/**
 * Shows that adding a new search term group via the search term group editor works.
 */
class CreateNewGroupTest extends SearchTermGroupEditorTest {

  @Test def createNewGroupAndTestChoiceBox(): Unit = {
    addGroup("Test Group")
  }

  def addGroup(newGroupName: String): Unit = {
    openFile(fileId)

    openStgEditor(fileId)

    createGroup(fileId, newGroupName)

    closeStgEditor(fileId)

    matchItems[String](StgChoiceBox.uiNode(fileId), (Settings.DefaultSearchTermGroups.map(_.name) ++ Seq[String](newGroupName)).sorted)
  }

  def addExistingGroupToGlobalGroup(groupToAdd: String): Unit = {
    openFile(fileId)

    openStgEditor(fileId)

    val listView = lookupStgListView(fileId)

    // find appropriate cell and click on 'like'
    for (i <- 0 to listView.getItems.size) yield {
      val cell = nthCell(listView, i)
      Option(cell.getItem) match {
        case Some(item) if item.name == groupToAdd => Option(cell)
        case _ => None
      }
    }.foreach(c => clickOn(c.globalStgButton))

    closeStgEditor(fileId)

  }

}
