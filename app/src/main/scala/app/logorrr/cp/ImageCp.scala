package app.logorrr.cp

import javafx.scene.image.{Image, ImageView}

case class ImageCp(url: String, width: Int, height: Int) extends CpResource {

  def imageView(): ImageView =
    new ImageView(new Image(url, width, height, true, true, true))

}


