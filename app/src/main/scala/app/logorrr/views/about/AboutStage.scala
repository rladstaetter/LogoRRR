package app.logorrr.views.about

import app.logorrr.meta.AppInfo
import javafx.scene.Scene
import javafx.stage.{Modality, Stage, Window}

class AboutStage(owner: Window) extends Stage {
  initOwner(owner)
  initModality(Modality.WINDOW_MODAL)
  setTitle(s"About ${AppInfo.fullAppNameWithVersion}")
  setOnCloseRequest(_ => this.close())
  setScene(new Scene(new AboutDialogBorderPane(this), 440, 250))
}


