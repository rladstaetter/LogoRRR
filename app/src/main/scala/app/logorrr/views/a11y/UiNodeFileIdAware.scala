package app.logorrr.views.a11y

import app.logorrr.io.FileId

trait UiNodeFileIdAware {

  def uiNode(id: FileId): UiNode

}


