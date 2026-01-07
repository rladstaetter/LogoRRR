package app.logorrr.views.a11y

import app.logorrr.conf.FileId
import javafx.geometry.Pos

trait UiNodeFileIdAndPosAware {
  def uiNode(id: FileId, pos: Pos): UiNode
}
