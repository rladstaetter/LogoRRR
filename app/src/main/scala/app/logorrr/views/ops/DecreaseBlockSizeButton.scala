package app.logorrr.views.ops

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}

object DecreaseBlockSizeButton extends UiNodeFileIdAware:

  val Size = 8

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseBlockSizeButton])


class DecreaseBlockSizeButton