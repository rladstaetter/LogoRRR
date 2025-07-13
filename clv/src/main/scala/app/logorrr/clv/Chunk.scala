package app.logorrr.clv

import java.util
import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal.RoundingMode

object Chunk {

  val MaxHeight = 4096

  /** defines how many list cells should be rendered per visible ListView space */
  val ChunksPerVisibleViewPort = 6

  private def roundDown(doubleNumber: Double): Int = {
    BigDecimal.double2bigDecimal(doubleNumber).setScale(0, RoundingMode.DOWN).intValue
  }

  def calcDimensions(blockSize: Int
                     , listViewWidth: Double
                     , listViewHeight: Double
                     , chunksPerPage: Int): (Int, Int) = {


    // to not get into division by zero territory
    val cols: Int = if (listViewWidth < blockSize) 1 else roundDown(listViewWidth / blockSize)

    // per default, use 4 cells per visible page, align height with blocksize such that
    // we don't get artifacts. Further, make sure that the calculated height does not exceed
    // MaxHeight of underlying texture painting mechanism.

    // DO NOT REMOVE since the first division throws away the remainder and the multiplication
    // yields the best approximation of MaxHeight.
    val maxHeight = (MaxHeight / blockSize) * blockSize
    val heightCandidate = roundDown((listViewHeight / chunksPerPage) / blockSize) * blockSize
    // height is constrained by MaxHeight (which is 4096 currently in my experience) and BlockImage.DefaultBlocksPerPage x blocksize
    // as a lower bound, otherwise we'll get later problems
    val h2 = Math.max(heightCandidate, chunksPerPage * blockSize)
    val height = Math.min(h2, maxHeight)
    (cols, height)
  }

  /**
   * Depending on the visible area of a listview, partitions the entries list to one or several Chunks and fills them
   * with the appropriate number of elements.
   *
   * @param entries        entries which should be shown
   * @param blockSize      width/height of a block
   * @param listViewWidth  width of listview
   * @param listViewHeight height of listview
   * @return a sequence of Chunks, filled with the given entries
   */
  def mkChunks[A](entries: util.List[A]
                  , blockSize: Int
                  , listViewWidth: Double
                  , listViewHeight: Double
                  , nrChunksPerPage: Int): Seq[Chunk[A]] = {
    if (
      entries.isEmpty ||
        listViewWidth == 0 ||
        listViewHeight == 0 ||
        blockSize == 0) {
      Seq()
    } else {
      // how many entries fit into a chunk?
      val (cols, height) = calcDimensions(blockSize, listViewWidth, listViewHeight, nrChunksPerPage)
      val nrElements = height / blockSize * cols

      val entriesSize = entries.size()
      var curIndex = 0
      val lb = new ListBuffer[Chunk[A]]

      while (curIndex < entriesSize) {
        val end = if (curIndex + nrElements < entriesSize) {
          curIndex + nrElements
        } else {
          entriesSize
        }
        val blockViewEntries = entries.subList(curIndex, end)
        if (blockViewEntries.size() > 0) {
          lb.addOne(new Chunk(lb.size, blockViewEntries, cols, height))
        }
        curIndex = curIndex + nrElements
      }
      lb.toSeq
    }

  }
}

/**
 * Container for entries in order to fill ListView[Chunk]
 *
 * Will be painted by [[ChunkImage]]
 *
 * @param number  index in the ListView
 * @param entries entries contained in this Chunk
 * @param cols    number of columns in this Chunk (needed for mouse over/mouse press events)
 * @param height  height of Chunk
 */
class Chunk[E](val number: Int
               , val entries: java.util.List[E]
               , val cols: Int
               , val height: Int) {
  require(!entries.isEmpty, "entries was empty")
}