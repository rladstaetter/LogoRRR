package app.logorrr.views.block

import app.logorrr.model.LogEntry

import scala.collection.mutable.ListBuffer

object Chunk {

  def mkChunks(entries: java.util.List[LogEntry]
               , width: Int
               , height: Int
               , blocksize: Int): Seq[Chunk] = {
    if (width != 0 && blocksize != 0) {
      // nr of chunks is calculated as follows:
      // how many entries fit into a chunk?
      // given their size and the width and height of a chunk it is easy to calculate.
      val (cols, rows) = (width / blocksize, height / blocksize)
      val nrElements = rows * cols

      val entriesSize = entries.size()
      var curIndex = 0
      val lb = new ListBuffer[Chunk]

      while (curIndex < entriesSize) {
        val end = if (curIndex + nrElements < entriesSize) {
          curIndex + nrElements
        } else {
          entriesSize
        }
        val blockViewEntries = entries.subList(curIndex, end)
        if (blockViewEntries.size() > 0) {
          lb.addOne(Chunk(blockViewEntries))
        }
        curIndex = curIndex + nrElements
      }
      lb.toSeq
    } else {
      Seq()
    }

  }
}

case class Chunk(entries: java.util.List[LogEntry]) {
  require(!entries.isEmpty, "entries was empty")
  lazy val name = s"chunk:${entries.get(0).lineNumber}_${entries.get(entries.size() - 1).lineNumber}"
}