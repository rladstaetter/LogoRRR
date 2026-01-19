package app.logorrr.views.search.st

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.GfxElements
import app.logorrr.views.search.st.RemoveSearchTermButton.cssStyle
import javafx.scene.control.Button
import javafx.scene.paint.Color


object RemoveSearchTermButton extends UiNodeSearchTermAware:

  private val cssStyle: String =
    """
      |-fx-padding: 0pt;
      |-fx-spacing: 0pt;
      |-fx-border-width: 0pt;
      |-fx-border-radius: 0pt;
      |-fx-background-radius: 0pt;
      |-fx-border-color: transparent;
      |-fx-background-color: transparent;""".stripMargin


  /** css gymnastics to make remove button look pretty */
  def buttonCssStyle(color: Color): String =
    s"""
       |-fx-padding: 0;
       |-fx-border-width: 0pt;
       |-fx-border-radius: 0pt;
       |-fx-background-radius: 0pt;
       |-fx-font-family: 'Font Awesome 6 Free Regular';
       |-fx-icon-size: 16;
       |-fx-icon-code: far-window-close;
       |-fx-icon-color: ${ColorUtil.hexString(color)};
       |""".stripMargin

  override def uiNode(fileId: FileId, searchTerm: String): UiNode = UiNode(classOf[RemoveSearchTermButton].getSimpleName + "-" + HashUtil.md5Sum(fileId.absolutePathAsString + ":" + searchTerm))

/**
 * If clicked, removes a search term group from
 */
class RemoveSearchTermButton extends Button:
  val icon = GfxElements.closeWindowIcon
  setGraphic(icon)
  setTooltip(GfxElements.mkRemoveTooltip)
  setStyle(cssStyle)
  


