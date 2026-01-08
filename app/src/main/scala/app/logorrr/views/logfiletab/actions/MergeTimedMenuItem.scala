package app.logorrr.views.logfiletab.actions

import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.LogFileTab
import app.logorrr.views.main.MainTabPane
import javafx.scene.control.{Menu, MenuItem}

import java.nio.file.Files
import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala

object MergeTimedMenuItem extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[MergeTimedMenuItem])


class CombineFileMenuItem(thisFileId: FileId
                          , otherFileId: FileId
                          , mainTabPane: MainTabPane) extends MenuItem(otherFileId.value):

  setOnAction(_ => {
    for thisTab <- mainTabPane.getByFileId(thisFileId)
         otherTab <- mainTabPane.getByFileId(otherFileId) yield {
      val list = new util.ArrayList[LogEntry]()
      list.addAll(thisTab.entries)
      list.addAll(otherTab.entries)
      list.sort((o1: LogEntry, o2: LogEntry) => {
        (o1.someInstant, o2.someInstant) match {
          case (None, None) => 0
          case (None, Some(_)) => -1
          case (Some(_), None) => 1
          case (Some(d1), Some(d2)) => d1.compareTo(d2)
        }
      })
      val lines = new util.ArrayList[String]()
      list.forEach((t: LogEntry) => lines.add(t.value))
      val fileName = thisFileId.fileName + "_" + otherFileId.fileName
      val mergedPath = thisFileId.asPath.getParent.resolve(fileName)
      Files.write(mergedPath, lines)
      mainTabPane.addFile(FileId(mergedPath))
    }
  })



class MergeTimedMenuItem(thisFileId: FileId, tabPane: MainTabPane) extends Menu("Merge file with ...") {
  setId(MergeTimedMenuItem.uiNode(thisFileId).value)

  for t <- tabPane.getTabs.asScala do
    val otherFileTab = t.asInstanceOf[LogFileTab]
    if otherFileTab.fileId != thisFileId && otherFileTab.mutLogFileSettings.hasTimestampSetting.get then
      getItems.add(new CombineFileMenuItem(thisFileId, otherFileTab.fileId, tabPane))

  /*
    setOnAction(_ => {
      var deletethem = true
      val toBeDeleted: Seq[Tab] = {
        tabPane.getTabs.asScala.flatMap { t =>
          if (t.asInstanceOf[LogFileTab].fileId == fileTab.fileId) {
            deletethem = false
            None
          } else {
            if (deletethem) {
              t.asInstanceOf[LogFileTab].shutdown()
              Option(t)
            } else {
              None
            }
          }
        }.toSeq
      }
      tabPane.getTabs.removeAll(toBeDeleted: _*)
      // reinit context menu since there are no files left on the left side and thus the option should not be shown anymore
      tabPane.getTabs.get(0).asInstanceOf[LogFileTab].initContextMenu()

    })
  */
}