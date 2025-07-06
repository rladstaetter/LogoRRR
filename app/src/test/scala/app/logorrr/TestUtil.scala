package app.logorrr

import app.logorrr.jfxbfr.MutFilter
import app.logorrr.model.FilterUtil
import org.scalacheck.Gen

object TestUtil {

  val filterGen: Gen[MutFilter[_]] = for {
    f <- Gen.oneOf(FilterUtil.DefaultFilters)
  } yield f

}
