package app.logorrr.views.text.toolbaractions

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}

object DecreaseTextSizeButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[DecreaseTextSizeButton])


class DecreaseTextSizeButton