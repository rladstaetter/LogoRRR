package app.logorrr.views.settings.timestamp

import app.logorrr.conf.FileId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}

object FromLabel extends UiNodeFileIdAware:
  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[FromLabel])

class FromLabel extends ALabel("from column", FromLabel.uiNode(_).value)
