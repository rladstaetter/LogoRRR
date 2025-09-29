package app.logorrr.views.search

import javafx.scene.control.{Button, Tooltip}
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular
import org.kordamp.ikonli.javafx.FontIcon

class SearchTermGroupSaveButton(addFn: String => Unit) extends Button {
  setGraphic(new FontIcon(FontAwesomeRegular.EDIT))
  setTooltip(new Tooltip("edit search term groups"))
  setOnAction(_ => new SearchTermGroupEditDialog(addFn).showAndWait())
}
