package app.logorrr.views.text.contextactions

import app.logorrr.model.LogEntry
import app.logorrr.util.ClipBoardUtils
import javafx.scene.control.{MenuItem, MultipleSelectionModel}
import net.ladstatt.util.log.CanLog

class CopyEntriesMenuItem(selectionModel: MultipleSelectionModel[LogEntry])
  extends MenuItem("copy selection to clipboard") with CanLog {

  setOnAction(_ => ClipBoardUtils.copyToClipboard(selectionModel.getSelectedItems))

}
