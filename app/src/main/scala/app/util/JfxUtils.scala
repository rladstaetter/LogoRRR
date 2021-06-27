package app.util

import javafx.application.Platform
import javafx.event.Event
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage}

import scala.util.{Failure, Success, Try}


object JfxUtils extends CanLog {

  def execOnUiThread(f: => Unit): Unit = {
    if (!Platform.isFxApplicationThread) {
      Platform.runLater(() => f)
    } else {
      f
    }
  }

  def mkModalWindow(root: Parent
                    , title: String
                    , width: Int
                    , height: Int
                    , exitAction: (Stage, Event) => Try[Unit] = (stage, e) => Success(())): Stage = {
    val stage = new Stage()
    stage.initModality(Modality.APPLICATION_MODAL)
    stage.setTitle(title)
    val scene = new Scene(root, width, height)
    stage.setScene(scene)
    stage.setOnCloseRequest(JfxEventHandler(event => {
      event.consume()
      exitAction(stage, event) match {
        case Failure(e) => logException(e)
        case Success(t) => stage.close()
      }
    }))
    stage
  }
}
