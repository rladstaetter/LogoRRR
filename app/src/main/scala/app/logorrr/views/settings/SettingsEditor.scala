package app.logorrr.views.settings

import app.logorrr.conf.{FileId, LogoRRRGlobals, SearchTermGroup, Settings}
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.stage.{Modality, Stage}


class SettingsEditor(owner: Stage, fileId: FileId) extends Stage:
  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle("Settings")

  private val timeSettingsEditor = TimeSettingsEditor(LogoRRRGlobals.getTimestampSettings)

  // Layout for existing groups (including list and close button)
  private val manageExistingSearchTermGroup =
    SettingsManageStgEditor(fileId, LogoRRRGlobals.searchTermGroupEntries)

  private val contentLayout = new VBox(10)
  VBox.setVgrow(contentLayout, Priority.ALWAYS)
  contentLayout.setPadding(new Insets(10))
  val resetButton = new Button("Reset to factory defaults")
  resetButton.setId(SettingsEditor.ResetToDefaultButton.value)
  resetButton.setOnAction(_ => {
    LogoRRRGlobals.clearSearchTermGroups()
    Settings.DefaultSearchTermGroups.foreach(LogoRRRGlobals.putSearchTermGroup)
    LogoRRRGlobals.setTimestampSettings(null)
    timeSettingsEditor.updateSettings(Option(null))
  })

  private val closeButton: Button = new CloseSettingsEditorButton(this)

  private val hBox: HBox =
    val filler = JfxUtils.mkHgrowFiller()
    val h = new HBox()
    h.getChildren.addAll(resetButton, filler, closeButton)
    h

  contentLayout.getChildren.addAll(manageExistingSearchTermGroup, timeSettingsEditor, hBox)

  // --- Final Setup ---
  setScene(new Scene(contentLayout, 960, 720)) // Adjusted size for list view

object CloseSettingsEditorButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[CloseSettingsEditorButton])

class CloseSettingsEditorButton(stage: Stage) extends Button("Close") {
  setId(SettingsEditor.CloseButton.value)
  setOnAction(_ => stage.close())
}