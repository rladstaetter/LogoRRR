package app.logorrr.views.ops

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}

object IncreaseBlockSizeButton extends UiNodeFileIdAware {

  /** size of icon to increase block size */
  val Size = 16


  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[IncreaseBlockSizeButton])

}

class IncreaseBlockSizeButton