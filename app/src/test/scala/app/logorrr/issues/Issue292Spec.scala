package app.logorrr.issues

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class Issue292Spec extends AnyWordSpecLike with Matchers {

  "Issue 292" must {
    "single line" in {
      println(2)
    }
  }
}
