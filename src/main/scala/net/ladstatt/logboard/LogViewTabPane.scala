package net.ladstatt.logboard

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{Tab, TabPane}
import net.ladstatt.logboard.views.LogView

object LogViewTabPane {

  /** constructor to pass parent and do binding */
  def apply(parent: LogBoardMainBorderPane): LogViewTabPane = {
    val lvtp = new LogViewTabPane
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp.squareWidthProperty.bind(parent.squareWidthProperty)
    lvtp
  }

}

class LogViewTabPane extends TabPane {

  setStyle(
    """
      |-fx-background-image: url(/net/ladstatt/logboard/save-as.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-size: 100%;
      |""".stripMargin)

  /** bound to sceneWidthProperty of parent LogBoardMainBorderPane */
  val sceneWidthProperty = new SimpleIntegerProperty()

  /** bound to squareWidthProperty of parent LogBoardMainBorderPane */
  val squareWidthProperty = new SimpleIntegerProperty()

  def add(logReport: LogReport): Unit = getTabs.add(LogView(this, logReport))


  getSelectionModel.selectedItemProperty().addListener(new ChangeListener[Tab] {
    override def changed(observableValue: ObservableValue[_ <: Tab], t: Tab, t1: Tab): Unit = {
      t1 match {
        case logView: LogView => logView.doRepaint()
        case _ =>
      }
    }
  })


}
