package app.logorrr.views.menubar

import app.logorrr.conf.Settings
import app.logorrr.model
import app.logorrr.model.{LogEntry, LogReportDefinition}
import app.logorrr.util.{CanLog, LogoRRRFileChooser, OsUtil}
import app.logorrr.views.learner
import app.logorrr.views.learner.LogFormatLearnerStage
import app.logorrr.views.menubar.FileMenu.RecentFilesMenu
import javafx.scene.control.{Menu, MenuItem}

import java.nio.file.{Files, Path, Paths}

object FileMenu {

  class LearnLogFormat(logEntry: => LogEntry) extends MenuItem("learn log format") {
    setOnAction(_ => LogFormatLearnerStage(logEntry))
  }

  class OpenMenuItem(openLogFile: Path => Unit)
    extends MenuItem("Open") with CanLog {

    setOnAction(_ => {
      new LogoRRRFileChooser("Open log file").showAndWait() match {
        case Some(logFile) =>
          openLogFile(logFile)
        case None => logInfo("Cancelled open file ...")
      }

    })
  }

  class QuitMenuItem(closeApplication: => Unit) extends MenuItem("Quit") {
    setOnAction(e => closeApplication)
  }

  object RecentFilesMenu {


    object RecentFileMenu {

      case class LearnLogFormat(path: Path, updateLogDef: LogReportDefinition => Unit) extends MenuItem("Learn log format") {
        setOnAction(_ => {
          // read first line of log file
          val reader = Files.newBufferedReader(path)
          val firstLogLine = reader.readLine()
          reader.close()
          val lfls = learner.LogFormatLearnerStage(LogEntry(firstLogLine))
          lfls.showAndWait()
          updateLogDef(model.LogReportDefinition(path, lfls.getLogColumnDef()))
        })
      }

    }


    class RecentFileMenu(path: Path
                         , updateLogDef: LogReportDefinition => Unit) extends Menu(path.getFileName.toString) {
      getItems.add(RecentFileMenu.LearnLogFormat(path, updateLogDef))

    }


  }

  case class RecentFilesMenu(paths: Seq[Path]
                             , updateLogDef: LogReportDefinition => Unit) extends Menu("Log Files") {
    getItems.addAll(paths.map(p => new RecentFilesMenu.RecentFileMenu(p, updateLogDef)): _*)
  }


  class CloseAllMenuItem(removeAllLogFiles: => Unit) extends MenuItem("Close All") {
    setOnAction(_ => removeAllLogFiles)
  }

}

class FileMenu(openLogFile: Path => Unit
               , removeAllLogFiles: => Unit
               , updateLogDef: LogReportDefinition => Unit
               , closeApplication: => Unit) extends Menu("File") with CanLog {

  val settings = {
    logTrace("Reinit File Menu ...")
    Settings.someSettings.getOrElse(Settings.Default)
  }

  val recentFiles: Seq[String] = settings.recentFiles.logReportDefinition.map(_.pathAsString)

  val recentFilesMenu = RecentFilesMenu(recentFiles.map(f => Paths.get(f)), updateLogDef)

  def init(): Unit = {
    getItems.clear()
    getItems.add(new FileMenu.OpenMenuItem(openLogFile))

    if (recentFiles.nonEmpty) {
      getItems.add(recentFilesMenu)
      val closeAllMenuItems = new FileMenu.CloseAllMenuItem(removeAllLogFiles)
      getItems.add(closeAllMenuItems)
    }

    if (OsUtil.isWin) {
      getItems.add(new FileMenu.QuitMenuItem(closeApplication))
    }
  }

  init()
}