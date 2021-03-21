package net.ladstatt.logorrr.views.visual

import javafx.beans.property.{SimpleIntegerProperty, SimpleListProperty, SimpleObjectProperty}
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import net.ladstatt.logorrr.{CollectionUtils, Filter, LogEntry, LogReport}

import scala.collection.mutable
import scala.jdk.CollectionConverters.CollectionHasAsScala


class SquareImageScrollPane(entries: mutable.Buffer[LogEntry]
                            , selectedIndexProperty: SimpleIntegerProperty
                            , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                            , squareWidth: Int
                            , canvasWidth: Int) extends ScrollPane {

  val canvasWidthProperty = new SimpleIntegerProperty(canvasWidth)

  // wird nicht richtig updated von 'remove button'
  val searchFilters = new SimpleListProperty[Filter]()

  /** responsible for determining current logevent */
  val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = LogReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidthProperty.get())
      val entry = entries(index)
      selectedIndexProperty.set(index)
      selectedEntryProperty.set(entry)
    }
  }

  val iv = SquareImageView(entries.size, squareWidth, canvasWidth)
  iv.setOnMouseMoved(mouseEventHandler)
  setContent(iv)

  def repaint(sWidth: Int, cWidth: Int): Unit = {
    canvasWidthProperty.set(cWidth)
    val filters: Seq[Filter] = Option(searchFilters.get()).map(_.asScala.toSeq).getOrElse(Seq())
    println("SQF: " + filters)
    iv.setImage(SquareImageView.paint(entries, sWidth, cWidth, filters))
  }

}