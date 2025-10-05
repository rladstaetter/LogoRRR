package app.logorrr.views.search.stg

import app.logorrr.clv.color.ColorUtil
import app.logorrr.views.search.st.SimpleSearchTermVis
import javafx.scene.control.ToggleButton

class SimpleToggleButton(sstv: SimpleSearchTermVis) extends ToggleButton {
  setPrefWidth(100)
  setGraphic(sstv)
  setStyle(ColorUtil.mkCssBackgroundString(sstv.colorProperty.get()))
  setSelected(true)
}
