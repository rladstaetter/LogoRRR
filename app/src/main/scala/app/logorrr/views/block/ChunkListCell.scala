package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, JfxUtils}
import app.logorrr.views.search.Filter
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.ListCell
import javafx.scene.image.ImageView
import javafx.scene.input.{MouseButton, MouseEvent}

import scala.util.Try


/**
 * A listcell which can contain one or more log entries
 */
class ChunkListCell(selectedLineNumberProperty: SimpleIntegerProperty
                    , widthProperty: ReadOnlyDoubleProperty
                    , blockSizeProperty: SimpleIntegerProperty
                    , filtersProperty: ObservableList[Filter]
                    , selectInTextView: LogEntry => Unit
                   ) extends ListCell[Chunk] with CanLog {

  // if user selects an entry in the ChunkListView set selectedLineNumberProperty. This property is observed
  // via an listener and a yellow square will be painted.
  private val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      // on left mouse button
      if (me.getButton.equals(MouseButton.PRIMARY)) {
        val index = BlockImage.indexOf(me.getX.toInt, me.getY.toInt, blockSizeProperty.get, widthProperty.get.toInt - BlockImage.ScrollBarWidth)
        getEntryAt(getItem, index) match {
          case Some(value) =>
            // set selected property such that the next repaint will highlight this entry
            selectedLineNumberProperty.set(value.lineNumber)
            // we have to select also the entry in the LogTextView, couldn't get it to work with bindings / listeners
            selectInTextView(value)
          case None => System.err.println("no element found")
        }
      }
    }
  }

  setOnMouseClicked(mouseEventHandler)


  override def updateItem(t: Chunk, empty: Boolean): Unit = JfxUtils.execOnUiThread {
    super.updateItem(t, empty)

    if (empty || Option(t).isEmpty) {
      setGraphic(null)
    } else {
      if (widthProperty.get() > 0) {
        if (blockSizeProperty.get() > 0) {
          val bv = new BlockImage(t.number
            , entries = t.entries
            , selectedLineNumberProperty
            , filtersProperty
            , blockSizeProperty
            , widthProperty
            , heightProperty = new SimpleIntegerProperty(t.height))
          setGraphic(new ImageView(bv))
        } else {
          logTrace(s"Blocksize was ${blockSizeProperty.get()}, setting graphic to null")
          setGraphic(null)
        }
      } else {
        logTrace(s"Width was ${widthProperty.get()}, setting graphic to null")
        setGraphic(null)
      }
    }
  }

  private def getEntryAt(chunk: Chunk, index: Int): Option[LogEntry] = Try(chunk.entries.get(index)).toOption

}
