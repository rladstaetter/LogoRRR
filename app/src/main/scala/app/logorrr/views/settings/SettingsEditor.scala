package app.logorrr.views.settings

import app.logorrr.conf.*
import app.logorrr.conf.mut.{MutSearchTermGroup, MutTimeSettings}
import app.logorrr.model.DateFilterEvent
import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.main.LogoRRRMain
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.stage.{Modality, Stage, Window}


class SettingsEditor(owner: Window
                     , fileId: FileId
                     , main: LogoRRRMain) extends Stage:
  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle("Settings")


  // Layout for existing groups (including list and close button)
  private val searchTermGroupEditor =
    SettingsManageStgEditor(fileId, LogoRRRGlobals.searchTermGroupEntries)

  private val timeSettingsEditor = TimestampSettingsEditor(LogoRRRGlobals.timeSettings)
  private val resetButton = new Button("reset to factory defaults")
  resetButton.setId(SettingsEditor.ResetToDefaultButton.value)
  resetButton.setOnAction(_ => {
    LogoRRRGlobals.clearSearchTermGroups()
    Settings.DefaultSearchTermGroups.map(MutSearchTermGroup.apply).foreach(LogoRRRGlobals.add)
    LogoRRRGlobals.setTimeSettings(MutTimeSettings(TimeSettings.Invalid))
    timeSettingsEditor.updateSettings(MutTimeSettings(TimeSettings.Invalid))
  })

  private val contentLayout = new VBox(10)
  VBox.setVgrow(contentLayout, Priority.ALWAYS)
  contentLayout.setPadding(new Insets(10))

  private val applyAndCloseButton: Button = new ApplyAndCloseSettingsEditorButton(this, main, timeSettingsEditor)

  private val hBox: HBox =
    val filler = JfxUtils.mkHgrowFiller()
    val h = new HBox()
    h.getChildren.addAll(resetButton, filler, applyAndCloseButton)
    h

  contentLayout.getChildren.addAll(searchTermGroupEditor, timeSettingsEditor, hBox)

  // --- Final Setup ---
  setScene(new Scene(contentLayout, 960, 720)) // Adjusted size for list view

  def scrollToLast(): Unit =
    searchTermGroupEditor.scrollToLast()


  setOnCloseRequest(e => {
    searchTermGroupEditor.shutdown()
    timeSettingsEditor.shutdown()
  })


class ApplyAndCloseSettingsEditorButton(stage: Stage
                                        , main: LogoRRRMain
                                        , timeSettingsEditor: TimestampSettingsEditor) extends Button("apply & close") {
  setId(SettingsEditor.CloseButton.value)
  setOnAction(_ =>
    val settings = timeSettingsEditor.getTimeSettings()
    LogoRRRGlobals.setTimeSettings(MutTimeSettings(settings))
    main.fireEvent(DateFilterEvent(settings))
    stage.close())
}