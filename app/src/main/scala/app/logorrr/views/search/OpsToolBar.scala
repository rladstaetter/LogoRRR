package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.beans.property.{ObjectPropertyBase, Property, SimpleListProperty}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.*
import javafx.stage.Window
import net.ladstatt.util.os.OsUtil


object OpsToolBar:

  val w = 380

  val width: Int = OsUtil.osFun(w + 2, w, w + 2)


/**
 * Groups search ui widgets together.
 */
class OpsToolBar(mutLogFileSettings: MutLogFileSettings
                 , chunkListView: ChunkListView[LogEntry]
                 , logEntries: ObservableList[LogEntry]
                 , filteredList: FilteredList[LogEntry]) extends ToolBar:

  // layout
  setMaxHeight(Double.PositiveInfinity)
  setStyle("-fx-padding: 0px 0px 0px 4px;")

  setMinWidth(OpsToolBar.width)

  val searchRegion = new SearchRegion
  private val otherItemsRegion = new OtherItemsRegion
  private val timestampSettingsRegion = new TimestampSettingsRegion(mutLogFileSettings, chunkListView, logEntries, filteredList)

  getItems.addAll(searchRegion.items ++ otherItemsRegion.items ++ timestampSettingsRegion.items *)

  def init(window: Window
           , fileIdProperty: ObjectPropertyBase[FileId]
           , autoScrollProperty: Property[java.lang.Boolean]
           , searchTerms: SimpleListProperty[MutableSearchTerm]
           , filteredList: ObservableList[LogEntry]): Unit = {
    searchRegion.init(fileIdProperty, searchTerms)
    otherItemsRegion.init(fileIdProperty, autoScrollProperty, logEntries, filteredList)
    timestampSettingsRegion.init(window)
  }

  def shutdown(autoScrollProperty: Property[java.lang.Boolean]
               , searchTerms: SimpleListProperty[MutableSearchTerm]
               , filteredList: ObservableList[LogEntry]): Unit = {
    searchRegion.shutdown(searchTerms)
    otherItemsRegion.shutdown(autoScrollProperty, logEntries, filteredList)
    timestampSettingsRegion.shutdown()
  }
