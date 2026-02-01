package app.logorrr.views.search

import app.logorrr.conf.FileId
import javafx.scene.control.Control
import javafx.scene.input.{KeyCode, KeyEvent}

class SearchRegion(fileId: FileId, addFilterFn: MutableSearchTerm => Unit):

  /** textfield to enter search queries */
  val searchTextField = new SearchTextField(fileId)

  /** control which enables selecting color for a search term */
  private val colorPicker = new SearchColorPicker(fileId)

  private val searchButton = new SearchButton(fileId, searchTextField, colorPicker, addFilterFn)

  def execSearchOnHitEnter(event: KeyEvent): Unit =
    if event.getCode == KeyCode.ENTER then
      searchButton.fire()

  searchTextField.setOnKeyPressed(execSearchOnHitEnter)
  colorPicker.setOnKeyPressed(execSearchOnHitEnter)

  val items: Seq[Control] = Seq[Control](searchTextField, colorPicker, searchButton)
