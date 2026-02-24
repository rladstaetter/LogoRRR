package app.logorrr.views.settings

import app.logorrr.conf.mut.MutTimestampSettings
import app.logorrr.conf.{LogoRRRGlobals, TimestampSettings}
import app.logorrr.views.a11y.UiNode
import javafx.beans.binding.{Bindings, ObjectBinding}
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.geometry.Pos
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.layout.{GridPane, Priority, StackPane, VBox}
import javafx.util.converter.NumberStringConverter

object TimestampSettingsEditor:
  val EnableInitalizeButton = UiNode("TimestampSettingsEditor_EnableInitializeButton")
  val PatternTextField = UiNode("TimestampSettingsEditor_PatternTextField")
  val StartColTextField = UiNode("TimestampSettingsEditor_StartColTextField")
  val EndColTextField = UiNode("TimestampSettingsEditor_EndColTextField")

class TimestampSettingsEditor(someTimestampSettings: Option[MutTimestampSettings]) extends StackPane {

  private val converter = new NumberStringConverter()

  val startColProperty = new SimpleIntegerProperty()
  val endColProperty = new SimpleIntegerProperty()
  val patternProperty = new SimpleStringProperty()

  someTimestampSettings.foreach(set)

  def set(timestampSetting : MutTimestampSettings) : Unit =
    startColProperty.set(timestampSetting.getStartCol)
    endColProperty.set(timestampSetting.getEndCol)
    patternProperty.set(timestampSetting.getDateTimePatternCol)

  val timeSettingProperty = new ObjectBinding[TimestampSettings] {

    bind(startColProperty, endColProperty, patternProperty)

    override def computeValue(): TimestampSettings =
      TimestampSettings(startColProperty.get, endColProperty.get, patternProperty.get)
  }

  // --- 1. The Editor UI ---
  private val patternField = new TextField():
    setId(TimestampSettingsEditor.PatternTextField.value)
    setPrefWidth(300)
    textProperty().bindBidirectional(patternProperty)

  private val patternHyperlink = TimestampSettings.dateFormatterHLink.mkHyperLink()

  private val startColField = new TextField():
    setId(TimestampSettingsEditor.StartColTextField.value)
    setPrefWidth(100)
    textProperty().bindBidirectional(startColProperty, converter)

  private val endColField = new TextField():
    setId(TimestampSettingsEditor.EndColTextField.value)
    setPrefWidth(100)
    textProperty.bindBidirectional(endColProperty, converter)

  private val editorView: VBox = new VBox(10) {
    VBox.setVgrow(this, Priority.ALWAYS)

    val title = new Label("Timestamp settings")
    title.setStyle("-fx-font-weight: bold")

    val grid = new GridPane()
    grid.setHgap(10)
    grid.setVgap(10)
    grid.setAlignment(Pos.TOP_LEFT)

    grid.add(new Label("Date Pattern:"), 0, 0)
    grid.add(patternField, 1, 0)
    grid.add(patternHyperlink, 2, 0)
    grid.add(new Label("Start Column:"), 0, 1)
    grid.add(startColField, 1, 1)
    grid.add(new Label("End Column:"), 0, 2)
    grid.add(endColField, 1, 2)

    getChildren.addAll(title, grid)
  }

  // --- 2. The Placeholder UI ---
  private val placeholderView: VBox = new VBox(15) {
    setAlignment(Pos.CENTER)
    // Styling to make it look "empty" or "disabled"
    setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-style: dashed;")

    private val msg = new Label("No timestamp settings available.")
    private val enableBtn = new Button("Enable & Initialize"):
      setId(TimestampSettingsEditor.EnableInitalizeButton.value)

    getChildren.addAll(msg, enableBtn)

    // Action to handle enabling (This would usually call a parent method)
    enableBtn.setOnAction(_ => {
      val settings = MutTimestampSettings(TimestampSettings.Default)
      LogoRRRGlobals.setTimestampSettings(settings)
      updateSettings(Option(settings))
    })
  }

  // --- Initialization ---
  getChildren.addAll(editorView, placeholderView)
  updateSettings(someTimestampSettings)

  def getTimeSettings(): TimestampSettings = timeSettingProperty.get()

  def shutdown() : Unit = {
    startColField.textProperty().unbindBidirectional(startColProperty)
    endColField.textProperty().unbindBidirectional(endColProperty)
    patternField.textProperty().unbindBidirectional(patternProperty)
  }

  /**
   * Logic to switch between states
   */
  def updateSettings(settingsOpt: Option[MutTimestampSettings]): Unit = {
    settingsOpt match {
      case Some(settings) =>
        placeholderView.setVisible(false)
        editorView.setVisible(true)

      case None =>
        editorView.setVisible(false)
        placeholderView.setVisible(true)

        set(MutTimestampSettings(TimestampSettings.Default))
    }
  }
}