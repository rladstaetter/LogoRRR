package app.logorrr.views.visual

import app.logorrr.model.{LogEntry, LogFile}
import app.logorrr.views.Filter
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
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

  val filtersListProperty = new SimpleListProperty[Filter]()

  def filters: Seq[Filter] = Option(filtersListProperty.get()).map(_.asScala.toSeq).getOrElse(Seq())

  /** responsible for determining current logevent */
  private val mouseEventHandler: EventHandler[MouseEvent] = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = LogFile.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidthProperty.get())
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


  def repaint(sWidth: Int, cWidth: Int): Unit = {
    canvasWidthProperty.set(cWidth)
    iv.setImage(SquareImageView.paint(entries, sWidth, cWidth, filters))
  }

}