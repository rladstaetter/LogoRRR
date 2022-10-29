package app.logorrr.views.ops

import app.logorrr.views.search.{FiltersToolBar, OpsToolBar}
import javafx.geometry.Pos
import javafx.scene.layout.HBox


/**
 * Container to horizontally align search, filters and settings
 */
class OpsRegion(val opsToolBar: OpsToolBar
                , filtersToolBar: FiltersToolBar
                , settingsToolBar: SettingsOps)
  extends HBox {

  opsToolBar.setMaxHeight(Double.PositiveInfinity)
  filtersToolBar.setMaxHeight(Double.PositiveInfinity)
  setAlignment(Pos.CENTER_LEFT)
  getChildren.addAll(opsToolBar, filtersToolBar)

}

