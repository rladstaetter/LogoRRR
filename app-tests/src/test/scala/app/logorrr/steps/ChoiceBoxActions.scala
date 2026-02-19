package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.UiNode
import javafx.scene.Node
import javafx.scene.control.{ChoiceBox, ListCell}
import org.testfx.matcher.base.NodeMatchers
import org.testfx.util.WaitForAsyncUtils

import java.{lang, util}
import java.util.concurrent.{Callable, TimeUnit}
import scala.jdk.CollectionConverters.CollectionHasAsScala

trait ChoiceBoxActions:
  self: TestFxBaseApplicationTest =>

  def matchItems[T](uiNode: UiNode, expectedItems: Seq[T]): Unit =
    waitForPredicate[ChoiceBox[T]](uiNode, classOf[ChoiceBox[T]], cb => {
      val origSeq = cb.getItems.asScala.toSeq
      val ret = origSeq == expectedItems
      if (!ret) {
        System.err.println(origSeq.mkString(",") + " didn't match " + expectedItems.mkString(","))
      }
      ret
    })

  def selectChoiceBoxByValue(choiceBox: UiNode)(value: String): Unit = {
    // 1. Ensure the UI thread is settled before starting
    WaitForAsyncUtils.waitForFxEvents()

    // 2. Click the ChoiceBox to open it
    clickOn(choiceBox)

    // wait until node is available
    WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): lang.Boolean =
        val potentialNode = lookup(value).queryAll().stream().findFirst()
        potentialNode.isPresent && potentialNode.get().isVisible
    })

    // 4. Click the actual Node found, not just the string
    val targetNode = lookup(value).query[Node]()
    clickOn(targetNode)

    // 5. Final sync to let the popup close
    WaitForAsyncUtils.waitForFxEvents()
  }
