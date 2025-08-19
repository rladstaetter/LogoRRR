package app.logorrr.views.ops

import app.logorrr.io.FileId
import app.logorrr.views.{UiNode, UiNodeFileIdAware}

object IncreaseBlockSizeButton extends UiNodeFileIdAware {

  /** size of icon to increase block size */
  val Size = 16


  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[IncreaseBlockSizeButton])

}

class IncreaseBlockSizeButton