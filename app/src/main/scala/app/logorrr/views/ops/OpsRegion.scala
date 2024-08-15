package app.logorrr.views.ops

import app.logorrr.views.ops.time.TimeOpsToolBar
import app.logorrr.views.search.{FiltersToolBar, OpsToolBar}
import javafx.scene.layout.VBox


/**
 * Container to horizontally align search, filters and settings
 */
class OpsRegion(opsToolBar: OpsToolBar
                , filtersToolBar: FiltersToolBar
                , timeOpsToolBar: TimeOpsToolBar) extends VBox {
  getChildren.addAll(timeOpsToolBar, new StdOpsToolBar(opsToolBar, filtersToolBar))

}




