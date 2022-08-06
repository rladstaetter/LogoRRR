package app.logorrr.views.search

import javafx.scene.input.{KeyCode, KeyEvent}
/*
case class SearchOp(addFilterFn: Filter => Unit) {
  val searchTextField = new SearchTextField
  val colorPicker = new SearchColorPicker()

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
*/