package app.logorrr.views.visual

import app.logorrr.model.{LogEntries, LogEntry}
import app.logorrr.views.Filter
import app.logorrr.views.main.LogoRRRGlobals
import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent

import scala.collection.mutable
import scala.jdk.CollectionConverters.CollectionHasAsScala


object SquareImageScrollPane {

  def indexOf(x: Int, y: Int, squareWidth: Int, canvasWidth: Int): Int = y / squareWidth * (canvasWidth / squareWidth) + x / squareWidth

}

class SquareImageScrollPane(entries: mutable.Buffer[LogEntry]
                            , selectedIndexProperty: SimpleIntegerProperty
                            , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                            , canvasWidth: Int) extends ScrollPane {



  val canvasWidthProperty = new SimpleIntegerProperty(canvasWidth)

  def getCanvasWidth(): Int = canvasWidthProperty.get()

  val filtersListProperty = new SimpleListProperty[Filter]()

  def filters: Seq[Filter] = Option(filtersListProperty.get()).map(_.asScala.toSeq).getOrElse(Seq())

  /** responsible for determining current logevent */
  private val mouseEventHandler: EventHandler[MouseEvent] = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val currentSquareWidth = LogoRRRGlobals.settings.squareImageSettings.widthProperty.get
      val index = SquareImageScrollPane.indexOf(me.getX.toInt, me.getY.toInt, currentSquareWidth, getCanvasWidth())
      val entry = entries(index)
      selectedIndexProperty.set(index)
      selectedEntryProperty.set(entry)
      val pw = getWritableImage().getPixelWriter
      val x = (me.getX.toInt / currentSquareWidth) * currentSquareWidth
      val y = (me.getY.toInt / currentSquareWidth) * currentSquareWidth
      SquareImageView.paintRect(pw, x, y, currentSquareWidth, entry.calcColor(filters).darker)
    }
  }


  def getWritableImage(): WritableImage =  getContent.asInstanceOf[SquareImageView].getImage.asInstanceOf[WritableImage]


  def repaint(cWidth: Int): Unit = {
    canvasWidthProperty.set(cWidth)
    val squareWidth = LogoRRRGlobals.settings.squareImageSettings.widthProperty.get
    if (Option(getContent).isEmpty) {
      val iv = {
        val iiv = SquareImageView(entries.size, squareWidth, canvasWidth)
        iiv.setOnMouseClicked(mouseEventHandler)
        iiv
      }
      setContent(iv)
    }

    getContent.asInstanceOf[SquareImageView].setImage(SquareImageView.paint(entries,  squareWidth, cWidth, filters))
  }

}