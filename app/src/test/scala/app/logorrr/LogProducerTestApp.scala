package app.logorrr

import app.logorrr.conf.TimestampSettings

import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import java.util.Scanner

case class SimpleWriter(path: Path) extends Runnable:
  var running = true
  val dtf: DateTimeFormatter = TimestampSettings.DefaultFormatter

  def stop(): Unit = running = false

  override def run(): Unit =
    var linenumber = 1
    Files.write(path, util.Arrays.asList("started, waiting 20 secs ..."), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    Thread.sleep(20000)
    while running do
      Thread.sleep(50)
      val now = LocalDateTime.now()
      val str = s"${dtf.format(now)} $linenumber testlog "
      linenumber += 1
      Files.write(path, util.Arrays.asList(str), StandardOpenOption.CREATE, StandardOpenOption.APPEND)


object LogProducerTestApp:

  def main(args: Array[String]): Unit =
    if args.nonEmpty then
      val path = Paths.get(args(0))
      Files.deleteIfExists(path)
      val writer = SimpleWriter(path)

      new Thread(writer).start()
      Console.println(s"producing log entries in ${path.toAbsolutePath.toString}, press enter to stop")
      val keyboard = new Scanner(System.in)
      keyboard.nextLine()
      writer.stop()
    else
      Console.println("Usage: LogProducer <path to log file>")

