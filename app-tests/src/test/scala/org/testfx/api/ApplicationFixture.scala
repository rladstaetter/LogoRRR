package org.testfx.api

import javafx.stage.Stage

trait ApplicationFixture {
  @throws[Exception]
  def init(): Unit

  @throws[Exception]
  def start(stage: Stage): Unit

  @throws[Exception]
  def stop(): Unit
}
