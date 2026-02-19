package app.logorrr.views.settings

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.search.stg.{DeleteStgButton, SearchTermLabel}
import javafx.geometry.Insets
import javafx.scene.control.{ListCell, RadioButton, ToggleGroup, ToolBar}
import javafx.scene.layout.{HBox, Pane, Priority}

import scala.jdk.CollectionConverters.*


object SettingsStgListViewCell:

  val selectedStyle =
    """
      |-fx-font-weight: bold;
      |-fx-border-color: #cccccc;
      |-fx-border-width: 1 1 1 1;
      |-fx-border-style: dashed;
      |""".stripMargin

  // setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-style: dashed;")

  val unselectedStyle =
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

  private val radioButton = new RadioButton():
    setPadding(new Insets(0, 10, 0, 10))

  private val deleteButton = new DeleteStgButton(SettingsEditor.SettingsStgListViewDelete)
  private val toolBar = new ToolBar()
  private val spacer = new Pane()

  // Setup static layout properties
  radioButton.setToggleGroup(toggleGroup)
  HBox.setHgrow(spacer, Priority.ALWAYS)

  private def selectThisItem(item: MutSearchTermGroup): Unit =
    if !item.isSelected then
      LogoRRRGlobals.setDefaultSearchTermGroup(item)
      getListView.refresh()

  toolBar.setOnMouseClicked(event => {
    if event.getButton == javafx.scene.input.MouseButton.PRIMARY then
      Option(getItem).foreach(selectThisItem)
  })


  override def updateItem(item: MutSearchTermGroup, empty: Boolean): Unit =
    super.updateItem(item, empty)

    if empty || item == null then
      setText(null)
      setGraphic(null)
      setStyle("")
    else
      radioButton.setSelected(item.isSelected)

      // 1. Handle Visual Styling
      val currentStyle = if item.isSelected then SettingsStgListViewCell.selectedStyle else SettingsStgListViewCell.unselectedStyle
      setStyle(currentStyle)
      toolBar.setStyle(currentStyle)

      // 2. Selection Logic
      radioButton.setOnAction(_ => {
        LogoRRRGlobals.setDefaultSearchTermGroup(item)
        getListView.refresh()
      })

      // 3. Delete Logic
      deleteButton.setOnAction(_ => {
        LogoRRRGlobals.remove(item)
        getListView.getItems.remove(item)
      })

      // 4. Content Logic: check if list is empty
      val vis: Seq[SearchTermLabel] = item.termsProperty.asScala.map(t => SearchTermLabel(t)).toSeq

      toolBar.getItems.clear()
      toolBar.getItems.add(radioButton)
      toolBar.getItems.addAll(vis *)
      deleteButton.setDisable(false)

      // 5. Assemble remaining items
      toolBar.getItems.addAll(spacer, deleteButton)
      setGraphic(toolBar)