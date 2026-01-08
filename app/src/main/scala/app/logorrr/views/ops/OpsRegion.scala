package app.logorrr.views.ops

import app.logorrr.views.search.OpsToolBar
import app.logorrr.views.search.st.SearchTermToolBar
import javafx.scene.layout.VBox


class OpsRegion(opsToolBar: OpsToolBar
                , searchTermToolBar: SearchTermToolBar) extends VBox:
  getChildren.addAll(opsToolBar, searchTermToolBar)





