package app.logorrr

import app.logorrr.model.FilterUtil
import app.logorrr.views.MutFilter
import org.scalacheck.Gen

object TestUtil {

  val filterGen: Gen[MutFilter[_]] = for {
    f <- Gen.oneOf(FilterUtil.DefaultFilters)
  } yield f

}
