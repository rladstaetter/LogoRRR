package app.logorrr.views

import app.logorrr.io.FileId

trait UiNodeFileIdAware {

  def uiNode(id: FileId): UiNode

}

