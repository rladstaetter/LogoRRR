package app.logorrr.views.about

import app.logorrr.views.a11y.uinodes.LogoRRRMenu
import javafx.scene.control.MenuItem
import javafx.stage.Stage

object AboutMenuItem:

  def apply(menuItem: MenuItem, stage: Stage): MenuItem =
    menuItem.setId(LogoRRRMenu.About.value)
    menuItem.setOnAction(_ => new AboutStage(stage).showAndWait())
    menuItem

