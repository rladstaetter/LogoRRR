package app.logorrr

import app.logorrr.views.LogoRRRNode
import javafx.scene.Node
import javafx.scene.input.{KeyCode, MouseButton}
import org.junit.jupiter.api.AfterEach
import org.testfx.api.{FxRobotInterface, FxToolkit}
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import java.util.concurrent.{Callable, TimeUnit}

/**
 * provides helper methods to work with LogoRRR's UI components
 */
class TestFxBaseApplicationTest extends ApplicationTest {


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

  def waitForVisibility(query: String): Unit = {
    WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = lookup(query).`match`(NodeQueryUtils.isVisible).tryQuery.isPresent
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
