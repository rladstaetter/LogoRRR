package app.logorrr

import java.nio.file.{Path, Paths}

object TestFiles {

  private val baseDir = Paths.get("src/test/resources/app/logorrr/")

  val simpleLog0: Path = baseDir.resolve("SimpleLog-0.txt")
  val simpleLog1: Path = baseDir.resolve("SimpleLog-1.txt")
  val simpleLog2: Path = baseDir.resolve("SimpleLog-2.txt")
  val simpleLog3: Path = baseDir.resolve("SimpleLog-3.txt")
  val simpleLog4: Path = baseDir.resolve("SimpleLog-4.txt")

  val seq: Seq[Path] = Seq(
    simpleLog0
    , simpleLog1
    , simpleLog2
    , simpleLog3
    , simpleLog4
  )
}
