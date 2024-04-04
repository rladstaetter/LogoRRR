package app.logorrr.views

import app.logorrr.io.FileId

trait UiNodeAware {

  def uiNode(id: FileId): UiNode

}
