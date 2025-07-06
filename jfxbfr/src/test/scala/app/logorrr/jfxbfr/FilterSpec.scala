package app.logorrr.jfxbfr

import javafx.scene.paint.Color
import org.scalacheck.Gen
import org.scalatest.wordspec.AnyWordSpec

object FilterSpec {

  val gen: Gen[Fltr] = for {
    f <- Gen.oneOf(Fltr.DefaultFilters)
  } yield f

}

class FilterSpec extends AnyWordSpec {

  "Convert color to string and back" in {
    val color = Color.WHITE
    val c1 = Color.web(color.toString)
    assert(color == c1)
  }

}