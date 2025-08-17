package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}

object IncreaseTextSizeButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[IncreaseTextSizeButton])

}

class IncreaseTextSizeButton


