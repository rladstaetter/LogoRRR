package app.logorrr.views.settings

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.search.stg.{DeleteStgButton, SearchTermLabel}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.{HBox, Pane, Priority}

import java.lang


object SettingsStgListViewCell:

  val selectedStyle: String =
    """
      |-fx-font-weight: bold;
      |-fx-border-color: #cccccc;
      |-fx-border-width: 1 1 1 1;
      |-fx-border-style: dashed;
      |""".stripMargin

  val unselectedStyle: String =
    """
      |-fx-background-color: transparent;
      |-fx-font-weight: normal;
      |-fx-border-width: 1 1 1 1;
      |""".stripMargin

/**
 * A custom ListCell for managing Search Term Groups.
 *
 * Features:
 *
 * - RadioButton on the left to select the "default" group.
 * - SearchTermLabels in the middle.
 * - Delete button pushed to the far right via a spacer.
 * - Visual highlighting for the selected row.
 *
 */
class SettingsStgListViewCell(toggleGroup: ToggleGroup) extends ListCell[MutSearchTermGroup]:

  getStylesheets.add(getClass.getResource("/app/logorrr/css/SettingsStgListViewCell.css").toExternalForm)

  private val radioButton = new RadioButton():
    setPadding(new Insets(0, 10, 0, 10))

  private val deleteButton = new DeleteStgButton(SettingsEditor.SettingsStgListViewDelete)
  private val toolBar = new ToolBar()
  private val spacer = new Pane()

  toolBar.setOnMouseClicked(event => {
    if event.getButton == javafx.scene.input.MouseButton.PRIMARY then
      Option(getItem).foreach(selectThisItem)
  })

  radioButton.setToggleGroup(toggleGroup)
  HBox.setHgrow(spacer, Priority.ALWAYS)

  private def selectThisItem(item: MutSearchTermGroup): Unit =
    if !item.isSelected then
      LogoRRRGlobals.setDefaultSearchTermGroup(item)
      getListView.refresh()

  override def updateItem(group: MutSearchTermGroup, empty: Boolean): Unit =
    super.updateItem(group, empty)


    val textField = new TextField():
      setPrefWidth(200)
      setMaxWidth(200)
      getStyleClass.add("settings-text-field")
    
    // the most reliable way to clear a bidirectional bind
    // without tracking the old item is to check the current item (?)
    Option(getItem).foreach(oldGroup => textField.textProperty().unbindBidirectional(oldGroup.nameProperty))

    if empty || group == null then
      setText(null)
      setGraphic(null)
      setStyle("")
    else
      textField.textProperty().bindBidirectional(group.nameProperty)
      radioButton.setSelected(group.isSelected)

      val currentStyle = if group.isSelected then SettingsStgListViewCell.selectedStyle else SettingsStgListViewCell.unselectedStyle
      setStyle(currentStyle)
      toolBar.setStyle(currentStyle)

      radioButton.setOnAction(_ => {
        LogoRRRGlobals.setDefaultSearchTermGroup(group)
        getListView.refresh()
      })

      // 3. Delete Logic
      deleteButton.setOnAction(_ => {
        LogoRRRGlobals.remove(group)
        getListView.getItems.remove(group)
      })

      // don't edit / delete default element
      if MutSearchTermGroup.isDefaultElement(group) then {
        deleteButton.setDisable(true)
        textField.setDisable(true)
      } else {
        deleteButton.setDisable(false)
        textField.setDisable(false)
      }

      // 4. Update the ToolBar
      toolBar.getItems.clear()
      toolBar.getItems.addAll(radioButton, textField)
      toolBar.getItems.addAll(group.termsProperty.stream().map(t => SearchTermLabel(t)).toList)
      toolBar.getItems.addAll(spacer, deleteButton)

      setGraphic(toolBar)