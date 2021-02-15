package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ListChangeListener
import javafx.scene.control.{Tab, TabPane}
import net.ladstatt.logboard.views.LogView

import scala.jdk.CollectionConverters._

class LogViewTabPane extends TabPane {

  val canvasWidthProperty = new SimpleIntegerProperty(1000)

  val squareWidthProperty = new SimpleIntegerProperty(7)

  def getSquareWidth(): Int = squareWidthProperty.get

  def getCanvasWidth(): Int = canvasWidthProperty.get()


  getSelectionModel.selectedItemProperty().addListener(new ChangeListener[Tab] {
    override def changed(observableValue: ObservableValue[_ <: Tab], t: Tab, t1: Tab): Unit = {
      t1 match {
        case logView: LogView =>
          if (logView.isSelected) {
            logView.doRepaint(getCanvasWidth())
          }
        case _ =>
      }
    }
  })

  getTabs.addListener(new ListChangeListener[Tab] {
    override def onChanged(change: ListChangeListener.Change[_ <: Tab]): Unit = {
      while (change.next) {
        if (change.wasAdded()) {
          for (r <- change.getAddedSubList.asScala) {
            r match {
              case logView: LogView => logView.doRepaint(getCanvasWidth())
              case _ => // do nothing
            }
          }
        }
      }
    }
  })


}
