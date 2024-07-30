package app.logorrr.views.text.contextactions

import app.logorrr.model.LogEntry
import app.logorrr.util.{CanLog, ClipBoardUtils}
import javafx.scene.control.{MenuItem, MultipleSelectionModel}

class CopyEntriesMenuItem(selectionModel: MultipleSelectionModel[LogEntry])
  extends MenuItem("copy selection to clipboard") with CanLog {

  setOnAction(_ => ClipBoardUtils.copyToClipboard(selectionModel.getSelectedItems))

}
