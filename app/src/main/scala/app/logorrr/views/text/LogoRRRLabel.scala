package app.logorrr.views.text

import app.logorrr.views.search.MutableSearchTerm
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill}
import javafx.scene.paint.Color

object LogoRRRLabel:

  private def mkBg(color: Color) = new Background(new BackgroundFill(color, null, Insets.EMPTY))

  def mkL(text: String, color: Color): Label =
    val l = new Label(text)
    if color != MutableSearchTerm.UnclassifiedColor then
      l.setBackground(mkBg(color))
    l
