package app.logorrr.views.ops

import app.logorrr.views.search.{SearchTermToolBar, OpsToolBar}
import javafx.geometry.Pos
import javafx.scene.layout.HBox

class StdOpsToolBar(opsToolBar: OpsToolBar, filtersToolBar: SearchTermToolBar) extends HBox {

  HBox.setHgrow(filtersToolBar, javafx.scene.layout.Priority.ALWAYS)
  setAlignment(Pos.CENTER_LEFT)
  getChildren.addAll(opsToolBar)

}
