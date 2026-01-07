package app.logorrr.clv

import app.logorrr.model.LogEntry
import javafx.collections.{FXCollections, ObservableList}
import org.scalatest.wordspec.AnyWordSpec

import scala.jdk.CollectionConverters.CollectionHasAsScala

object ChunkSpec {

  /** creates a list of LogEntries with the given size */
  def mkTestLogEntries(size: Int): java.util.List[LogEntry] = {
    val entries = {
      (0 until size).foldLeft(new java.util.ArrayList[LogEntry]())((acc, e) => {
        acc.add(LogEntry(e, "", None, None))
        acc
      })
    }
    entries
  }

}

class ChunkSpec extends AnyWordSpec {

  def mkTestChunks(nrEntries: Int
                   , width: Int
                   , blocksize: Int
                   , listViewHeight: Double): ObservableList[Chunk[LogEntry]] = {
    val l = FXCollections.observableArrayList[Chunk[LogEntry]]()
    Chunk.updateChunks(l, ChunkSpec.mkTestLogEntries(nrEntries), blocksize, width, listViewHeight, Chunk.ChunksPerVisibleViewPort)
  }

  "Chunk" should {
    val lvh = 1000
    "empty when width is 0" in assert(mkTestChunks(0, 23, 0, lvh).isEmpty)
    "empty when height is 0" in assert(mkTestChunks(10, 23, 10, 0).isEmpty)
    "empty when blocksize is 0" in assert(mkTestChunks(10, 20, 0, lvh).isEmpty)
    "nonempty" in assert(!mkTestChunks(1, 1, 1, lvh).isEmpty)
    "size 1" in assert(mkTestChunks(2, 1, 2, lvh).size == 1)
    "size 2" in assert(mkTestChunks(2, 1, 1, lvh).size == 1)

    "200 entries bagger" in {
      val chunks = mkTestChunks(200, 100, 1, 1000)
      assert(chunks.size == 1)
      val entriesOfFirstChunk = chunks.get(0).entries
      assert(entriesOfFirstChunk.get(entriesOfFirstChunk.size() - 1).lineNumber == 199)
    }
    // default chunk size is 4
    "test default chunk size" in {
      val chunks = mkTestChunks(1000, 100 + ChunkListView.DefaultScrollBarWidth, 10, 1000)
      assert(chunks.size == Chunk.ChunksPerVisibleViewPort)
      assert(chunks.get(0).entries.size == 176)
      assert(chunks.get(1).entries.size == 176)
      assert(chunks.get(2).entries.size == 176)
      assert(chunks.get(3).entries.size == 176)
      assert(chunks.get(4).entries.size == 176)
      assert(chunks.get(5).entries.size == 120)
      val lastEntries = chunks.get(chunks.size() - 1).entries
      assert(lastEntries.get(lastEntries.size - 1).lineNumber == 999)
    }
  }
  ".calcDimensions" should {
    "scenario-a" in assert(Chunk.calcDimensions(10, 911.0, 1000, Chunk.ChunksPerVisibleViewPort)._2 != 0)
    "scenario-b" in assert(Chunk.calcDimensions(50, 949.0, 647.5, Chunk.ChunksPerVisibleViewPort) == (18, 300))
  }
  "issue-231" should {
    "scenario a" in {
      val nrEntries = 1000
      val chunks = mkTestChunks(nrEntries, 1353, 16, 235.5)
      assert(chunks.size == 2)

      val firstChunk = chunks.get(0)
      val lastChunk = chunks.get(chunks.size() - 1)

      assert(firstChunk.height == 96)
      assert(lastChunk.height == 96)

      assert(firstChunk.entries.size == 504)
      assert(lastChunk.entries.size == 496)

      assert(chunks.asScala.map(_.entries.size).sum == nrEntries)
    }
    "scenario b" in {
      val nrEntries = 100
      val chunks = mkTestChunks(nrEntries, 949, 50, 647.5)
      assert(chunks.size == 1)

      val firstChunk = chunks.get(0)
      assert(firstChunk.height == 300)
      assert(firstChunk.entries.size == 100)

      assert(chunks.asScala.map(_.entries.size).sum == nrEntries)
    }
  }
}
