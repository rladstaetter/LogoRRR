package app.logorrr.conf

import com.typesafe.config.{ConfigRenderOptions, ConfigValueFactory}
import javafx.scene.paint.Color
import pureconfig.*

import scala.jdk.CollectionConverters.*
import scala.util.Try

object RenderOptions {
  val opts = ConfigRenderOptions.defaults().setOriginComments(false)
}

object LogoRRRColor {

  given reader: ConfigReader[Color] = {
    ConfigReader.fromStringTry(_ => scala.util.Try(Color.WHITE))
  }

  given writer: ConfigWriter[Color] = ConfigWriter.fromFunction(r => {
    ConfigValueFactory.fromMap(
      Map(
        "color" -> r.toString
      ).asJava
    )
  })

  //  implicit lazy val colorReader: ConfigReader[Color] = ConfigReader[String].map(s => Color.web(s))
  //  implicit lazy val colorWriter: ConfigWriter[Color] = ConfigWriter[String].contramap(c => c.toString)

}