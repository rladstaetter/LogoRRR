package app.logorrr.views

import app.logorrr.conf.LogoRRRGlobals
import app.logorrr.views.block.{HasBlockSizeProperty, RectButton}
import app.logorrr.views.search.SearchToolBar
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.control.{Control, ToolBar}
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

/**
 * Container to horizontally align search, filters and settings
 */
class OpsBorderPane(pathAsString: String
                    , searchToolBar: SearchToolBar
                    , filtersToolBar: FiltersToolBar
                    , settingsToolBar: SettingsToolBar)
  extends BorderPane
    with HasBlockSizeProperty {

  def setFontSize(fontSize: Int): Unit =
    LogoRRRGlobals.getLogFileSettings(pathAsString).setFontSize(fontSize)

  def getFontSize(): Int = LogoRRRGlobals.getLogFileSettings(pathAsString).getFontSize()

  val stepSize = 4

  // bound to global var
  override val blockSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty()

  val fontSizeProperty: SimpleIntegerProperty = new SimpleIntegerProperty()

  val items: Seq[Control] = {
    val smallerBtn =
      new RectButton(2 * stepSize, 2 * stepSize, Color.GRAY,
        _ => {
          if (getBlockSize() - stepSize > 0) {
            setBlockSize(getBlockSize() - stepSize)
            setFontSize(getFontSize() - stepSize)
          }
        })
    val biggerBtn =
      new RectButton(3 * stepSize, 3 * stepSize, Color.GRAY
        , _ => {
          if (getBlockSize() + stepSize < 10 * stepSize) {
            setBlockSize(getBlockSize() + stepSize)
            setFontSize(getFontSize() + stepSize)
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

