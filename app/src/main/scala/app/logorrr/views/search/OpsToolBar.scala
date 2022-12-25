package app.logorrr.views.search

import app.logorrr.model.LogEntry
import app.logorrr.util.OsUtil
import app.logorrr.views.autoscroll.AutoScrollCheckBox
import app.logorrr.views.block.HasBlockSizeProperty
import app.logorrr.views.ops.{ClearLogButton, DecreaseBlockSizeButton, IncreaseBlockSizeButton}
import app.logorrr.views.settings.timer.TimerSettingsLogView
import app.logorrr.views.text.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}


object OpsToolBar {

  /** increment/decrement block size */
  val blockSizeStep = 4

  /** increment / decrement font size */
  val fontSizeStep = 2

  private val BackgroundSelectedStyle: String =
    """
      |-fx-background-color: CYAN;
      |-fx-border-width: 1px 1px 1px 1px;
      |-fx-border-color: BLUE;
      |-fx-padding: 0px 0px 0px 3px;
      |""".stripMargin

}

/**
 * Groups search ui widgets together.
 *
 * @param addFilterFn filter function which results from user interaction with SearchToolbar
 */
class OpsToolBar(pathAsString: String
                 , addFilterFn: Filter => Unit
                 , logEntries: ObservableList[LogEntry])
  extends ToolBar
    with HasBlockSizeProperty {

  //setStyle(SearchToolBar.BackgroundSelectedStyle)
  setStyle("""-fx-padding: 0px 0px 0px 4px;""")
  val w = 610
  val macWidth = w
  val winWidth = w + 2
  val linuxWidth = w + 2
  val width = OsUtil.osFun(winWidth, macWidth, linuxWidth) // different layouts (may be dependent on font size renderings?)
  setMaxWidth(width)
  setMinWidth(width)

  override val blockSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty()

  /** control which enables selecting color for a search tag */
  private val colorPicker = new SearchColorPicker()

  /** toggles search behavior from case sensitive search to a regex search */
  val regexToggleButton = new SearchActivateRegexToggleButton()

  /** textfield to enter search queries */
  val searchTextField = new SearchTextField(regexToggleButton)

  private val searchButton = new SearchButton(searchTextField, regexToggleButton, colorPicker, addFilterFn)

  val autoScrollCheckBox = new AutoScrollCheckBox(pathAsString)

  val clearLogButton = new ClearLogButton(logEntries)

  val firstNEntries = TimerSettingsLogView.mkEntriesToShow(logEntries)
  val timerButton = new TimerButton(pathAsString, firstNEntries)

  def execSearchOnHitEnter(event: KeyEvent): Unit = {
    if (event.getCode == KeyCode.ENTER) {
      searchButton.fire()
    }
  }

  searchTextField.setOnKeyPressed(execSearchOnHitEnter)
  regexToggleButton.setOnKeyPressed(execSearchOnHitEnter)
  colorPicker.setOnKeyPressed(execSearchOnHitEnter)

  val searchItems = Seq[Control](searchTextField, regexToggleButton, colorPicker, searchButton)

  val sizeItems: Seq[Control] = {
    val decreaseBlockSizeButton = new DecreaseBlockSizeButton(blockSizeProperty)
    val increaseBlockSizeButton = new IncreaseBlockSizeButton(blockSizeProperty)
    val decreaseTextSizeButton = new DecreaseTextSizeButton(pathAsString)
    val increaseTextSizeButton = new IncreaseTextSizeButton(pathAsString)
    Seq(decreaseBlockSizeButton, increaseBlockSizeButton, decreaseTextSizeButton, increaseTextSizeButton)
  }

  val otherItems: Seq[Node] = {
    Seq(autoScrollCheckBox, clearLogButton, timerButton)
  }

  getItems.addAll(searchItems ++ sizeItems ++ otherItems: _*)

}
