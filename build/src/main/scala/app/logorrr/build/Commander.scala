package app.logorrr.build

import java.nio.file.Path

object Commander {

  def execCmd(workingDir: Path, cmds: Seq[String]): Int = {
    System.out.println(cmds.mkString(" "))
    new ProcessBuilder()
      .directory(workingDir.toFile)
      .inheritIO()
      .command(cmds.asJava)
      .start()
      .waitFor()
  }

}
