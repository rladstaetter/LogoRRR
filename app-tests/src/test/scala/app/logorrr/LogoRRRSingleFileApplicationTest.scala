package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.MockOpenFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.views.LogoRRRNode
import javafx.scene.Node
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.stage.Stage
import org.junit.jupiter.api.AfterEach
import org.testfx.api.{FxRobotInterface, FxToolkit}
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import java.nio.file.Path
import java.util.concurrent.{Callable, TimeUnit}



/**
 * Extend this class which setups environment for LogoRRR and provides various helper methods
 */
class LogoRRRSingleFileApplicationTest(val path: Path) extends TestFxBaseApplicationTest {


  @throws[Exception]
  override def start(stage: Stage): Unit = {
    val services = LogoRRRServices(new MockHostServices
      , new MockOpenFileService(Option(path))
      , isUnderTest = true)
    LogoRRRApp.start(stage, Settings.Default, services)
    stage.toFront()
  }


  @AfterEach
  @throws[Exception]
  def tearDown(): Unit = {
    // exit application
    push(KeyCode.COMMAND, KeyCode.Q)
    FxToolkit.hideStage()
    release(Array[KeyCode](): _*)
    release(Array[MouseButton](): _*)
  }

  def clickOn(node: LogoRRRNode): FxRobotInterface = clickOn(node.ref)

  def waitForVisibility(id: LogoRRRNode): Unit = {
    WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = lookup(id.ref).`match`(NodeQueryUtils.isVisible).tryQuery.isPresent
    })
  }

  def waitForPredicate[A <: Node](id: LogoRRRNode, clazz: Class[A], predicate: A => Boolean): Unit = {
    WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = {
        predicate(lookup(id.ref).queryAs(clazz))
      }
    })
  }

}
