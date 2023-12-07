package app.logorrr.util

import app.logorrr.views.block.Chunk
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import org.scalatest.wordspec.AnyWordSpec

class MathUtilSpec extends AnyWordSpec {

  "MathUtil" should {
    ".calcBoundedHeight" in {
      assert(Chunk.calcDimensions(new SimpleDoubleProperty(911.0), new SimpleIntegerProperty(10), new SimpleDoubleProperty(1000))._2 != 0)
    }

  }

}
