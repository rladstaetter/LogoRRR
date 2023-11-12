package app.logorrr.views.logfiletab

import app.logorrr.util.OsUtil
import javafx.scene.control.{MenuItem, Tab}

import java.awt.Desktop
import java.nio.file.Paths
import scala.collection.immutable.Seq
import scala.jdk.CollectionConverters.CollectionHasAsScala

class OpenInFinderMenuItem(pathAsString: String) extends MenuItem(if (OsUtil.isWin) {
  "Show Log in Explorer"
} else if (OsUtil.isMac) {
  "Show Log in Finder"
} else {
  "Show Log ..."
}) {

  setOnAction(_ => Desktop.getDesktop.open(Paths.get(pathAsString).getParent.toFile))

}

class CloseMenuItem(fileTab: => LogFileTab) extends MenuItem("Close") {

  setOnAction(_ => Option(fileTab.getTabPane.getSelectionModel.getSelectedItem).foreach { t =>
    fileTab.cleanupBeforeClose()
    fileTab.getTabPane.getTabs.remove(t)
  })

}

class CloseOtherFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close Other Files") {
  val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if (t.asInstanceOf[LogFileTab].pathAsString == fileTab.pathAsString) {
          None
        } else {
          t.asInstanceOf[LogFileTab].cleanupBeforeClose()
          Option(t)
        }
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted: _*)
  })

}

class CloseAllFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close All Files") {
  val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        t.asInstanceOf[LogFileTab].cleanupBeforeClose()
        Option(t)
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted: _*)
  })

}


class CloseLeftFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close Files to the Left") {
  val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    var deletethem = true
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if (t.asInstanceOf[LogFileTab].pathAsString == fileTab.pathAsString) {
          deletethem = false
          None
        } else {
          if (deletethem) {
            t.asInstanceOf[LogFileTab].cleanupBeforeClose()
            Option(t)
          } else {
            None
          }
        }
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted: _*)
  })

}


class CloseRightFilesMenuItem(fileTab: => LogFileTab) extends MenuItem("Close Files to the Right") {
  val tabPane = fileTab.getTabPane
  setOnAction(_ => {
    var deletethem = false
    val toBeDeleted: Seq[Tab] = {
      tabPane.getTabs.asScala.flatMap { t =>
        if (t.asInstanceOf[LogFileTab].pathAsString == fileTab.pathAsString) {
          deletethem = true
          None
        } else {
          if (deletethem) {
            t.asInstanceOf[LogFileTab].cleanupBeforeClose()
            Option(t)
          } else {
            None
          }
        }
      }.toSeq
    }
    tabPane.getTabs.removeAll(toBeDeleted: _*)
  })

}