package app.logorrr.util

import javafx.scene.image.{Image, ImageView}

import java.util.Properties


abstract class CpResource(value: String)

case class PropsCp(classPathResource : String) extends CpResource(classPathResource) {

  def asProperties(clazz: Class[_]): Properties = {
    val properties = new Properties()
    val is = clazz.getResourceAsStream(classPathResource)
    try {
      properties.load(is)
    } finally {
      Option(is).foreach(_.close)
    }
    properties
  }

}

case class ImageCp(value: String, width: Int, height: Int) extends CpResource(value) {
  def imageView(): ImageView =
    new ImageView(new Image(value, width, height, true, true, true))
}
