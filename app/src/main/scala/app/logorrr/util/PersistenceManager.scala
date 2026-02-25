package app.logorrr.util

import app.logorrr.conf.{FileId, LogoRRRGlobals}
import javafx.animation.PauseTransition
import javafx.beans.property.Property
import javafx.collections.FXCollections
import javafx.concurrent.Task
import javafx.util.{Duration, Subscription}
import net.ladstatt.util.log.TinyLog

import java.util


/**
 * Persist logorrr.json periodically or on change of given observables, debouncing bursts of changes using
 * JavaFX native tools
 * */
class PersistenceManager extends TinyLog {

  private val subscriptions = FXCollections.observableArrayList[Subscription]()

  val map: java.util.Map[FileId, Set[Subscription]] = new util.HashMap[FileId, Set[Subscription]]()

  // provide 2 second cooldown not to persist settings all the time
  private val scheduler = new PauseTransition(Duration.millis(2000))
  scheduler.setOnFinished(_ => persist())

  def init(obs: Set[Property[?]]): Unit = {
    this.subscriptions.addAll(subscribe(obs).toSeq *)
  }

  def init(fileId: FileId, obs: Set[Property[?]]): Unit = {
    println(s"Adding props for : ${fileId.fileName}")
    this.map.put(fileId, subscribe(obs))
  }

  private def subscribe(obs: Set[Property[?]]): Set[Subscription] = {
    obs.map(o =>
      println("Subscribing: " + o)
      o.subscribe(() => {
        logTrace(s"Property changed ${o.getValue} - resetting timer")
        scheduler.playFromStart()
      }))
  }

  def shutdown(fileId: FileId): Unit = Option(map.get(fileId)).foreach(s => s.foreach(_.unsubscribe()))

  def shutdown(): Unit = {
    this.subscriptions.forEach(_.unsubscribe())
    map.keySet.forEach(k => shutdown(k))
  }


  private def persist(): Unit = {
    // Create a Task for the background work
    val persistTask = new Task[Unit] {
      override def call(): Unit = {
        // This runs on a background thread
        LogoRRRGlobals.persist(LogoRRRGlobals.getSettings)
      }
    }

    // Handle errors or success the "JavaFX way"
    persistTask.setOnFailed(e => {
      val exception = persistTask.getException
      logError(s"Persistence failed: ${exception.getMessage}")
    })

    persistTask.setOnSucceeded(_ => {
      logTrace("Persistence completed successfully.")
    })

    // Execute the task on a background thread
    val thread = new Thread(persistTask)
    thread.start()
  }
}
