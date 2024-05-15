package app.logorrr.steps

import app.logorrr.services.LogoRRRServices
import app.logorrr.LogoRRRApp
import app.logorrr.usecases.TestFxBaseApplicationTest
import javafx.stage.Stage

/**
 * Startup LogoRRR
 */
trait CanStartApplication {
  self: TestFxBaseApplicationTest =>

  def services: LogoRRRServices

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    LogoRRRApp.start(stage, services)
  }
}
