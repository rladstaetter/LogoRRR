package net.ladstatt.logorrr.views.visual

import javafx.beans.property.{SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseEvent
import net.ladstatt.logorrr.{LogEntry, LogReport}

import scala.collection.mutable


class SquareImageScrollPane(entries: mutable.Buffer[LogEntry]
                            , selectedIndexProperty: SimpleIntegerProperty
                            , selectedEntryProperty: SimpleObjectProperty[LogEntry]
                            , squareWidth: Int
                            , canvasWidth: Int) extends ScrollPane {

  val canvasWidthProperty = new SimpleIntegerProperty(canvasWidth)
/*
  viewportBoundsProperty().addListener(new InvalidationListener {
    override def invalidated(observable: Observable): Unit = {
      println("viewportbound invalidated")
    }
  })

  vvalueProperty.addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
      println(t1.doubleValue() + " " + getViewportBounds)
    }
  })*/

  /** responsible for determining current logevent */
  val mouseEventHandler = new EventHandler[MouseEvent]() {
    override def handle(me: MouseEvent): Unit = {
      val index = LogReport.indexOf(me.getX.toInt, me.getY.toInt, squareWidth, canvasWidthProperty.get())
      val entry = entries(index)
      selectedIndexProperty.set(index)
      selectedEntryProperty.set(entry)
    }
  }

  val iv = SquareImageView(entries, squareWidth, canvasWidth)
  iv.setOnMouseMoved(mouseEventHandler)
  setContent(iv)

  def repaint(sWidth: Int, cWidth: Int): Unit = {
    canvasWidthProperty.set(cWidth)
    iv.setImage(SquareImageView.paint(entries, sWidth, cWidth))
  }

}