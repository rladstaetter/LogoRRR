package app.logorrr.usecases

import app.logorrr.views.a11y.UiNode
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.TextField
import org.testfx.api.FxRobotInterface
import org.testfx.service.query.NodeQuery
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import java.util.concurrent.{Callable, TimeUnit}
import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * some functions which are used in various tests
 * */
trait FxBaseInterface:
  self: FxRobotInterface =>

  /** makes sure that the caret is at the end of the textfield */
  def replaceText(textField: TextField, text: String): Unit =
    val textLength = textField.getText.length
    // Get the screen coordinates of the TextField
    val bounds = textField.localToScreen(textField.getBoundsInLocal)

    // Calculate the point: Right edge (MaxX) and middle height (Centroid)
    val rightSideX = bounds.getMaxX - 5 // 5 pixels inward from the far right
    val centerY = (bounds.getMinY + bounds.getMaxY) / 2
    moveTo(new Point2D(rightSideX, centerY)).clickOn().eraseText(textLength).write(text)

  def lookup[T <: Node](uiNode: UiNode): T = lookup(uiNode.ref).query[T]

  def clickOn(node: UiNode): FxRobotInterface = clickOn(node.ref)

  def doubleClickOn(node: UiNode): FxRobotInterface = doubleClickOn(node.ref)

  def waitForVisibility(id: UiNode): Unit = waitForVisibility(lookup(id.ref))

  // def waitForVisibility(query: String): Unit = waitForVisibility(lookup(query))

  def waitForVisibility(nodeQuery: NodeQuery, timeout: FiniteDuration = 2.seconds): Unit =
    WaitForAsyncUtils.waitFor(timeout.toSeconds, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = nodeQuery.`match`(NodeQueryUtils.isVisible).tryQuery.isPresent
    })

  def waitForPredicate[A <: Node](id: UiNode, clazz: Class[A], predicate: A => Boolean, timeout: FiniteDuration = 2.seconds): Unit =
    WaitForAsyncUtils.waitFor(timeout.toSeconds, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = {
        predicate(lookup(id.ref).queryAs(clazz))
      }
    })
