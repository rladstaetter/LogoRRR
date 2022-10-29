package app.logorrr.views.search

import app.logorrr.util.ColorUtil
import javafx.scene.control.{ColorPicker, Tooltip}

class SearchColorPicker extends ColorPicker {
  setValue(ColorUtil.randColor)
  setMaxWidth(46)
  setTooltip(new Tooltip("choose color"))
  // see https://stackoverflow.com/questions/45966844/how-to-change-the-icon-size-of-a-color-picker-in-javafx
  setStyle("""-fx-color-label-visible: false""".stripMargin)

}

