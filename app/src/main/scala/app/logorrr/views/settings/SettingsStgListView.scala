package app.logorrr.views.settings

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.collections.ObservableList
import javafx.scene.control.{ListView, ToggleGroup}

object SettingsStgListView extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SettingsStgListView])

/**
 * ListView implementation that holds MutSearchTermGroup items.
 * * @param searchTermGroups the observable list of mutable search term groups.
 */
class SettingsStgListView(searchTermGroups: ObservableList[MutSearchTermGroup]) extends ListView[MutSearchTermGroup]:

  setId(SettingsEditor.SettingsStgListView.value)
  setItems(searchTermGroups)

  // A single ToggleGroup shared across all cells ensures
  // only one RadioButton is selected at a time.
  private val tg = new ToggleGroup

  // Set the factory to use our custom cell implementation
  setCellFactory(_ => new SettingsStgListViewCell(tg))

  /**
   * Accessor for the underlying mutable items.
   */
  def getSearchTermGroups: ObservableList[MutSearchTermGroup] = searchTermGroups