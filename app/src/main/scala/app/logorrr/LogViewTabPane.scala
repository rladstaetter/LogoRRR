package app.logorrr

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{Tab, TabPane}
import app.logorrr.views.LogView

import scala.jdk.CollectionConverters._

object LogViewTabPane {

  /** constructor to pass parent and do binding */
  def apply(parent: AppMainBorderPane): LogViewTabPane = {
    val lvtp = new LogViewTabPane
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp.squareWidthProperty.bind(parent.squareWidthProperty)
    lvtp
  }

}

class LogViewTabPane extends TabPane {

  /** bound to sceneWidthProperty of parent logorrrMainBorderPane */
  val sceneWidthProperty = new SimpleIntegerProperty()

  /** bound to squareWidthProperty of parent logorrrMainBorderPane */
  val squareWidthProperty = new SimpleIntegerProperty()

  setStyle(
    """
      |-fx-background-image: url(/app/logorrr/save-as.png);
      |-fx-background-position: center center;
      |-fx-background-repeat: no-repeat;
      |-fx-background-color: BEIGE;
      |-fx-background-size: 100%;
      |""".stripMargin)

  getSelectionModel.selectedItemProperty().addListener(new ChangeListener[Tab] {
    override def changed(observableValue: ObservableValue[_ <: Tab], t: Tab, t1: Tab): Unit = {
      t1 match {
        case logView: LogView => logView.repaint()
        case _ =>
      }
    }
  })

  def add(logReport: LogReport): Unit = getTabs.add(LogView(this, logReport))

  /** shutdown all tabs */
  def shutdown(): Unit = {
    for {t <- getTabs.asScala} {
      t match {
        case l: LogView => l.shutdown()
        case _ =>
      }
    }
  }
}
