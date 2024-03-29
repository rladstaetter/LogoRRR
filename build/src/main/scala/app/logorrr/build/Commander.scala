package app.logorrr.build

import java.nio.file.Path
import scala.jdk.CollectionConverters._

object Commander {

  def execCmd(workingDir: Path, cmds: Seq[String]): Int = {
    Console.println(cmds.mkString(" "))
    new ProcessBuilder()
      .directory(workingDir.toFile)
      .inheritIO()
      .command(cmds.asJava)
      .start()
      .waitFor()
  }

}
