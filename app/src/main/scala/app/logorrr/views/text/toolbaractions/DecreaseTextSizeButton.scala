package app.logorrr.views.text.toolbaractions

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}

object DecreaseTextSizeButton extends UiNodeFileIdAware {

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseTextSizeButton])

}

class DecreaseTextSizeButton