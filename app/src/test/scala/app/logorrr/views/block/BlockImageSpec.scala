package app.logorrr.views.block

import org.scalatest.wordspec.AnyWordSpec

class BlockImageSpec extends AnyWordSpec {

  "BlockImage.indexOf" should {
    ".a" in assert(BlockImage.indexOf(0, 0, 100, 1000) == 0)
    ".b" in assert(BlockImage.indexOf(0, 0, 10, 100) == 0)
    ".c" in assert(BlockImage.indexOf(11, 0, 10, 100) == 1)
    ".d" in assert(BlockImage.indexOf(99, 0, 10, 100) == 9)
    ".f" in assert(BlockImage.indexOf(0, 11, 10, 100) == 10)
    ".e" in assert(BlockImage.indexOf(99, 99, 10, 100) == 99)
  }

  "BlockImage.calcVirtualHeight" should {
    "sc0" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 0) == 0)
    "sc1" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 1) == 7)
    "sc10" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 10) == 7)
    "sc11" in assert(BlockImage.calcVirtualHeight(1, 7, 10, 11) == 14)
  }
}
