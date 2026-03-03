package app.logorrr.views.search.st

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.FileId
import app.logorrr.model.RemoveSearchTermButtonEvent
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.util.GfxElements
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.paint.Color
import org.kordamp.ikonli.javafx.FontIcon


object RemoveSearchTermButton extends UiNodeSearchTermAware:

  override def uiNode(fileId: FileId, searchTerm: String): UiNode = UiNode(classOf[RemoveSearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm))

/**
 * If clicked, removes a search term group from
 */
class RemoveSearchTermButton extends AnIkonliButton(GfxElements.Icons.windowClose, GfxElements.ToolTips.mkRemove):

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , visibleBinding: BooleanBinding
           , mutableSearchTerm: MutableSearchTerm): Unit =
    super.init(visibleBinding)
    setOnAction(e => fireEvent(RemoveSearchTermButtonEvent(mutableSearchTerm)))
    idProperty.bind(Bindings.createStringBinding(
      () =>
        (for fileId <- Option(fileIdProperty.get())
             searchTerm <- Option(mutableSearchTerm.valueProperty.get)
        yield RemoveSearchTermButton.uiNode(fileId, searchTerm).value).getOrElse(""),
      fileIdProperty, mutableSearchTerm.valueProperty))

  override def shutdown(): Unit = {
    super.shutdown()
    idProperty().unbind()
    setOnAction(null)
    visibleProperty().unbind()
  }
