package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
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
 * A listcell which can contain one or more log entries
 */
class ChunkListCell(selectedLineNumberProperty: SimpleIntegerProperty
                    , widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty
                    , filtersProperty: ObservableList[Filter]
                    , scrollTo: LogEntry => Unit
                   ) extends ListCell[Chunk] with CanLog {

  // if user selects an entry in the ChunkListView set selectedLineNumberProperty. This property is observed
  // via an listener and a yellow square will be painted.
  private val mouseClickedHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      // on left mouse button
      if (me.getButton.equals(MouseButton.PRIMARY)) {
        val index = BlockImage.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, widthProperty.get.toInt - BlockImage.ScrollBarWidth)
        getEntryAt(getItem, index) match {
          case Some(value) =>
            selectedLineNumberProperty.set(value.lineNumber)
            // we have to select also the entry in the LogTextView, couldn't get it to work with bindings / listeners
            scrollTo(value)
          case None =>
        }
      }
    }
  }
  private val mouseMovedHandler: EventHandler[MouseEvent] = (me: MouseEvent) => {
    val index = BlockImage.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, widthProperty.get.toInt - BlockImage.ScrollBarWidth)
    getEntryAt(getItem, index) match {
      case Some(logEntry) =>
        Option(getGraphic).map(_.asInstanceOf[ImageView].getImage.asInstanceOf[BlockImage].pixelBuffer) match {
          case Some(pb) =>
            val col = Filter.calcColor(logEntry.value, pb.filters)
            pb.paintBlockAtIndexWithColor(index, logEntry.lineNumber, col.darker())
            // schedule repaint with original color again some time in the future
            val task: Runnable = () => pb.paintBlockAtIndexWithColor(index, logEntry.lineNumber, col)

            // Create a Timeline that fires once after 500 milliseconds
            val timeline = new Timeline(new KeyFrame(Duration.millis(500), (_: ActionEvent) => task.run()))
            timeline.setCycleCount(1) // Ensure it runs only once
            timeline.play()
          case None =>
        }
      case None =>
    }

  }

  setOnMouseMoved(mouseMovedHandler)
  setOnMouseClicked(mouseClickedHandler)


  override def updateItem(t: Chunk, empty: Boolean): Unit = JfxUtils.execOnUiThread {
    super.updateItem(t, empty)

    if (empty || Option(t).isEmpty || blockSizeProperty.get() <= 0 || widthProperty.get() <= 0) {
      setGraphic(null)
    } else {
      val bv = BlockImage(t.number
        , entries = t.entries
        , selectedLineNumberProperty
        , filtersProperty
        , blockSizeProperty
        , widthProperty
        , heightProperty = new SimpleIntegerProperty(t.height))
      val view = new ImageView(bv)
      setGraphic(view)
    }
  }

  private def getEntryAt(chunk: Chunk, index: Int): Option[LogEntry] = Try(chunk.entries.get(index)).toOption

}
