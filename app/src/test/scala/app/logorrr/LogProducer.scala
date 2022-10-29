package app.logorrr

import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import java.util.Scanner

case class SimpleWriter(path: Path) extends Runnable {
  var running = true
  val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss A");

  def stop(): Unit = running = false

  override def run(): Unit = {
    var linenumber = 1
    while (running) {
      Thread.sleep(100)
      val now = LocalDateTime.now();
      val str = s"${dtf.format(now)} $linenumber testlog "
      linenumber += 1
      Files.write(path, util.Arrays.asList(str), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    }
  }

}

object LogProducer {

  def main(args: Array[String]): Unit = {
    if (args.nonEmpty) {

      val path = Paths.get(args(0))
      Files.deleteIfExists(path)
      val writer = SimpleWriter(path)
      new Thread(writer).start()

      val keyboard = new Scanner(System.in)
      System.out.println(s"producing log entries in ${path.toAbsolutePath.toString}, press enter to stop")
      keyboard.nextLine()
      writer.stop()
    } else {
      println("Usage: LogProducer <path to log file>")
    }

  }
}
