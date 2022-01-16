package app.logorrr.views

import app.logorrr.model.LogReport
import app.logorrr.views.main.AppMainBorderPane
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.control.{Tab, TabPane}

import java.nio.file.Path
import scala.jdk.CollectionConverters._

object LogViewTabPane {

  /** constructor to pass parent and do binding */
  def apply(parent: AppMainBorderPane
            , initFileMenu: => Unit): LogViewTabPane = {
    val lvtp = new LogViewTabPane(initFileMenu)
    lvtp.sceneWidthProperty.bind(parent.sceneWidthProperty)
    lvtp.squareWidthProperty.bind(parent.squareWidthProperty)
    lvtp
  }

}

class LogViewTabPane(initFileMenu: => Unit) extends TabPane {

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
        case logView: LogReportTab =>
          // to set 'selected' property in Tab and to trigger repaint correctly (see issue #9)
          getSelectionModel.select(logView)
          logView.repaint()
        case _ =>
      }
    }
  })

  def add(logReport: LogReport): Unit = getTabs.add(LogReportTab(this, logReport, initFileMenu))

  def contains(p: Path): Boolean = getTabs.asScala.map(_.asInstanceOf[LogReportTab]).exists(lr => lr.logReport.path.toAbsolutePath.toString == p.toAbsolutePath.toString)

  /** shutdown all tabs */
  def shutdown(): Unit = {
    for {t <- getTabs.asScala} {
      t match {
        case l: LogReportTab => l.shutdown()
        case _ =>
      }
    }
  }
}