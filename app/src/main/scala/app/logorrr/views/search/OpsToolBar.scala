package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.util.OsUtil
import app.logorrr.views.autoscroll.AutoScrollCheckBox
import app.logorrr.views.block.HasBlockSizeProperty
import app.logorrr.views.ops.{ClearLogButton, CopyLogButton, DecreaseBlockSizeButton, IncreaseBlockSizeButton}
import app.logorrr.views.text.toolbaractions.{DecreaseTextSizeButton, IncreaseTextSizeButton}
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}


object OpsToolBar {

  /** increment/decrement block size */
  val blockSizeStep = 2

  /** increment / decrement font size */
  val fontSizeStep = 1

}

/**
 * Groups search ui widgets together.
 *
 * @param addFilterFn filter function which results from user interaction with SearchToolbar
 */
class OpsToolBar(fileId: FileId
                 , addFilterFn: Filter => Unit
                 , logEntries: ObservableList[LogEntry]
                 , filteredList: FilteredList[LogEntry]
                 , val blockSizeProperty: SimpleIntegerProperty)
  extends ToolBar
    with HasBlockSizeProperty {

  //setStyle(SearchToolBar.BackgroundSelectedStyle)
  setStyle("""-fx-padding: 0px 0px 0px 4px;""")
  val w = 380
  val macWidth: Int = w
  val winWidth: Int = w + 2
  val linuxWidth: Int = w + 2
  val width: Int = OsUtil.osFun(winWidth, macWidth, linuxWidth) // different layouts (may be dependent on font size renderings?)
  setMinWidth(width)

  /** control which enables selecting color for a search tag */
  private val colorPicker = new SearchColorPicker()

  /** toggles search behavior from case sensitive search to a regex search */
  val regexToggleButton = new SearchActivateRegexToggleButton()

  /** textfield to enter search queries */
  val searchTextField = new SearchTextField(regexToggleButton)

  private val searchButton = new SearchButton(searchTextField, regexToggleButton, colorPicker, addFilterFn)

  val autoScrollCheckBox = new AutoScrollCheckBox(fileId)

  val clearLogButton = new ClearLogButton(logEntries)

  val copySelectionButton = new CopyLogButton(filteredList)

//  val firstNEntries: ObservableList[LogEntry] = TimerSettingsLogView.mkEntriesToShow(logEntries)

//  val timerButton = new TimerButton(fileId, firstNEntries)

  def execSearchOnHitEnter(event: KeyEvent): Unit = {
    if (event.getCode == KeyCode.ENTER) {
      searchButton.fire()
    }
  }

  searchTextField.setOnKeyPressed(execSearchOnHitEnter)
  regexToggleButton.setOnKeyPressed(execSearchOnHitEnter)
  colorPicker.setOnKeyPressed(execSearchOnHitEnter)

  val searchItems: Seq[Control] = Seq[Control](searchTextField, regexToggleButton, colorPicker, searchButton)

  val sizeItems: Seq[Control] = {
    val decreaseBlockSizeButton = new DecreaseBlockSizeButton(blockSizeProperty)
    val increaseBlockSizeButton = new IncreaseBlockSizeButton(blockSizeProperty)
    val decreaseTextSizeButton = new DecreaseTextSizeButton(fileId)
    val increaseTextSizeButton = new IncreaseTextSizeButton(fileId)
    Seq(decreaseBlockSizeButton, increaseBlockSizeButton, decreaseTextSizeButton, increaseTextSizeButton)
  }

  val otherItems: Seq[Node] = {
    Seq(autoScrollCheckBox, clearLogButton, copySelectionButton)
  }

  getItems.addAll(searchItems ++ sizeItems ++ otherItems: _*)

}
