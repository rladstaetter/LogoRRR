package app.logorrr.views.search

import app.logorrr.conf.FileId
import javafx.beans.property.{ObjectPropertyBase, SimpleListProperty}
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.input.{KeyCode, KeyEvent}

class SearchRegion:

  /** textfield to enter search queries */
  val searchTextField: SearchTextField = new SearchTextField

  /** control which enables selecting color for a search term */
  private val colorPicker = new SearchColorPicker

  private val searchButton = new SearchButton

  def execSearchOnHitEnter(event: KeyEvent): Unit =
    if event.getCode == KeyCode.ENTER then
      searchButton.fire()


  val items: Seq[Control] = Seq[Control](searchTextField, colorPicker, searchButton)

  def init(fileIdProperty: ObjectPropertyBase[FileId]
           , mutSearchTerms: ObservableList[MutableSearchTerm]): Unit =
    searchTextField.init(fileIdProperty)
    colorPicker.init(fileIdProperty)
    searchButton.init(fileIdProperty, searchTextField.textProperty, colorPicker.valueProperty(), mutSearchTerms)
    searchTextField.setOnKeyPressed(execSearchOnHitEnter)
    colorPicker.setOnKeyPressed(execSearchOnHitEnter)

  def shutdown(searchTerms: ObservableList[MutableSearchTerm]): Unit =
    searchTextField.shutdown()
    colorPicker.shutdown()
    searchButton.shutdown(searchTextField.textProperty, colorPicker.valueProperty(), searchTerms)


