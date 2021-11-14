package app.util

import javafx.application.Platform
import javafx.event.{Event, EventHandler}
import javafx.scene.image.Image
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage}

import java.nio.file.Path
import scala.util.{Failure, Success, Try}





object JfxEventHandler {

  def apply[E <: Event](f: E => Unit): EventHandler[E] = (e: E) => f(e)

}
