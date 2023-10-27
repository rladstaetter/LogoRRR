package app.logorrr.views.block

import app.logorrr.model.LogEntry
import org.scalatest.wordspec.AnyWordSpec

import java.util
import java.util.Collections

class ChunkSpec extends AnyWordSpec {

  "Chunk" should {
    val height = 100
    "empty when width is 0" in assert(Chunk.mkChunks(Collections.emptyList(), 0, height, 23).isEmpty)
    "empty when blocksize is 0" in assert(Chunk.mkChunks(Collections.emptyList(), 30, height, 0).isEmpty)
    "nonempty" in assert(Chunk.mkChunks(util.Arrays.asList(LogEntry(0, "test", None)), 1, height, 1).nonEmpty)
    "size 1" in assert(Chunk.mkChunks(util.Arrays.asList(LogEntry(0, "test", None), LogEntry(1, "test2", None)), 2, height, 1).size == 1)
    "size 2" in assert(Chunk.mkChunks(util.Arrays.asList(LogEntry(0, "test", None), LogEntry(1, "test2", None)), 1, height, 1).size == 1)
    "200 entries bagger" in {
      val chunks = Chunk.mkTestChunks(200, 100, height, 1)
      assert(chunks.size == 1)
      val entriesOfFirstChunk = chunks(0).entries
      assert(entriesOfFirstChunk.get(entriesOfFirstChunk.size() - 1).lineNumber == 199)
    }
    "testchunks" in {
      val chunks = Chunk.mkTestChunks(1000, 100, 100, 10)
      assert(chunks.size == 10)
      assert(chunks.forall(c => c.entries.size == 100))
    }
    // create 10 chunks consisting of 10 x 10 blocks yielding 1000 entries
    "1000 entries" in {
      val chunks = Chunk.mkTestChunks(1000, 100, 100, 10)
      assert(chunks.size == 10)
      assert(chunks.forall(c => c.entries.size == 100))
      // last entry has line number 999, is in last chunk
     assert(chunks.last.entries.get(chunks.last.entries.size - 1).lineNumber == 999)
    }

  }
}
