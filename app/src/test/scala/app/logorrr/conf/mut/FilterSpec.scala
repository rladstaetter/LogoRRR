package app.logorrr.conf.mut

import app.logorrr.model.LogFileSettings
import app.logorrr.views.search.Filter
import org.scalacheck.Gen

object FilterSpec {

  val gen: Gen[Filter] = for {
    f <- Gen.oneOf(LogFileSettings.DefaultFilter)
  } yield f

}
