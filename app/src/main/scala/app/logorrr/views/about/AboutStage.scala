package app.logorrr.views.about

import app.logorrr.conf.AppInfo
import javafx.scene.Scene
import javafx.stage.{Modality, Stage, Window}

class AboutStage(owner: Window) extends Stage:
  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle(s"About ${AppInfo.fullAppNameWithVersion}")
  setOnCloseRequest(_ => this.close())
  private val width = 450
  private val height = 300
  private val scene = new Scene(new AboutDialogBorderPane(this), width, height)
  setScene(scene)

  // to fix display bug when used on linux / snap
  setOnShown(_ => {
    this.setWidth(width)
    this.setHeight(height)
    // Optional: Center the window on the screen after resizing
    this.centerOnScreen()
  })


