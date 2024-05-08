package app.logorrr.conf

import org.scalactic.source.Position
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.Checkers

trait CheaterSpec extends AnyWordSpec with Checkers {

  // hack until i find out why it can't find this implicit position
  given pos: Position = Position("", "", 0)

}
