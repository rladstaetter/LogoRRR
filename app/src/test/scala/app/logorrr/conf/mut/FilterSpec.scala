package app.logorrr.conf.mut

import app.logorrr.LogoRRRSpec
import app.logorrr.model.LogFileSettings
import app.logorrr.views.search.Filter
import javafx.scene.paint.Color
import org.scalacheck.Gen

object FilterSpec {

  val gen: Gen[Filter] = for {
    f <- Gen.oneOf(LogFileSettings.DefaultFilters)
  } yield f

}

class FilterSpec extends LogoRRRSpec {

  "Convert color to string and back" in {
    val color = Color.WHITE
    val c1 = Color.web(color.toString)
    assert(color == c1)
  }

}