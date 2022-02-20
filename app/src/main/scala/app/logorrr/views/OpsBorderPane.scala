package app.logorrr.views

import javafx.geometry.Pos
import javafx.scene.layout.BorderPane

/**
 * Container to horizontally align search, filters and settings
 */
class OpsBorderPane(searchToolBar: SearchToolBar
                    , filtersToolBar: FiltersToolBar
                    , settingsToolBar: SettingsToolBar) extends BorderPane {
  setLeft(searchToolBar)
  BorderPane.setAlignment(searchToolBar, Pos.CENTER)

  setCenter(filtersToolBar)
  BorderPane.setAlignment(filtersToolBar, Pos.CENTER_LEFT)

  setRight(settingsToolBar)
  BorderPane.setAlignment(settingsToolBar, Pos.CENTER_RIGHT)

}
