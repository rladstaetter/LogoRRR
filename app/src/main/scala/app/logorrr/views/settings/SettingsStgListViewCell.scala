package app.logorrr.views.settings

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.conf.mut.MutSearchTermGroup
import app.logorrr.views.a11y.uinodes.SettingsEditor
import app.logorrr.views.search.stg.{DeleteStgButton, SearchTermLabel}
import javafx.animation.*
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.effect.DropShadow
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.scene.paint.Color
import javafx.util.Duration


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

  def glowsy(): Unit = {
    val node = getGraphic
    if (node != null) {
      // 1. Create the Glow Effect
      val glow = new DropShadow()
      glow.setColor(Color.GOLD) // Or a bright "Comic" yellow
      glow.setRadius(0)
      glow.setSpread(0.6)
      node.setEffect(glow)

      // 2. Scale Effect (The "Pop")
      val scale = new ScaleTransition(Duration.millis(150), node)
      scale.setFromX(1.0)
      scale.setFromY(1.0)
      scale.setToX(1.15) // Slightly larger
      scale.setToY(1.15)
      scale.setAutoReverse(true)
      scale.setCycleCount(2)
      scale.setInterpolator(Interpolator.EASE_OUT)

      // 3. Glow Effect (The "Radiance")
      val glowAnimation = new Timeline(
        new KeyFrame(Duration.ZERO,
          new KeyValue(glow.radiusProperty(), 0.0),
          new KeyValue(glow.colorProperty(), Color.GOLD.deriveColor(0, 1, 1, 0.2))
        ),
        new KeyFrame(Duration.millis(200),
          new KeyValue(glow.radiusProperty(), 30.0),
          new KeyValue(glow.colorProperty(), Color.GOLD)
        ),
        new KeyFrame(Duration.millis(500),
          new KeyValue(glow.radiusProperty(), 0.0),
          new KeyValue(glow.colorProperty(), Color.TRANSPARENT)
        )
      )

      // 4. Combine and Play
      val rewardEffect = new ParallelTransition(scale, glowAnimation)
      rewardEffect.setOnFinished(_ => {
        node.setEffect(null)
        node.setScaleX(1.0)
        node.setScaleY(1.0)
      })

      rewardEffect.play()
    }
  }


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

      deleteButton.setOnAction(_ => {
        LogoRRRGlobals.remove(group)
        getListView.getItems.remove(group)
      })

      if MutSearchTermGroup.isDefaultElement(group) then {
        deleteButton.setDisable(true)
        textField.setDisable(true)
      } else {
        deleteButton.setDisable(false)
        textField.setDisable(false)
      }

      toolBar.getItems.clear()
      toolBar.getItems.addAll(radioButton, textField)
      toolBar.getItems.addAll(group.termsProperty.stream().map(t => SearchTermLabel(t)).toList)
      toolBar.getItems.addAll(spacer, deleteButton)

      setGraphic(toolBar)