package app.logorrr.testfx

import javafx.application.{Application, HostServices, Preloader}
import javafx.stage.Stage
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.scalacheck.Checkers
import org.testfx.api.{FxRobot, FxToolkit}

abstract class TestFxSpec extends FxRobot with AnyWordSpecLike with Checkers with BeforeAndAfterAll with ApplicationFixture {
  me =>

  override def beforeAll(): Unit = {
    // Initialize JavaFX Toolkit
    javafx.application.Platform.startup(() => {})
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(() => new ApplicationAdapter(me))

  }

  override def afterAll(): Unit = {
    FxToolkit.cleanupAfterTest(me, new ApplicationAdapter(me))
  }

  def launch(appClass: Class[_ <: Application], appArgs: String*): Application = {
    FxToolkit.registerPrimaryStage
    FxToolkit.setupApplication(appClass, appArgs: _*)
  }

  @throws[Exception]
  override def init(): Unit = {}

  @throws[Exception]
  override def start(stage: Stage): Unit = {}

  @throws[Exception]
  override def stop(): Unit = {}

  @deprecated def getHostServices: HostServices = throw new UnsupportedOperationException

  @deprecated def getParameters: Application.Parameters = throw new UnsupportedOperationException

  @deprecated def notifyPreloader(notification: Preloader.PreloaderNotification): Unit = {
    throw new UnsupportedOperationException
  }

}




