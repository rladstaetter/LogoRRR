package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.UiNode
import javafx.scene.control.ChoiceBox

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

  /**
   * Given a choicebox, select an entry by given value
   *
   * @param choiceBox the choicebox to choose
   * @param value     the value to select
   */
  def selectChoiceBoxByValue(choiceBox: UiNode)(value: String): Unit =
    clickOn(choiceBox).clickOn(value)
