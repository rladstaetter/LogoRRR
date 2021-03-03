package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ListChangeListener
import javafx.scene.control.{Tab, TabPane}
import net.ladstatt.logboard.views.LogView

import scala.jdk.CollectionConverters._

object LogViewTabPane {

  def apply(parent: LogBoardMainBorderPane): LogViewTabPane = {
    val lvtp = new LogViewTabPane
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp.squareWidthProperty.bind(parent.squareWidthProperty)
    lvtp
  }
}

class LogViewTabPane extends TabPane {

  /** bound to sceneWidthProperty of parent LogBoardMainBorderPane */
  val sceneWidthProperty = new SimpleIntegerProperty()

  /** bound to squareWidthProperty of parent LogBoardMainBorderPane */
  val squareWidthProperty = new SimpleIntegerProperty()

  def add(logReport: LogReport): Unit = {
    getTabs.add(new LogView(logReport, squareWidthProperty.get, sceneWidthProperty.get))
  }


  getSelectionModel.selectedItemProperty().addListener(new ChangeListener[Tab] {
    override def changed(observableValue: ObservableValue[_ <: Tab], t: Tab, t1: Tab): Unit = {
      t1 match {
        case logView: LogView => logView.doRepaint()
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
              case logView: LogView => logView.doRepaint()
              case _ => // do nothing
            }
          }
        }
      }
    }
  })


}
