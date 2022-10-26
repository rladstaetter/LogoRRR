package app.logorrr.views.search

import app.logorrr.util.{ColorUtil, OsUtil}
import javafx.beans.binding.StringBinding
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.HBox
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

import scala.jdk.CollectionConverters._


object SearchToolBar {

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
class SearchToolBar(addFilterFn: Filter => Unit) extends ToolBar {

  //setStyle(SearchToolBar.BackgroundSelectedStyle)
  setStyle("""-fx-padding: 0px 0px 0px 4px;""")

  /** expose for key accelerator */
  val searchTextField = new SearchTextField
  private val colorPicker = new SearchColorPicker()

  /** expose for key accelerator */
  val regexToggleButton = new SearchActivateRegexToggleButton()

  searchTextField.promptTextProperty().bind(new StringBinding {
    bind(regexToggleButton.selectedProperty())

    override def computeValue(): String =
      if (regexToggleButton.isSelected) {
        "<regex search string>"
      } else {
        "<search string>"
      }
  })

  class SearchButton extends CustomSearchButton {

    setOnAction(_ => {
      if (searchTextField.getText.nonEmpty) {
        val filter =
          if (regexToggleButton.isSelected) {
            new RegexFilter(searchTextField.getText, colorPicker.getValue.toString)
          } else {
            new Filter(searchTextField.getText, colorPicker.getValue.toString)
          }
        colorPicker.setValue(ColorUtil.randColor)
        searchTextField.clear()
        addFilterFn(filter)
      }
    })

  }

  private val searchButton = new SearchButton()

  // if 'ENTER' is pressed when focus is in searchField, execute a search right away.
  // I would prefer to instantiate an accelerator here as well, but there is a NPE if we do it in the constructor.
  // Because of that LogoRRRAccelerators class exists. On the other hand it is ok to have a central place to define
  // global shortcuts.
  searchTextField.setOnKeyPressed(execSearchOnHitEnter)
  regexToggleButton.setOnKeyPressed(execSearchOnHitEnter)
  colorPicker.setOnKeyPressed(execSearchOnHitEnter)

  def execSearchOnHitEnter(event: KeyEvent): Unit = {
    if (event.getCode == KeyCode.ENTER) {
      searchButton.fire()
    }
  }
  val width = 356

  setMaxWidth(width)
  setMinWidth(width)

  getItems.addAll(Seq(searchTextField, regexToggleButton, colorPicker, searchButton).asJava)

}
