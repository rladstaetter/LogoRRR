package app.logorrr.views.block

import org.scalatest.wordspec.AnyWordSpec

class BlockImageSpec extends AnyWordSpec {

  "BlockImage.calcVirtualHeight" should {
    "sc0" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 0) == 0)
    "sc1" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 1) == 7)
    "sc10" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 10) == 7)
    "sc11" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 11) == 14)
  }
}
