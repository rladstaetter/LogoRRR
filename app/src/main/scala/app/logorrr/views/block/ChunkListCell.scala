package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils, OsUtil}
import app.logorrr.views.search.Filter
import javafx.animation.{KeyFrame, Timeline}
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.ListCell
import javafx.scene.image.ImageView
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.util.Duration

import scala.util.Try


/**
 * A listcell which can contain one or more log entries.
 *
 * To see how those cells are populated, see [[Chunk.mkChunks]]. [[ChunkImage]] is responsible to draw all Chunks
 */
class ChunkListCell(selectedLineNumberProperty: SimpleIntegerProperty
                    , widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty
                    , filtersProperty: ObservableList[Filter]
                    , firstVisibleTextCellIndexProperty: SimpleIntegerProperty
                    , lastVisibleTextCellIndexProperty: SimpleIntegerProperty
                    , scrollTo: LogEntry => Unit
                   ) extends ListCell[Chunk] with CanLog {

  /**
   * @param maxOccupiedWidth max space in x direction where blocks will be shown
   * @param me               mouse event
   * @return -1 if MouseEvent is outside the region of interest
   */
  private def calcIndex(maxOccupiedWidth: Double, me: MouseEvent): Int = {
    if (me.getX <= maxOccupiedWidth) {
      ChunkImage.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, ChunkListView.calcListViewWidth(widthProperty.get()).toInt)
    } else -1
  }

  // if user selects an entry in the ChunkListView set selectedLineNumberProperty. This property is observed
  // via an listener and a yellow square will be painted.
  private val mouseClickedHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      // on left mouse button
      if (me.getButton.equals(MouseButton.PRIMARY)) {
        Option(getItem).map(_.cols) match {
          case Some(cols) =>
            val index = calcIndex(cols * blockSizeProperty.get(), me)
            getEntryAt(getItem, index) match {
              case Some(value) =>
                selectedLineNumberProperty.set(value.lineNumber)
                // we have to select also the entry in the LogTextView, couldn't get it to work with bindings / listeners
                scrollTo(value)
              case None =>
            }
          case None => // outside of a chunk
        }
      }
    }
  }


  private val mouseMovedHandler: EventHandler[MouseEvent] = (me: MouseEvent) => {
    Option(getItem).map(_.cols) match {
      case Some(cols) =>
        val index = calcIndex(cols * blockSizeProperty.get(), me)
        getEntryAt(getItem, index) match {
          case Some(logEntry) =>
            Option(getGraphic).map(_.asInstanceOf[ImageView].getImage.asInstanceOf[ChunkImage].pixelBuffer) match {
              case Some(pb) =>
                val col = Filter.calcColor(logEntry.value, pb.filters)
                pb.paintBlockAtIndexWithColor(index, logEntry.lineNumber, col.darker())
                // schedule repaint with original color again some time in the future
                val task: Runnable = () => pb.paintBlockAtIndexWithColor(index, logEntry.lineNumber, col)

                // Create a Timeline that fires once after 250 milliseconds
                val timeline = new Timeline(new KeyFrame(Duration.millis(250), (_: ActionEvent) => task.run()))
                timeline.setCycleCount(1) // Ensure it runs only once
                timeline.play()
              case None =>
            }
          case None => // if no valid item found, ignore
        }
      case None => // if outside of a chunk, just ignore
    }
  }

  // see #262 - until this is fixed don't activate the mousemoved handler on windows
  if (OsUtil.isMac) {
    setOnMouseMoved(mouseMovedHandler)
  }
  setOnMouseClicked(mouseClickedHandler)


  override def updateItem(chunk: Chunk, empty: Boolean): Unit = JfxUtils.execOnUiThread {
    super.updateItem(chunk, empty)

    if (empty || Option(chunk).isEmpty || blockSizeProperty.get() <= 0 || widthProperty.get() <= 0) {
      setGraphic(null)
    } else {
      val bv = ChunkImage(chunk
        , filtersProperty
        , selectedLineNumberProperty
        , widthProperty
        , blockSizeProperty
        , firstVisibleTextCellIndexProperty
        , lastVisibleTextCellIndexProperty)
      val view = new ImageView(bv)
      setGraphic(view)
    }
  }

  private def getEntryAt(chunk: Chunk, index: Int): Option[LogEntry] = Try(chunk.entries.get(index)).toOption

}
