package app.logorrr.views

import app.logorrr.views.block.{HasBlockSizeProperty, RectButton}
import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.geometry.Pos
import javafx.scene.control.{Control, ToolBar}
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

/**
 * Container to horizontally align search, filters and settings
 */
class OpsBorderPane(searchToolBar: SearchToolBar
                    , filtersToolBar: FiltersToolBar
                    , settingsToolBar: SettingsToolBar)
  extends BorderPane
    with HasBlockSizeProperty {

  val stepSize = 4

  override val blockSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty(stepSize)
  val items: Seq[Control] = {
    val smallerBtn =
      new RectButton(2 * stepSize, 2 * stepSize, Color.GRAY,
        _ => {
          if (getBlocksize() - stepSize > 0) {
            setBlockSize(getBlocksize() - stepSize)
          }
        })
    val biggerBtn =
      new RectButton(3 * stepSize, 3 * stepSize, Color.GRAY
        , _ => {
          if (getBlocksize() + stepSize < 10 * stepSize) {
            setBlockSize(getBlocksize() + stepSize)
          }
        })

    Seq(smallerBtn, biggerBtn)
  }

  private val box = new ToolBar(items: _*)
  box.setMaxHeight(Double.PositiveInfinity)
  setLeft(box)
  BorderPane.setAlignment(box, Pos.CENTER)

  setCenter(filtersToolBar)
  BorderPane.setAlignment(searchToolBar, Pos.CENTER_LEFT)

  setRight(searchToolBar)
  BorderPane.setAlignment(filtersToolBar, Pos.CENTER_LEFT)


}

