package app.logorrr.views.text

import app.logorrr.clv.color.ColorUtil
import app.logorrr.views.search.MutableSearchTerm
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.{Background, BackgroundFill}
import javafx.scene.paint.Color

object LogoRRRLabel:

  def mkL(text: String, color: Color): Label =
    val l = new Label(text)
    if color != MutableSearchTerm.UnclassifiedColor then
      l.setBackground(ColorUtil.mkBg(color))
    l
