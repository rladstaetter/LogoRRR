package app.logorrr

import app.logorrr.io.FileId

import java.nio.file.{Path, Paths}

object TestFiles {

  val baseDir: Path = Paths.get("src/test/resources/app/logorrr/")

  val simpleLog0: FileId = FileId(baseDir.resolve("SimpleLog-0.txt")) // 4 entries
  val simpleLog1: FileId = FileId(baseDir.resolve("SimpleLog-1.txt")) // 100 entries, with time information
  val simpleLog2: FileId = FileId(baseDir.resolve("SimpleLog-2.txt")) // 5 entries
  val simpleLog3: FileId = FileId(baseDir.resolve("SimpleLog-3.txt")) // 4 entries
  val simpleLog4: FileId = FileId(baseDir.resolve("SimpleLog-4.txt")) // 4 entries
  val simpleLog5: FileId = FileId(baseDir.resolve("SimpleLog-5.txt")) // 1 line, 'a b c d e'

  val timedLog: FileId = simpleLog1

  val zipFileContaining10Files: FileId = FileId(baseDir.resolve("zip-containing-10-files.zip"))

  val seq: Seq[FileId] = Seq(
    simpleLog0
    , simpleLog1
    , simpleLog2
    , simpleLog3
    , simpleLog4
    , simpleLog5
  )
}
