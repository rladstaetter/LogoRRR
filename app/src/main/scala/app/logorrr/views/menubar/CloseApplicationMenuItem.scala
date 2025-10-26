package app.logorrr.views.menubar

import app.logorrr.util.JfxUtils
import app.logorrr.views.a11y.uinodes.LogoRRRMenu
import javafx.scene.control.MenuItem
import javafx.stage.Stage

object CloseApplicationMenuItem {

  def apply(menuItem: MenuItem, stage: Stage, isUnderTest: Boolean): MenuItem = {
    menuItem.setId(LogoRRRMenu.CloseApplication.value)
    Option(menuItem.getOnAction) match {
      case Some(value) =>
        menuItem.setOnAction(e => {
          JfxUtils.closeStage(stage)
          if (!isUnderTest) {
            value.handle(e) // 'natively' quit application on macosx
          }
        })
      case None =>
        menuItem.setOnAction(_ => JfxUtils.closeStage(stage))
    }
    menuItem
  }

}
