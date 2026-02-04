package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.BoundFileId
import javafx.scene.control.TextField

class ATextField(fn: FileId => String) extends TextField with BoundFileId(fn):
  setPrefWidth(60)
  setEditable(false)
