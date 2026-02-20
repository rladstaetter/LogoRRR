package app.logorrr.views.search.st

import app.logorrr.clv.color.ColorUtil
import app.logorrr.conf.FileId
import app.logorrr.util.HashUtil
import app.logorrr.views.a11y.{UiNode, UiNodeSearchTermAware}
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.RemoveSearchTermButton.{buttonCssStyle, cssStyle}
import app.logorrr.views.util.GfxElements
import javafx.beans.binding.{Bindings, BooleanBinding}
import javafx.beans.property.ObjectPropertyBase
import javafx.collections.ObservableList
import javafx.scene.control.Button
import javafx.scene.paint.{Color, Paint}


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
  val icon = GfxElements.Icons.windowClose
  setGraphic(icon)
  setTooltip(GfxElements.ToolTips.mkRemove)
  setStyle(cssStyle)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , visibleBinding: BooleanBinding
           , mutableSearchTerm: MutableSearchTerm
           , mutSearchTerms: ObservableList[MutableSearchTerm]): Unit =
    visibleProperty().bind(visibleBinding)
    // TODO implement via bubble event
    setOnAction(e => mutSearchTerms.remove(mutableSearchTerm))
    idProperty.bind(Bindings.createStringBinding(
      () =>
        (for fileId <- Option(fileIdProperty.get())
             searchTerm <- Option(mutableSearchTerm.valueProperty.get)
        yield RemoveSearchTermButton.uiNode(fileId, searchTerm).value).getOrElse(""),
      fileIdProperty, mutableSearchTerm.valueProperty))

  def shutdown(): Unit = {
    icon.iconColorProperty().unbind()
    idProperty().unbind()
    setOnAction(null)
    visibleProperty().unbind()
  }
  


