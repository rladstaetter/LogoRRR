package app.logorrr.views.block

import app.logorrr.model.LogEntry
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import org.scalatest.wordspec.AnyWordSpec

import java.util
import java.util.Collections

object ChunkSpec {

  /** creates a list of LogEntries with the given size */
  def mkTestLogEntries(size: Int): java.util.List[LogEntry] = {
    val entries = {
      (0 until size).foldLeft(new java.util.ArrayList[LogEntry]())((acc, e) => {
        acc.add(LogEntry(e, "", None))
        acc
      })
    }
    entries
  }

}

class ChunkSpec extends AnyWordSpec {

  def mkChunks(l: java.util.List[LogEntry]
               , width: Double
               , blocksize: Int): Seq[Chunk] = {
    Chunk.mkChunks(FXCollections.observableArrayList(l)
      , new SimpleDoubleProperty(width)
      , new SimpleIntegerProperty(blocksize)
      , new SimpleDoubleProperty(1000))
  }

  def mkChunks(nrEntries: Int
               , width: Double
               , blocksize: Int): Seq[Chunk] = {
    mkChunks(ChunkSpec.mkTestLogEntries(nrEntries), width, blocksize)
  }

  "Chunk" should {
    val height = 100
    "empty when width is 0" in assert(mkChunks(Collections.emptyList[LogEntry](), 0, 23).isEmpty)
    "empty when blocksize is 0" in assert(mkChunks(Collections.emptyList[LogEntry](), 30, 0).isEmpty)
    "nonempty" in assert(mkChunks(util.Arrays.asList(LogEntry(0, "test", None)), 1, 1).nonEmpty)
    "size 1" in assert(mkChunks(util.Arrays.asList(LogEntry(0, "test", None), LogEntry(1, "test2", None)), 2, 1).size == 1)
    "size 2" in assert(mkChunks(util.Arrays.asList(LogEntry(0, "test", None), LogEntry(1, "test2", None)), 1, 1).size == 1)
    "200 entries bagger" in {
      val chunks = mkChunks(200, 100, 1)
      assert(chunks.size == 1)
      val entriesOfFirstChunk = chunks(0).entries
      assert(entriesOfFirstChunk.get(entriesOfFirstChunk.size() - 1).lineNumber == 199)
    }
    "testchunks" in {
      val chunks = mkChunks(1000, 100, 10)
      assert(chunks.size == 10)
      assert(chunks.forall(c => c.entries.size == 100))
    }
    // create 10 chunks consisting of 10 x 10 blocks yielding 1000 entries
    "1000 entries" in {
      val chunks = mkChunks(1000, 100, 10)
      assert(chunks.size == 10)
      assert(chunks.forall(c => c.entries.size == 100))
      // last entry has line number 999, is in last chunk
      assert(chunks.last.entries.get(chunks.last.entries.size - 1).lineNumber == 999)
    }

  }
}
