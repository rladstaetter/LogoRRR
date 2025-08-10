package app.logorrr

import app.logorrr.views.{MutFilter, SearchTerm}
import org.scalacheck.Gen

object TestUtil {

  val searchTermGen: Gen[SearchTerm] = for {f <- Gen.oneOf(SearchTerm.DefaultFilters)} yield f

  val mutFilterGen: Gen[MutFilter] = for {
    f <- Gen.oneOf(MutFilter.DefaultFilters)
  } yield f

}
