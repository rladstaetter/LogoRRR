package app.logorrr.views.search

import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.MutFilter
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
import net.ladstatt.util.os.OsUtil


object OpsToolBar {

  /** increment / decrement font size */
  val fontSizeStep: Int = 1

}

/**
 * Groups search ui widgets together.
 *
 * @param addFilterFn filter function which results from user interaction with SearchToolbar
 */
class OpsToolBar(fileId: FileId
                 , addFilterFn: MutFilter[_] => Unit
                 , logEntries: ObservableList[LogEntry]
                 , filteredList: FilteredList[LogEntry]
                 , val blockSizeProperty: SimpleIntegerProperty)
  extends ToolBar with HasBlockSizeProperty {

  // TODO fix this; not really elegant
  setStyle("""-fx-padding: 0px 0px 0px 4px;""")
  val w = 380
  private val macWidth: Int = w
  private val winWidth: Int = w + 2
  private val linuxWidth: Int = w + 2
  val width: Int = OsUtil.osFun(winWidth, macWidth, linuxWidth) // different layouts (may be dependent on font size renderings?)
  setMinWidth(width)

  /** control which enables selecting color for a search tag */
  private val colorPicker = new SearchColorPicker(fileId)

  /** toggles search behavior from case sensitive search to a regex search */
  val regexToggleButton = new SearchActivateRegexToggleButton(fileId)

  /** textfield to enter search queries */
  val searchTextField = new SearchTextField(fileId, regexToggleButton)

  private val searchButton = new SearchButton(fileId, searchTextField, regexToggleButton, colorPicker, addFilterFn)

  private val autoScrollCheckBox = new AutoScrollCheckBox(fileId)

  private val clearLogButton = new ClearLogButton(fileId, logEntries)

  private val copySelectionButton = new CopyLogButton(fileId, filteredList)


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
    Seq(new DecreaseBlockSizeButton(fileId, blockSizeProperty)
      , new IncreaseBlockSizeButton(fileId, blockSizeProperty)
      , new DecreaseTextSizeButton(fileId)
      , new IncreaseTextSizeButton(fileId))
  }

  val otherItems: Seq[Node] = {
    Seq(autoScrollCheckBox, clearLogButton, copySelectionButton)
  }

  getItems.addAll(searchItems ++ sizeItems ++ otherItems: _*)

}
