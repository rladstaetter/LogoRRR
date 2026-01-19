package app.logorrr.views.text

import app.logorrr.conf.FileId
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.model.LogEntry
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.collections.transformation.FilteredList
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.MultipleSelectionModel
import jfx.incubator.scene.control.richtext.model.{CodeTextModel, RichParagraph}
import jfx.incubator.scene.control.richtext.{CodeArea, SyntaxDecorator, TextPos}
import net.ladstatt.util.log.TinyLog

import scala.collection.mutable.ListBuffer


class SimpleSyntaxDecorator extends SyntaxDecorator {
  val paragraphs = new ListBuffer[RichParagraph]()

  override def createRichParagraph(model: CodeTextModel, index: Int): RichParagraph =
    if (paragraphs.isEmpty || index >= paragraphs.size) return RichParagraph.builder.build
    paragraphs(index)

  override def handleChange(m: CodeTextModel, start: TextPos, end: TextPos, charsTop: Int, linesAdded: Int, charsBottom: Int): Unit = {
    val text = getPlainText(m)
    println(text)
  }

  protected def getPlainText(model: CodeTextModel): String = {
    val sb = new StringBuilder
    var newLine = false
    for (i <- 0 until model.size) {
      if (newLine) sb.append('\n')
      else newLine = true
      sb.append(model.getPlainText(i))
    }
    sb.toString
  }
}

object LogTextView extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[LogTextView])


class LogTextView(mutLogFileSettings: MutLogFileSettings
                  , filteredList: FilteredList[LogEntry])
  extends CodeArea with TinyLog:

  setLineNumbersEnabled(true)

  setId(LogTextView.uiNode(mutLogFileSettings.getFileId).value)
  private val elementInvalidationListener = JfxUtils.mkInvalidationListener(_ => updateLogTextView)

  def init(): Unit =
    getStylesheets.add(getClass.getResource("/app/logorrr/LogTextView.css").toExternalForm)
    styleProperty.bind(mutLogFileSettings.fontStyleBinding)
    filteredList.addListener(elementInvalidationListener)
    updateLogTextView

  def updateLogTextView: Unit =
    setModel(new CodeTextModel(new LogEntryContent(filteredList)))

  def removeListeners(): Unit =
    filteredList.removeListener(elementInvalidationListener)
    styleProperty.unbind()

  setSyntaxDecorator(new SimpleSyntaxDecorator())

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

