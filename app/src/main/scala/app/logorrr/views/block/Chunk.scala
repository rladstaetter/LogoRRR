package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.MathUtil
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList

import scala.collection.mutable.ListBuffer

object Chunk {

  def calcDimensions(widthProperty: ReadOnlyDoubleProperty
                     , blockSizeProperty: SimpleIntegerProperty
                     , listViewHeightProperty: ReadOnlyDoubleProperty): (Int, Int) = {
    val w = if (widthProperty.get() - BlockImage.ScrollBarWidth >= 0) widthProperty.get() - BlockImage.ScrollBarWidth else widthProperty.get()

    // to not get into division by zero territory
    val cols: Int = if (w < blockSizeProperty.get()) 1 else MathUtil.roundUp(w / blockSizeProperty.get())

    // per default, use 4 cells per visible page, align height with blocksize such that
    // we don't get artifacts. Further, make sure that the calculated height does not exceed
    // MaxHeight of underlying texture painting mechanism.

    // DO NOT REMOVE since the first division throws away the remainder and the multiplication
    // yields the best approximation of MaxHeight.
    val maxHeight = (BlockImage.MaxHeight / blockSizeProperty.get()) * blockSizeProperty.get()
    val heightCandidate = MathUtil.roundDown((listViewHeightProperty.get() / BlockImage.DefaultBlocksPerPage) / blockSizeProperty.get()) * blockSizeProperty.get()
    // height is constrained by MaxHeight (which is 4096 currently in my experience) and BlockImage.DefaultBlocksPerPage x blocksize
    // as a lower bound, otherwise we'll get later problems
    val h2 = Math.max(heightCandidate, BlockImage.DefaultBlocksPerPage * blockSizeProperty.get())
    val height = Math.min(h2, maxHeight)
    (cols, height)
  }


  def mkChunks(entriesProperty: ObservableList[LogEntry]
               , widthProperty: ReadOnlyDoubleProperty
               , blockSizeProperty: SimpleIntegerProperty
               , listViewHeightProperty: ReadOnlyDoubleProperty): Seq[Chunk] = {
    if (widthProperty.get() != 0 && blockSizeProperty.get() != 0) {
      // nr of chunks is calculated as follows:
      // how many entries fit into a chunk?
      // given their size and the width and height of a chunk it is easy to calculate.
      val (cols, height) = calcDimensions(widthProperty, blockSizeProperty, listViewHeightProperty)
      val nrElements = height / blockSizeProperty.get() * cols

      val entriesSize = entriesProperty.size()
      var curIndex = 0
      val lb = new ListBuffer[Chunk]

      while (curIndex < entriesSize) {
        val end = if (curIndex + nrElements < entriesSize) {
          curIndex + nrElements
        } else {
          entriesSize
        }
        val blockViewEntries = entriesProperty.subList(curIndex, end)
        if (blockViewEntries.size() > 0) {
          lb.addOne(new Chunk(lb.size, blockViewEntries, height))
        }
        curIndex = curIndex + nrElements
      }
      lb.toSeq
    } else {
      Seq()
    }

  }
}

class Chunk(val number: Int
            , val entries: java.util.List[LogEntry]
            , val height: Int) {
  require(!entries.isEmpty, "entries was empty")
}