package app.logorrr.views.visual

import app.logorrr.model.{LogEntry, LogReport}
import app.logorrr.Filter
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent

import scala.collection.mutable
import scala.jdk.CollectionConverters.CollectionHasAsScala


class SquareImageScrollPane(entries: mutable.Buffer[LogEntry]
                            , selectedIndexProperty: SimpleIntegerProperty
                            , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                            , squareWidth: Int
                            , canvasWidth: Int) extends ScrollPane {

  val canvasWidthProperty = new SimpleIntegerProperty(canvasWidth)

  val searchFilters = new SimpleListProperty[Filter]()

  /** responsible for determining current logevent */
  val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = LogReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidthProperty.get())
      val entry = entries(index)
      selectedIndexProperty.set(index)
      selectedEntryProperty.set(entry)
      val pw = getWritableImage().getPixelWriter
      val x = (me.getX.toInt / squareWidth) * squareWidth
      val y = (me.getY.toInt / squareWidth) * squareWidth
      SquareImageView.paintRect(pw, x, y, squareWidth, entry.calcColor(filters).darker)
    }
  }

  val iv = SquareImageView(entries.size, squareWidth, canvasWidth)
  iv.setOnMouseClicked(mouseEventHandler)
  setContent(iv)

  def getWritableImage(): WritableImage = iv.getImage.asInstanceOf[WritableImage]

  def filters: Seq[Filter] = Option(searchFilters.get()).map(_.asScala.toSeq).getOrElse(Seq())

  def repaint(sWidth: Int, cWidth: Int): Unit = {
    canvasWidthProperty.set(cWidth)
    iv.setImage(SquareImageView.paint(entries, sWidth, cWidth, filters))
  }

}