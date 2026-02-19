package app.logorrr.steps

import app.logorrr.conf.FileId
import app.logorrr.usecases.{FxBaseInterface, SingleFileApplicationTest}
import app.logorrr.views.search.st.{ASearchTermToggleButton, RemoveSearchTermButton, SearchTermToggleButton, SearchTermToolBar}
import app.logorrr.views.search.{SearchButton, SearchTextField}
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import org.testfx.api.FxRobotInterface

trait SearchTermToolbarActions:
  self: FxBaseInterface =>

  def search(fileId: FileId, terms: String*): Unit = terms.foreach(t => searchFor(fileId, t))

  def searchFor(fileId: FileId, needle: String): FxRobotInterface =
    clickOn(SearchTextField.uiNode(fileId)).write(needle)
    clickOn(SearchButton.uiNode(fileId))

  def existsSearchTermToggleButton(fileId: FileId, searchTerm: String): Boolean =
    Option(lookup(ASearchTermToggleButton.uiNode(fileId, searchTerm))).isDefined

  def lookupSearchTerms(fileId: FileId): FilteredList[Node] =
    val toolbar: SearchTermToolBar = lookup[SearchTermToolBar](SearchTermToolBar.uiNode(fileId))
    toolbar.getItems.filtered(n => n.isInstanceOf[SearchTermToggleButton])


  // wipes all search terms
  def clearAllSearchTerms(fileId: FileId): Unit =
    var finished = false
    while (!finished)
      val items = lookupSearchTerms(fileId)
      finished = items.isEmpty
      if !finished then clickOn(RemoveSearchTermButton.uiNode(fileId, items.get(0).asInstanceOf[ASearchTermToggleButton].getValue))

