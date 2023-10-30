package app.logorrr.views.block

import app.logorrr.model.LogEntry
import app.logorrr.util.MathUtil
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList

import scala.collection.mutable.ListBuffer

object Chunk {

  def mkChunks(entriesProperty: ObservableList[LogEntry]
               , widthProperty: ReadOnlyDoubleProperty
               , blockSizeProperty: SimpleIntegerProperty
               , listViewHeightProperty: ReadOnlyDoubleProperty): Seq[Chunk] = {
    if (widthProperty.get() != 0 && blockSizeProperty.get() != 0) {
      // nr of chunks is calculated as follows:
      // how many entries fit into a chunk?
      // given their size and the width and height of a chunk it is easy to calculate.
      val (cols, rows, height) = MathUtil.calcBoundedHeight(widthProperty, blockSizeProperty, entriesProperty, listViewHeightProperty)
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