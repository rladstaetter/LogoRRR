package app.logorrr.views.search.stg

import app.logorrr.conf.{FileId, SearchTerm}
import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon


object OpenStgEditorButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[OpenStgEditorButton])


class OpenStgEditorButton(mutLogFileSettings: MutLogFileSettings
                               , fileId: FileId
                               , activeSearchTerms: () => Seq[SearchTerm]) extends Button:
  setId(OpenStgEditorButton.uiNode(mutLogFileSettings.getFileId).value)
  setGraphic(new FontIcon(FontAwesomeRegular.EDIT))
  setTooltip(new Tooltip("edit search term groups"))
  setOnAction(_ => new SearchTermGroupEditor(this.getScene.getWindow, mutLogFileSettings, fileId, activeSearchTerms()).showAndWait())

