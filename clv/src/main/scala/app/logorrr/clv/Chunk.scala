package app.logorrr.clv

import javafx.collections.{FXCollections, ObservableList}

import java.util

object Chunk:

  val MaxHeight = 4096

  /** defines how many list cells should be rendered per visible ListView space */
  val ChunksPerVisibleViewPort = 6


  /**
   * Calculates how many blocks are to be painted per line and the height of a Chunkcell.
   *
   * @param blockSize      size of a block
   * @param listViewWidth  visible width
   * @param listViewHeight visible height
   * @param chunksPerPage  how many chunks we have for the viewport
   * @return returns a tuple consisting of 2 Ints : how many blocks exist in x direction, and the height of a Chunk
   */
  def calcDimensions(blockSize: Int
                     , listViewWidth: Double
                     , listViewHeight: Double
                     , chunksPerPage: Int): (Int, Int) =

    // to not get into division by zero territory
    val numberOfBlocksInXDirection: Int = if listViewWidth < blockSize then 1 else (listViewWidth / blockSize).toInt

    // per default, use ChunksPerVisibleViewPort cells per visible page, align height with blocksize
    // such that we don't get artifacts. Further, make sure that the calculated height does not exceed
    // MaxHeight of underlying texture painting mechanism.

    // DO NOT REMOVE since the first division throws away the remainder and the multiplication
    // yields the best approximation of MaxHeight.
    val maxHeight = (MaxHeight / blockSize) * blockSize
    val heightCandidate = ((listViewHeight / chunksPerPage) / blockSize).toInt * blockSize
    // height is constrained by MaxHeight (which is 4096 currently in my experience)
    // and BlockImage.DefaultBlocksPerPage x blocksize as a lower bound, otherwise we'll get problems later
    val h2 = Math.max(heightCandidate, chunksPerPage * blockSize)
    val chunkHeight = Math.min(h2, maxHeight)
    (numberOfBlocksInXDirection, chunkHeight)

  /**
   * Depending on the visible area of a listview, partitions the entries list to one or several Chunks and fills them
   * with the appropriate number of elements.
   *
   * @param elements       elements to display
   * @param blockSize      width/height of a block
   * @param listViewWidth  width of listview
   * @param listViewHeight height of listview
   * @return the modified chunkList
   */
  def modifyChunkList[A](chunkList: ObservableList[Chunk[A]]
                         , elements: util.List[A]
                         , blockSize: Int
                         , listViewWidth: Int
                         , listViewHeight: Double
                         , nrChunksPerPage: Int): Unit =

    if (elements.isEmpty || listViewWidth == 0 || listViewHeight == 0 || blockSize == 0) then {
      chunkList.clear()
    } else
      // how many entries fit into a chunk?
      val (lineBlockCount, chunkHeight) = calcDimensions(blockSize, listViewWidth, listViewHeight, nrChunksPerPage)
      val nrElements = chunkHeight / blockSize * lineBlockCount

      val entriesSize = elements.size()
      var curIndex = 0
      val l = FXCollections.observableArrayList[Chunk[A]]()

      while curIndex < entriesSize do
        val end = if curIndex + nrElements < entriesSize then
          curIndex + nrElements
        else
          entriesSize
        val blockViewEntries: util.List[A] = elements.subList(curIndex, end)
        if blockViewEntries.size() > 0 then
          l.add(new Chunk(l.size, blockViewEntries, lineBlockCount, chunkHeight))
        curIndex = curIndex + nrElements

      chunkList.setAll(l)


/**
 * Container for entries in order to fill ListView[Chunk]
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