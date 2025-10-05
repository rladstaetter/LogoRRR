package app.logorrr.views.a11y

import app.logorrr.io.FileId
import app.logorrr.views.a11y.UiNode

trait UiNodeFileIdAware {

  def uiNode(id: FileId): UiNode

}


