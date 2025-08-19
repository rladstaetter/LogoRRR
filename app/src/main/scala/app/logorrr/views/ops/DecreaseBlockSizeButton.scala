package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}

object DecreaseBlockSizeButton extends UiNodeFileIdAware {

  val Size = 8

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseBlockSizeButton])

}

class DecreaseBlockSizeButton