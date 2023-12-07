package app.logorrr.views.ops

import app.logorrr.views.search.{FiltersToolBar, OpsToolBar}
import javafx.geometry.Pos
import javafx.scene.layout.HBox


/**
 * Container to horizontally align search, filters and settings
 */
class OpsRegion(opsToolBar: OpsToolBar, filtersToolBar: FiltersToolBar) extends HBox {

  HBox.setHgrow(filtersToolBar, javafx.scene.layout.Priority.ALWAYS)
  setAlignment(Pos.CENTER_LEFT)
  opsToolBar.setMaxHeight(Double.PositiveInfinity)
  filtersToolBar.setMaxHeight(Double.PositiveInfinity)
  getChildren.addAll(opsToolBar, filtersToolBar)

}

