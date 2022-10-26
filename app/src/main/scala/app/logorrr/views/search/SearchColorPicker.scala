package app.logorrr.views.search

import app.logorrr.util.ColorUtil
import javafx.scene.control.{Button, ColorPicker, Tooltip}
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

class SearchColorPicker extends ColorPicker {
  setValue(ColorUtil.randColor)
  setMaxWidth(46)
  setTooltip(new Tooltip("choose color"))
  // see https://stackoverflow.com/questions/45966844/how-to-change-the-icon-size-of-a-color-picker-in-javafx
  setStyle("""-fx-color-label-visible: false""".stripMargin)

}

class CustomSearchButton extends Button {
  setGraphic(new FontIcon(FontAwesomeSolid.SEARCH))
  setTooltip(new Tooltip("search"))
  setMaxWidth(40)

}