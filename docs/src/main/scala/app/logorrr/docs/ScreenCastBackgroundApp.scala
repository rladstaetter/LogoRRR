package app.logorrr.docs

import app.logorrr.docs.Area._
import app.logorrr.util.CanLog
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.{Background, BackgroundFill, BorderPane}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

/**
 * White background for screencasts
 */
object ScreenCastBackgroundApp {

  def main(args: Array[String]): Unit = {
    javafx.application.Application.launch(classOf[ScreenCastBackgroundApp], args: _*)
  }
}

class ScreenCastBackgroundApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    val s0 = R1280x800

    val rectangle = new Rectangle(s0.width,s0.height)
    rectangle.setFill(Color.WHITE)
    val pane = new BorderPane(rectangle)
    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)))
    pane.setPadding(new Insets(100, 100, 100, 100))
    val scene = new Scene(pane)
    stage.setScene(scene)
    stage.show()
  }


}