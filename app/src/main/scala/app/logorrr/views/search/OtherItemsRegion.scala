package app.logorrr.views.search

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.autoscroll.AutoScrollCheckBox
import app.logorrr.views.ops.{ClearLogButton, CopyLogButton}
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.Node

class OtherItemsRegion(fileId: FileId, logEntries: ObservableList[LogEntry], filteredList: FilteredList[LogEntry]):

  private val autoScrollCheckBox = new AutoScrollCheckBox(fileId)
  private val clearLogButton = new ClearLogButton(fileId, logEntries)
  private val copySelectionButton = new CopyLogButton(fileId, filteredList)

  val items: Seq[Node] = Seq(autoScrollCheckBox, clearLogButton, copySelectionButton)
