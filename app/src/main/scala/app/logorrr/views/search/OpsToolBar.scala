package app.logorrr.views.search

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.time.{SliderVBox, TimeRange, TimeUtil, TimestampSettingsButton}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import net.ladstatt.util.os.OsUtil


object OpsToolBar:

  def apply(mutLogFileSettings: MutLogFileSettings
            , chunkListView: ChunkListView[LogEntry]
            , entries: ObservableList[LogEntry]
            , filteredList: FilteredList[LogEntry]): OpsToolBar =

    new OpsToolBar(mutLogFileSettings.getFileId
      , mutLogFileSettings
      , chunkListView
      , mutLogFileSettings.mutSearchTerms.add(_)
      , entries
      , filteredList)



/**
 * Groups search ui widgets together.
 *
 * @param addFilterFn filter function which results from user interaction with SearchToolbar
 */
class OpsToolBar(fileId: FileId
                 , mutLogFileSettings: MutLogFileSettings
                 , chunkListView: ChunkListView[LogEntry]
                 , addFilterFn: MutableSearchTerm => Unit
                 , logEntries: ObservableList[LogEntry]
                 , filteredList: FilteredList[LogEntry]) extends ToolBar:

  setMaxHeight(Double.PositiveInfinity)

  setStyle("""-fx-padding: 0px 0px 0px 4px;""")
  val w = 380
  private val macWidth: Int = w
  private val winWidth: Int = w + 2
  private val linuxWidth: Int = w + 2
  val width: Int = OsUtil.osFun(winWidth, macWidth, linuxWidth) // different layouts (may be dependent on font size renderings?)
  setMinWidth(width)


  val searchRegion = new SearchRegion(fileId, addFilterFn)

  val otherItemsRegion = new OtherItemsRegion(fileId, logEntries, filteredList)

  val timestampSettingsRegion = new TimestampSettingsRegion(mutLogFileSettings, chunkListView, logEntries, filteredList)


  getItems.addAll(searchRegion.items ++ otherItemsRegion.items ++ timestampSettingsRegion.items *)

