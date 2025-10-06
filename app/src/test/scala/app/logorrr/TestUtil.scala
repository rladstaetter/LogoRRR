package app.logorrr

import app.logorrr.views.search.{MutableSearchTerm, SearchTerm}
import org.scalacheck.Gen

object TestUtil {

  val searchTermGen: Gen[SearchTerm] = for {f <- Gen.oneOf(SearchTerm.DefaultSearchTerms)} yield f

  val mutSearchTermGen: Gen[MutableSearchTerm] = for {
    f <- Gen.oneOf(MutableSearchTerm.DefaultFilters)
  } yield f

}
