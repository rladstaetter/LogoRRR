package app.logorrr.jfxbfr

import org.scalatest.wordspec.AnyWordSpec

class ChunkListCellSpec extends AnyWordSpec {

  "ChunkListCell.indexOf" should {
    ".a" in assert(ChunkListCell.indexOf(0, 0, 100, 1000) == 0)
    ".b" in assert(ChunkListCell.indexOf(0, 0, 10, 100) == 0)
    ".c" in assert(ChunkListCell.indexOf(11, 0, 10, 100) == 1)
    ".d" in assert(ChunkListCell.indexOf(99, 0, 10, 100) == 9)
    ".f" in assert(ChunkListCell.indexOf(0, 11, 10, 100) == 10)
    ".e" in assert(ChunkListCell.indexOf(99, 99, 10, 100) == 99)
  }

}
