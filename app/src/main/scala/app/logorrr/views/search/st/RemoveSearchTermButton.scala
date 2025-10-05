package app.logorrr.views.search.st

import app.logorrr.clv.color.ColorUtil
import app.logorrr.io.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.MutableSearchTerm
import javafx.scene.control.{Button, Tooltip}
import javafx.scene.paint.Color
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


object RemoveSearchTermButton extends UiNodeSearchTermAware {
  val ZeroPadding = "-fx-padding: 0;"

  override def uiNode(fileId: FileId, searchTerm: MutableSearchTerm): UiNode = UiNode(classOf[RemoveSearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm.getPredicate.description))
}

class RemoveSearchTermButton extends Button {

  setGraphic(new FontIcon(FontAwesomeRegular.WINDOW_CLOSE))
  setTooltip(new Tooltip("remove"))

  useParentBackgroundColor()

  setOnMouseEntered(_ => getCurrentBackground.foreach(color => setStyle(RemoveSearchTermButton.ZeroPadding + ColorUtil.mkCssBackgroundString(color.darker()))))
  setOnMouseExited(_ => useParentBackgroundColor())

  private def useParentBackgroundColor(): Unit = setStyle(RemoveSearchTermButton.ZeroPadding + "-fx-background-color: inherit;")

  private def getCurrentBackground: Option[Color] = {
    if (getBackground != null && !getBackground.getFills.isEmpty) {
      val paint = getBackground.getFills.get(0).getFill
      paint match {
        case color: Color => Option(color)
        case _ => None
      }
    } else None
  }

}

