package app.logorrr.usecases

import app.logorrr.steps.FileMenuActions
import app.logorrr.views.UiNode
import javafx.scene.Node
import javafx.scene.input.{KeyCode, MouseButton}
import org.junit.jupiter.api.AfterEach
import org.testfx.api.{FxRobotInterface, FxToolkit}
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.service.query.NodeQuery
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import java.util.concurrent.{Callable, TimeUnit}
import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * provides helper methods to work with LogoRRR's UI components
 */
class TestFxBaseApplicationTest
  extends ApplicationTest
    with FileMenuActions {

  @AfterEach
  @throws[Exception]
  def tearDown(): Unit = {
    quitApplication()
    // exit application
    //push(KeyCode.COMMAND, KeyCode.Q)
    FxToolkit.hideStage()
    release(Array[KeyCode](): _*)
    release(Array[MouseButton](): _*)
  }

  def clickOn(node: UiNode): FxRobotInterface = clickOn(node.ref)

  def waitForVisibility(id: UiNode): Unit = waitForVisibility(lookup(id.ref))

  def waitForVisibility(query: String): Unit = waitForVisibility(lookup(query))

  def waitForVisibility(nodeQuery: NodeQuery, finiteDuration: FiniteDuration = 2.seconds): Unit = {
    WaitForAsyncUtils.waitFor(finiteDuration.toSeconds, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = nodeQuery.`match`(NodeQueryUtils.isVisible).tryQuery.isPresent
    })
  }

  def waitForPredicate[A <: Node](id: UiNode, clazz: Class[A], predicate: A => Boolean): Unit = {
    WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = {
        predicate(lookup(id.ref).queryAs(clazz))
      }
    })
  }


}
