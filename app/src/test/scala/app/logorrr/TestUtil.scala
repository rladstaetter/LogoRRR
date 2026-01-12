package app.logorrr

import app.logorrr.conf.{LogFileSettings, SearchTerm, Settings}
import app.logorrr.views.search.MutableSearchTerm
import org.scalacheck.Gen

object TestUtil:

  val searchTermGen: Gen[SearchTerm] = for f <- Gen.oneOf(LogFileSettings.JuLSearchTermGroup.terms) yield f

  val mutSearchTermGen: Gen[MutableSearchTerm] = for
    f <- Gen.oneOf(MutableSearchTerm.DefaultSearchTerms)
  yield f

