package app.logorrr.docs

import app.logorrr.docs.Area._
import net.ladstatt.util.log.CanLog
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.{Background, BackgroundFill, BorderPane}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import net.ladstatt.app.{AppId, AppMeta}

/**
 * White background for screencasts
 */
object ScreenCastBackgroundApp {

  def main(args: Array[String]): Unit = {
    val appMeta = net.ladstatt.app.AppMeta(AppId("ScreenCastBackgroundApp", "screencastbackgroundapp", "screencastbackground.app"), AppMeta.LogFormat)
    net.ladstatt.app.AppMeta.initApp(appMeta)
    javafx.application.Application.launch(classOf[ScreenCastBackgroundApp], args: _*)
  }
}

class ScreenCastBackgroundApp extends javafx.application.Application with CanLog {

  def start(stage: Stage): Unit = {
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    val s0 = R2560x1600

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