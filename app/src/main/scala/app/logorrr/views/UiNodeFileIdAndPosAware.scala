package app.logorrr.views

import app.logorrr.io.FileId
import javafx.geometry.Pos

trait UiNodeFileIdAndPosAware {
  def uiNode(id: FileId, pos: Pos): UiNode
}
