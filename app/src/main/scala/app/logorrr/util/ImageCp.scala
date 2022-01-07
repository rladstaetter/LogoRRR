package app.logorrr.util

import javafx.scene.image.{Image, ImageView}

case class ImageCp(value: String, width: Int, height: Int) extends CpResource(value) {
  def imageView(): ImageView =
    new ImageView(new Image(value, width, height, true, true, true))
}
