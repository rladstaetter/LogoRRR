package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.views.search.{MutableSearchTerm, SearchTerm}
import org.scalacheck.Gen

object TestUtil {

  val searchTermGen: Gen[SearchTerm] = for {f <- Gen.oneOf(Settings.JavaLoggingGroup.terms)} yield f

  val mutSearchTermGen: Gen[MutableSearchTerm] = for {
    f <- Gen.oneOf(MutableSearchTerm.DefaultSearchTerms)
  } yield f

}
