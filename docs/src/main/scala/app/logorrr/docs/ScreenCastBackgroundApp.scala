package app.logorrr.docs

import app.logorrr.clv.color.ColorUtil
import app.logorrr.docs.Area.*
import net.ladstatt.util.log.TinyLog
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.{Background, BackgroundFill, BorderPane}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

import java.nio.file.Paths

/**
 * White background for screencasts
 */
object ScreenCastBackgroundApp {

  def main(args: Array[String]): Unit = {
    TinyLog.init(Paths.get("target/backgroundapp.log"))
    javafx.application.Application.launch(classOf[ScreenCastBackgroundApp], args*)
  }
}

class ScreenCastBackgroundApp extends javafx.application.Application with TinyLog {

  def start(stage: Stage): Unit = {
    Application.setUserAgentStylesheet("/app/logorrr/LogoRRR.css")
    val s0 = R2560x1600

    val rectangle = new Rectangle(s0.width,s0.height)
    rectangle.setFill(Color.WHITE)
    val pane = new BorderPane(rectangle)
    pane.setBackground(ColorUtil.mkBg(Color.WHITE))
    pane.setPadding(new Insets(100, 100, 100, 100))
    val scene = new Scene(pane)
    stage.setScene(scene)
    stage.show()
  }


}