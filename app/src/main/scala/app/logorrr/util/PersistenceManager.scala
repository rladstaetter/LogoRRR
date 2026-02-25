package app.logorrr.util

import app.logorrr.conf.FileId
import javafx.animation.PauseTransition
import javafx.beans.property.Property
import javafx.collections.FXCollections
import javafx.util.{Duration, Subscription}
import net.ladstatt.util.log.TinyLog

import java.util
import java.util.function.BiConsumer


/**
 * Persist logorrr.json periodically or on change of given observables, debouncing bursts of changes using
 * JavaFX native tools
 * */
class PersistenceManager extends TinyLog {

  val subscriptions = FXCollections.observableArrayList[Subscription]()

  val map: java.util.Map[FileId, Set[Subscription]] = new util.HashMap[FileId, Set[Subscription]]()

  // 1. The Debouncer (Strong reference keeps this alive)
  private val scheduler = new PauseTransition(Duration.millis(500))
  scheduler.setOnFinished(_ => persist())

  def init(obs: Set[Property[?]]): Unit = {
    this.subscriptions.addAll(subscribe(obs).toSeq *)
  }

  def init(fileId: FileId, obs: Set[Property[?]]): Unit = {
    println(s"Adding props for : ${fileId.fileName}")
    this.map.put(fileId, subscribe(obs))
  }

  private def subscribe(obs: Set[Property[?]]): Set[Subscription] = {
    obs.map(o => o.subscribe(() => {
      logTrace(s"Property changed ${o.getValue} - resetting timer")
      scheduler.playFromStart()
    }))
  }

  def shutdown(fileId: FileId) = Option(map.get(fileId)).foreach(s => s.foreach(_.unsubscribe()))

  def shutdown(): Unit = {
    this.subscriptions.forEach(_.unsubscribe())
    map.keySet.forEach(k => shutdown(k))
  }


  private def persist(): Unit = {
    println("Persisting state...")
    // Perform your heavy operation here (ideally on a background thread)
  }
}
