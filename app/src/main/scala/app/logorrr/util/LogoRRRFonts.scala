package app.logorrr.util

import javafx.scene.text.Font

object LogoRRRFonts {

  // load font thanks to https://www.jetbrains.com/lp/mono/
  Font.loadFont(getClass.getResource("/app/logorrr/JetBrainsMono-Regular.ttf").toExternalForm, 12)

  def jetBrainsMono(size: Int): String =
    s"""|-fx-font-family: 'JetBrains Mono';
        |-fx-font-size: ${size.toString} px;
        |""".stripMargin

}
