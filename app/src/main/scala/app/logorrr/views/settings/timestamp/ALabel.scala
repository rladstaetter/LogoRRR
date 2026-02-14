package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.model.BoundId
import javafx.geometry.Pos
import javafx.scene.control.Label

class ALabel(l: String, fn: FileId => String) extends Label(l) with BoundId(f => FromLabel.uiNode(f).value):
  setPrefWidth(100)
  setAlignment(Pos.CENTER_LEFT)
