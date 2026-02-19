package app.logorrr.util

import javafx.beans.binding.StringBinding
import javafx.beans.property.Property
import javafx.scene.text.Font

object LogoRRRFonts:

  // load font thanks to https://www.jetbrains.com/lp/mono/
  Font.loadFont(getClass.getResource("/app/logorrr/JetBrainsMono-Regular.ttf").toExternalForm, 12)

  def jetBrainsMono(size: Int): String =
    s"""|-fx-font-family: 'JetBrains Mono';
        |-fx-font-size: ${size.toString} px;
        |""".stripMargin

class JetbrainsMonoFontStyleBinding(fontSizeProperty: Property[Number]) extends StringBinding:
  bind(fontSizeProperty)

  override def computeValue(): String = LogoRRRFonts.jetBrainsMono(fontSizeProperty.getValue.intValue())
