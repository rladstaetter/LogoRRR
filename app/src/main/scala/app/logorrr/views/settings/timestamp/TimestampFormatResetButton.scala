package app.logorrr.views.settings.timestamp

import app.logorrr.clv.ChunkListView
import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.{UiNode, UiNodeFileIdAware}
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Button

import java.util

object TimestampFormatResetButton extends UiNodeFileIdAware {
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[TimestampFormatResetButton])
}

/** reset log timestamp settings to default values */
class TimestampFormatResetButton(mutLogFileSettings: MutLogFileSettings
                                 , chunkListView: ChunkListView[LogEntry]
                                 , logEntries: ObservableList[LogEntry]
                                 , opsToolBar: OpsToolBar
                                 , closeStage: => Unit) extends Button("reset") {
  setId(TimestampFormatResetButton.uiNode(mutLogFileSettings.getFileId).value)
  setAlignment(Pos.CENTER_RIGHT)
  setPrefWidth(180)
  setOnAction(_ => {
    mutLogFileSettings.setSomeLogEntryInstantFormat(None)
    LogoRRRGlobals.persist()
    // we have to deactivate this listener otherwise
    chunkListView.removeInvalidationListener()
    val tempList = new util.ArrayList[LogEntry]()
    logEntries.forEach(e => {
      tempList.add(e.withOutTimestamp())
    })
    logEntries.setAll(tempList)
    // activate listener again
    chunkListView.addInvalidationListener()
    opsToolBar.initializeRanges()
    closeStage
  })
}
