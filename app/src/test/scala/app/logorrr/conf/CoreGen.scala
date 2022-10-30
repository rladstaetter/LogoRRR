package app.logorrr.conf

import org.scalacheck.Gen

object CoreGen {
  val booleanGen: Gen[Boolean] = Gen.oneOf(false, true)

}
