package app.logorrr.jfxbfr

import javafx.scene.paint.Color
import org.scalatest.wordspec.AnyWordSpec

class ColorUtilSpec extends AnyWordSpec {

  "Convert color to string and back" in {
    val color = Color.WHITE
    val c1 = Color.web(color.toString)
    assert(color == c1)
  }

}