package app.logorrr

import app.logorrr.views.{MutableSearchTerm, SearchTerm}
import org.scalacheck.Gen

object TestUtil {

  val searchTermGen: Gen[SearchTerm] = for {f <- Gen.oneOf(SearchTerm.DefaultFilters)} yield f

  val mutFilterGen: Gen[MutableSearchTerm] = for {
    f <- Gen.oneOf(MutableSearchTerm.DefaultFilters)
  } yield f

}
