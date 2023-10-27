package app.logorrr.util

import app.logorrr.views.block.ChunkSpec
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import org.scalatest.wordspec.AnyWordSpec

class MathUtilSpec extends AnyWordSpec {

  "MathUtil" should {
    ".calcBoundedHeight" in {
      assert(MathUtil.calcBoundedHeight(new SimpleDoubleProperty(911.0), new SimpleIntegerProperty(10), ChunkSpec.mkTestLogEntries(70)) != 0)
    }

  }

}
