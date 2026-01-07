package app.logorrr.views.a11y

import app.logorrr.conf.FileId

trait UiNodeFileIdAware {

  def uiNode(id: FileId): UiNode

}


