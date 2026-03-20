package app.logorrr.views.settings

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.SimpleListProperty
import javafx.collections.ObservableList
import javafx.scene.control.{ListView, ToggleGroup}

object SettingsStgListView extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[SettingsStgListView])

/**
 * ListView implementation that holds MutSearchTermGroup items.
 * * @param searchTermGroups the observable list of mutable search term groups.
 */
class SettingsStgListView(searchTermGroups: SimpleListProperty[MutSearchTermGroup]) extends ListView[MutSearchTermGroup]:

  setId(SettingsEditor.SettingsStgListView.value)
  itemsProperty.bind(searchTermGroups)

  // A single ToggleGroup shared across all cells ensures
  // only one RadioButton is selected at a time.
  private val tg = new ToggleGroup

  // Set the factory to use our custom cell implementation
  setCellFactory(_ => new SettingsStgListViewCell(tg))

  /**
   * Accessor for the underlying mutable items.
   */
  def getSearchTermGroups: ObservableList[MutSearchTermGroup] = searchTermGroups

  def shutdown(): Unit = itemsProperty().unbind()

  def scrollToAndHighlightLast(): Unit =
    val lastIndex = getItems.size() - 1
    scrollTo(getItems.size )
    if 0 < lastIndex then
      // 2. We must wait for the next "Pulse" so the cell is actually rendered
      // Using Platform.runLater is usually enough to wait for the layout pass
      javafx.application.Platform.runLater(() => {

        // 3. Find the cell by its index
        // We use lookupAll to find all cells and filter by index
        import scala.jdk.CollectionConverters.*

        // highlight new element
        this.lookupAll(".list-cell").asScala.collectFirst {
          case cell: SettingsStgListViewCell if cell.getIndex == lastIndex && !cell.isEmpty =>
            cell.glowsy()
        }
      })
