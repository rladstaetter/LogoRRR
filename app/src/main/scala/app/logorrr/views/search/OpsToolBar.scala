package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.logfiletab.LogFilePane
import javafx.beans.property.{ObjectPropertyBase, Property}
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.stage.Window
import net.ladstatt.util.os.OsUtil


object OpsToolBar:

  val w = 380

  val width: Int = OsUtil.osFun(w + 2, w, w + 2)


/**
 * Groups search ui widgets together.
 */
class OpsToolBar(logFilePane: LogFilePane
                 , mutLogFileSettings: MutLogFileSettings
                 , chunkListView: ChunkListView[LogEntry]
                 , logEntries: ObservableList[LogEntry]) extends ToolBar:

  // layout
  setMaxHeight(Double.PositiveInfinity)
  setStyle("-fx-padding: 0px 0px 0px 4px;")

  setMinWidth(OpsToolBar.width)

  val searchRegion = new SearchRegion
  private val otherItemsRegion = new OtherItemsRegion
  val timestampSettingsRegion = new TimestampSettingsRegion(logFilePane, mutLogFileSettings, chunkListView, logEntries)

  getItems.addAll(searchRegion.items ++ otherItemsRegion.items ++ timestampSettingsRegion.items *)

  def init(owner: Window
           , fileIdProperty: ObjectPropertyBase[FileId]
           , autoScrollProperty: Property[java.lang.Boolean]
           , mutSearchTerms: ObservableList[MutableSearchTerm]
           , filteredList: ObservableList[LogEntry]): Unit = {
    searchRegion.init(fileIdProperty, mutSearchTerms)
    otherItemsRegion.init(fileIdProperty, autoScrollProperty, logEntries, filteredList)
    timestampSettingsRegion.init(owner)
  }

  def shutdown(autoScrollProperty: Property[java.lang.Boolean]
               , searchTerms: ObservableList[MutableSearchTerm]
               , filteredList: ObservableList[LogEntry]): Unit = {
    searchRegion.shutdown(searchTerms)
    otherItemsRegion.shutdown(autoScrollProperty, logEntries, filteredList)
    timestampSettingsRegion.shutdown()
  }

  val searchTextField: SearchTextField = searchRegion.searchTextField