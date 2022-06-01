package app.logorrr.views.text

import javafx.scene.paint.Color
import org.scalatest.wordspec.AnyWordSpec

class LinePartSpec extends AnyWordSpec {


  "LinePart" should {
    "simple match 1" in {
      val str = "abcdef"
      val ps = Seq(LinePart("cdef", 2, Option(Color.BLUE)))
      val labels = LinePart.reduce(str, ps)
      assert(Seq(LinePart("ab", 0), LinePart("cdef", 2, Option(Color.BLUE))) == labels)
    }
    "simple match 2" in {
      val str = "abcdef"
      val ps = Seq(LinePart("cd", 2, Option(Color.BLUE)))
      val labels = LinePart.reduce(str, ps)
      assert(labels == Seq(LinePart("ab", 0), LinePart("cd", 2, Option(Color.BLUE)), LinePart("ef", 4)))
    }
  }

}
