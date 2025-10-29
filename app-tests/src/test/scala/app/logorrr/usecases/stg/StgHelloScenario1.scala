package app.logorrr.usecases.stg

import app.logorrr.conf.Settings
import app.logorrr.views.search.stg.StgChoiceBox
import org.junit.jupiter.api.Test

class StgHelloScenario1 extends SearchTermGroupEditorTest {

  @Test def scenario1(): Unit = {
    openFile(fileId)

    openStgEditor(fileId)
    val expected = Seq("a", "b", "c")
    expected.foreach(createGroup(fileId, _))

    val view = lookupStgListView(fileId)

    assert(view.getItems.size() == expected.size + Settings.Default.searchTermGroups.size)
    clickOn(nthCell(view, 0).deleteButton)
    assert(view.getItems.size() == expected.tail.size + Settings.Default.searchTermGroups.size)

    closeStgEditor(fileId)

    matchItems[String](StgChoiceBox.uiNode(fileId), (expected.tail ++ Settings.DefaultSearchTermGroups.map(_.name)).sorted)
  }


}
