package app.logorrr.views.settings

import app.logorrr.conf.*
import app.logorrr.conf.mut.{MutSearchTermGroup, MutTimestampSettings}
import app.logorrr.model.DateFilterEvent
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import app.logorrr.views.logfiletab.LogFilePane
import app.logorrr.views.main.LogoRRRMain
import app.logorrr.views.search.MutableSearchTerm
import app.logorrr.views.search.st.FavoritesComboBox
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.{Button, ChoiceBox, ComboBox}
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.stage.{Modality, Stage}


class SettingsEditor(owner: Stage
                     , fileId: FileId
                     , main: LogoRRRMain) extends Stage:
  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle("Settings")


  // Layout for existing groups (including list and close button)
  private val searchTermGroupEditor =
    SettingsManageStgEditor(fileId, LogoRRRGlobals.searchTermGroupEntries)

  private val timeSettingsEditor = TimestampSettingsEditor(LogoRRRGlobals.getTimestampSettings)

  private val contentLayout = new VBox(10)
  VBox.setVgrow(contentLayout, Priority.ALWAYS)
  contentLayout.setPadding(new Insets(10))
  val resetButton = new Button("reset to factory defaults")
  resetButton.setId(SettingsEditor.ResetToDefaultButton.value)
  resetButton.setOnAction(_ => {
    LogoRRRGlobals.clearSearchTermGroups()
    Settings.DefaultSearchTermGroups.map(MutSearchTermGroup.apply).foreach(LogoRRRGlobals.add)
    LogoRRRGlobals.setTimestampSettings(null)
    timeSettingsEditor.updateSettings(None)
  })

  private val applyAndCloseButton: Button = new ApplyAndCloseSettingsEditorButton(this, main, timeSettingsEditor)

  private val hBox: HBox =
    val filler = JfxUtils.mkHgrowFiller()
    val h = new HBox()
    h.getChildren.addAll(resetButton, filler, applyAndCloseButton)
    h

  contentLayout.getChildren.addAll(searchTermGroupEditor, timeSettingsEditor, hBox)

  // --- Final Setup ---
  setScene(new Scene(contentLayout, 960, 720)) // Adjusted size for list view

  setOnCloseRequest(e => {
    searchTermGroupEditor.shutdown()
    timeSettingsEditor.shutdown()
  })

object ApplyAndCloseSettingsEditorButton extends UiNodeFileIdAware:

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[ApplyAndCloseSettingsEditorButton])

class ApplyAndCloseSettingsEditorButton(stage: Stage
                                        , main: LogoRRRMain
                                        , timeSettingsEditor: TimestampSettingsEditor) extends Button("apply & close") {
  setId(SettingsEditor.CloseButton.value)
  setOnAction(_ =>
    val settings = timeSettingsEditor.getTimeSettings()
    LogoRRRGlobals.setTimestampSettings(MutTimestampSettings(settings))
    main.fireEvent(DateFilterEvent(settings))
    stage.close())
}