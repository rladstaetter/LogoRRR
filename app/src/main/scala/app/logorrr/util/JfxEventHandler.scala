package app.logorrr.util

import javafx.event.{Event, EventHandler}

object JfxEventHandler {

  def apply[E <: Event](f: E => Unit): EventHandler[E] = (e: E) => f(e)

}
