package app.logorrr.views.text

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.Node
import javafx.scene.control.MultipleSelectionModel
import jfx.incubator.scene.control.richtext.model.BasicTextModel.InMemoryContent
import jfx.incubator.scene.control.richtext.{CodeArea, SideDecorator}
import jfx.incubator.scene.control.richtext.model.CodeTextModel
import net.ladstatt.util.log.CanLog


object LogTextView extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogTextView])

  def apply(mutLogFileSettings: MutLogFileSettings
            , filteredList: FilteredList[LogEntry]) =
    new LogTextView(mutLogFileSettings, new CodeTextModel(new LogEntryContent(filteredList)))


class LogTextView(mutLogFileSettings: MutLogFileSettings
                  , model: CodeTextModel)
  extends CodeArea(model) with CanLog:

  def init(): Unit = {
    setLineNumbersEnabled(true)
    setHighlightCurrentParagraph(true)
  }


  def removeListeners(): Unit = ()

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

