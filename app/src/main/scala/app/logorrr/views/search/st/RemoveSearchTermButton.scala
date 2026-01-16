package app.logorrr.views.search.st

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.{MutableSearchTerm, GfxElements}
import javafx.scene.control.Button
import javafx.scene.paint.Color


object RemoveSearchTermButton extends UiNodeSearchTermAware:
  val ZeroPadding = "-fx-padding: 0;"

  override def uiNode(fileId: FileId, searchTerm: String): UiNode = UiNode(classOf[RemoveSearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm))

class RemoveSearchTermButton extends Button:
  val icon = GfxElements.closeWindowIcon
  setGraphic(icon)
  setTooltip(GfxElements.mkRemoveTooltip)
//  useParentBackgroundColor()
//  setOnMouseEntered(_ => getCurrentBackground.foreach(color => setStyle(RemoveSearchTermButton.ZeroPadding + ColorUtil.mkCssBackgroundString(color.darker()))))
//   setOnMouseExited(_ => useParentBackgroundColor())

  private def useParentBackgroundColor(): Unit = setStyle(RemoveSearchTermButton.ZeroPadding  + "-fx-background-color: inherit;")

  private def getCurrentBackground: Option[Color] =
    if getBackground != null && !getBackground.getFills.isEmpty then
      val paint = getBackground.getFills.get(0).getFill
      paint match
        case color: Color => Option(color)
        case _ => None
    else None


