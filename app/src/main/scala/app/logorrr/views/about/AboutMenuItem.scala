package app.logorrr.views.about

import app.logorrr.views.a11y.UiNodes
import javafx.scene.control.MenuItem
import javafx.stage.Stage

object AboutMenuItem {

  def apply(menuItem: MenuItem, stage: Stage): MenuItem = {
    menuItem.setId(UiNodes.HelpMenu.About.value)
    menuItem.setOnAction(_ => new AboutDialogStage(stage).showAndWait())
    menuItem
  }

}
