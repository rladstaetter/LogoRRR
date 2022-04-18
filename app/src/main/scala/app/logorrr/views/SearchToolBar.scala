package app.logorrr.views

import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.paint.Color

object SearchOp {

  class SearchColorPicker extends ColorPicker {
    setValue(Color.MAGENTA)
  }

  class SearchTextField extends TextField {
    setPrefWidth(200)
    setPromptText("<enter search string>")
  }

  class SearchWidget(searchTextField: SearchTextField
                     , colorPicker: ColorPicker
                     , addFilterFn: Filter => Unit) extends Button("search") {

    setOnAction(_ => {
      val filter = new Filter(searchTextField.getText, colorPicker.getValue.toString)
      searchTextField.clear()
      addFilterFn(filter)
    })

  }

}

case class SearchOp(addFilterFn: Filter => Unit) {
  val searchTextField = new SearchOp.SearchTextField
  val colorPicker = new SearchOp.SearchColorPicker()
  val searchWidget = new SearchOp.SearchWidget(searchTextField, colorPicker, addFilterFn)

  // if 'ENTER' is pressed when focus is in searchField, execute a search right away.
  // I would prefer to instantiate an accelerator here as well, but there is a NPE if we do it in the constructor.
  // Because of that LogoRRRAccelerators class exists. On the other hand it is ok to have a central place to define
  // global shortcuts.
  searchTextField.setOnKeyPressed((event: KeyEvent) => {
    if (event.getCode == KeyCode.ENTER) {
      searchWidget.fire()
    }
  })

  val items = Seq(searchTextField, colorPicker, searchWidget)
}


class SearchToolBar(addFilterFn: Filter => Unit) extends ToolBar {

  val searchOp = SearchOp(addFilterFn)

  /** expose for key accelerator */
  val searchTextField: SearchOp.SearchTextField = searchOp.searchTextField

  getItems.addAll(searchOp.items: _*)
}
