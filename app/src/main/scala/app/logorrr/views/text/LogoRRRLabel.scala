package app.logorrr.views.text

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.Color

object LogoRRRLabel {

  def mkBg(color: Color) = new Background(new BackgroundFill(color, new CornerRadii(1.25), Insets.EMPTY))

  def mkL(msg: String, someColor: Option[Color] = None): Label = {
    val l = new Label(msg)
    someColor.foreach(c => l.setBackground(mkBg(c)))
    l
  }
}
