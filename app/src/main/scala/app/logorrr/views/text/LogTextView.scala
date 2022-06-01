package app.logorrr.views.text

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.model.LogEntry
import app.logorrr.util.{ClipBoardUtils, JfxUtils}
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

import java.time.Instant
import scala.jdk.CollectionConverters._
import scala.language.postfixOps


object LogTextView {


  val timeBarColor = Color.BISQUE.darker()
  val timeBarOverflowColor = timeBarColor.darker()


  class LineNumberLogEntry(pathAsString: String
                           , e: LogEntry
                           , maxLength: Int
                           , timings: Map[Int, Instant]
                          ) extends BorderPane {

    /*
        val hBox = new HBox()

        val bg = new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(size * 1.25), Insets.EMPTY))

        private val labels: Seq[Label] =
          for ((c, i) <- Seq(e.value, e.value, e.value).zipWithIndex) yield {
            val l = new Label(c)
            if (i % 2 == 0) {
              l.setBackground(bg)
              l.setTextFill(Color.BLACK)
            }
            //l.setStyle("-fx-background: rgb(255,0,255);")
            // l.setTextFill(Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)))
            l
          }
        hBox.getChildren.addAll(labels: _*)
        BorderPane.setAlignment(hBox, Pos.CENTER_LEFT)
        setLeft(LineDecoratorLabel(e, maxLength))
        setCenter(hBox)

        hBox.getChildren.addAll(labels: _*)
        BorderPane.setAlignment(hBox, Pos.CENTER_LEFT)
        setLeft(LineDecoratorLabel(e, maxLength))
        setCenter(hBox)

        hBox.getChildren.addAll(labels: _*)
        BorderPane.setAlignment(hBox, Pos.CENTER_LEFT)
        setLeft(LineDecoratorLabel(e, maxLength))
        setCenter(hBox)
      */

    val label = {
      val l = new Label(e.value)
      BorderPane.setAlignment(l, Pos.CENTER_LEFT)
      l
    }
    private val label1: LineNumberLabel = LineNumberLabel(e.lineNumber, maxLength)
    BorderPane.setAlignment(label1, Pos.CENTER)

    setLeft(label1)
    setCenter(label)
    e.someInstant foreach {
      instant => setBottom(LineNumberBar(e, instant, timings))
    }
  }

}


class LogTextView(pathAsString: String
                  , filteredList: FilteredList[LogEntry]
                  , timings: Map[Int, Instant]
                 ) extends BorderPane {

  private val fixedCellSize = 26

  /** 'pragmatic way' to determine width of max elems in this view */
  val maxLength = filteredList.size().toString.length

  val listView: ListView[LogEntry] = {
    val lv = new ListView[LogEntry]()
    lv.setItems(filteredList)
    val i = LogoRRRGlobals.getLogFileSettings(pathAsString).selectedIndexProperty.get()
    lv.getSelectionModel.select(i)
    lv
  }
  //listView.heightProperty().addListener(JfxUtils.onNew((s: Number) => {}))
  // listView.heightProperty().addListener(JfxUtils.onNew((n: Number) => {  }))
  listView.setCellFactory((_: ListView[LogEntry]) => new LogEntryListCell())
  //  listView.setFixedCellSize(fixedCellSize)

  LogoRRRGlobals.getLogFileSettings(pathAsString).selectedIndexProperty.addListener(JfxUtils.onNew((n: Number) => {
    Option(listView.getItems.filtered((t: LogEntry) => t.lineNumber == n.intValue()).get(0)) match {
      case Some(value) =>
        val relativeIndex = listView.getItems.indexOf(value)
        listView.getSelectionModel.select(relativeIndex)
        listView.scrollTo(relativeIndex - ((listView.getHeight / fixedCellSize) / 2).toInt)
      // println(s"selectedIndex : ${n.intValue()}, scrollTo : $relativeIndex")
      case None =>
    }

  }))

  setCenter(listView)

  class LogEntryListCell extends ListCell[LogEntry] {
    styleProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).fontStyle)
    //setStyle(LogoRRRFonts.jetBrainsMono(LogTextView.fontSize))
    setGraphic(null)
    val cm = new ContextMenu()
    val copyCurrentToClipboard = new MenuItem("copy text to clipboard")

    cm.getItems.add(copyCurrentToClipboard)

    override def updateItem(t: LogEntry, b: Boolean): Unit = {
      super.updateItem(t, b)
      Option(t) match {
        case Some(e) =>
          setText(null)
          val filters = LogoRRRGlobals.getLogFileSettings(pathAsString).filtersProperty.get().asScala.toSeq
          val entry = LogoRRRLogEntry(e, maxLength, filters)
          entry.lineNumberLabel.styleProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).fontStyle)
          entry.res.foreach(l => l.styleProperty().bind(LogoRRRGlobals.getLogFileSettings(pathAsString).fontStyle))
          setGraphic(entry)
          //setGraphic(new LogTextView.LineNumberLogEntry(e, maxLength, timings))
          copyCurrentToClipboard.setOnAction(_ => ClipBoardUtils.copyToClipboardText(e.value))
          setContextMenu(cm)
        case None =>
          setGraphic(null)
          setText(null)
          setContextMenu(null)
      }
    }

  }
  /*
    def select(logEntry: LogEntry): Unit = {
      listView.getSelectionModel.select(logEntry)
      val index = listView.getSelectionModel.getSelectedIndex - ((listView.getHeight / fixedCellSize) / 2).toInt
      listView.scrollTo(index)
    }
  */
}


