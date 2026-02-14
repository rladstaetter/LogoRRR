package app.logorrr.views.search.stg

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.model.BoundId
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.beans.property.ObjectPropertyBase
import javafx.scene.control.{Button, Tooltip}
import javafx.stage.Window
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


object OpenStgEditorButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[OpenStgEditorButton])


class OpenStgEditorButton(mutLogFileSettings: MutLogFileSettings
                          , fileId: FileId
                          , activeSearchTerms: () => Seq[SearchTerm])
  extends Button with BoundId(OpenStgEditorButton.uiNode(_).value):

  setGraphic(new FontIcon(FontAwesomeRegular.EDIT))
  setTooltip(new Tooltip("edit search term groups"))

  lazy val editor = new SearchTermGroupEditor(mutLogFileSettings, fileId, activeSearchTerms())

  setOnAction:
    _ => editor.showAndWait()

  def init(window: Window, fileIdProperty: ObjectPropertyBase[FileId]): Unit =
    bindIdProperty(fileIdProperty)
    editor.init(window, fileIdProperty)

  def shutdown(): Unit =
    unbindIdProperty()
    editor.shutdown()