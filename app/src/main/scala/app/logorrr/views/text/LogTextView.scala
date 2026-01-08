package app.logorrr.views.text

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.MultipleSelectionModel
import jfx.incubator.scene.control.richtext.CodeArea
import jfx.incubator.scene.control.richtext.model.CodeTextModel
import net.ladstatt.util.log.CanLog


object LogTextView extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogTextView])


class LogTextView(mutLogFileSettings: MutLogFileSettings
                  , filteredList: FilteredList[LogEntry]) extends CodeArea with CanLog:

  private val elementInvalidationListener = JfxUtils.mkInvalidationListener(_ => updateLogTextView)

  def init(): Unit =
    filteredList.addListener(elementInvalidationListener)
    updateLogTextView

  def updateLogTextView: Unit =
    setModel(new CodeTextModel(new LogEntryContent(filteredList)))

  def removeListeners(): Unit =
    filteredList.removeListener(elementInvalidationListener)

  def getSelectionModel: MultipleSelectionModel[LogEntry] = new MultipleSelectionModel[LogEntry] {
    override def getSelectedIndices: ObservableList[Integer] = ???

    override def getSelectedItems: ObservableList[LogEntry] = ???

    override def selectIndices(i: Int, ints: Int*): Unit = ???

    override def selectAll(): Unit = ???

    override def selectFirst(): Unit = ???

    override def selectLast(): Unit = ???

    override def clearAndSelect(i: Int): Unit = ???

    override def select(i: Int): Unit = ???

    override def select(t: LogEntry): Unit = ???

    override def clearSelection(i: Int): Unit = ???

    override def clearSelection(): Unit = ???

    override def isSelected(i: Int): Boolean = ???

    override def isEmpty: Boolean = ???

    override def selectPrevious(): Unit = ???

    override def selectNext(): Unit = ???
  }

  def scrollToActiveLogEntry(): Unit = ()

  def scrollToItem(logEntry: LogEntry): Unit = ()

  def scrollTo(i: Int): Unit = ()

  def getItems: ObservableList[LogEntry] =
    FXCollections.observableArrayList()

