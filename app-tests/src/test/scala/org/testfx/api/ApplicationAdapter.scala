package org.testfx.api

import javafx.application.Application
import javafx.stage.Stage

class ApplicationAdapter(val applicationFixture: ApplicationFixture) extends Application {

  @throws[Exception]
  override def init(): Unit = {
    applicationFixture.init()
  }

  @throws[Exception]
  override def start(primaryStage: Stage): Unit = {
    applicationFixture.start(primaryStage)
  }

  @throws[Exception]
  override def stop(): Unit = {
    applicationFixture.stop()
  }

  override def hashCode: Int = applicationFixture.hashCode

  override def equals(obj: Any): Boolean = applicationFixture == obj.asInstanceOf[ApplicationAdapter].applicationFixture

}
