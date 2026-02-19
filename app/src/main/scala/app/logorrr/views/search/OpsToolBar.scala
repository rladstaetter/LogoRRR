package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import javafx.beans.property.{ObjectProperty, ObjectPropertyBase, Property}
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.stage.Window
import net.ladstatt.util.os.OsUtil

import java.util.function.Predicate


object OpsToolBar:

  val w = 380

  val width: Int = OsUtil.osFun(w + 2, w, w + 2)


/**
 * Groups search ui widgets together.
 */
class OpsToolBar(owner: Window
                 , mutLogFileSettings: MutLogFileSettings
                 , chunkListView: ChunkListView[LogEntry]
                 , logEntries: ObservableList[LogEntry]
                 , predicateProperty: ObjectProperty[Predicate[? >: LogEntry]]) extends ToolBar:

  // layout
  setMaxHeight(Double.PositiveInfinity)
  setStyle("-fx-padding: 0px 0px 0px 4px;")

  setMinWidth(OpsToolBar.width)

  val searchRegion = new SearchRegion
  private val otherItemsRegion = new OtherItemsRegion
  private val timestampSettingsRegion = new TimestampSettingsRegion(owner: Window, mutLogFileSettings, chunkListView, logEntries, predicateProperty)

  getItems.addAll(searchRegion.items ++ otherItemsRegion.items ++ timestampSettingsRegion.items *)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , autoScrollProperty: Property[java.lang.Boolean]
           , mutSearchTerms: ObservableList[MutableSearchTerm]
           , filteredList: ObservableList[LogEntry]): Unit = {
    searchRegion.init(fileIdProperty, mutSearchTerms)
    otherItemsRegion.init(fileIdProperty, autoScrollProperty, logEntries, filteredList)
    timestampSettingsRegion.init()
  }

  def shutdown(autoScrollProperty: Property[java.lang.Boolean]
               , searchTerms: ObservableList[MutableSearchTerm]
               , filteredList: ObservableList[LogEntry]): Unit = {
    searchRegion.shutdown(searchTerms)
    otherItemsRegion.shutdown(autoScrollProperty, logEntries, filteredList)
    timestampSettingsRegion.shutdown()
  }
