package app.logorrr.steps

import app.logorrr.usecases.TestFxBaseApplicationTest
import app.logorrr.views.a11y.UiNode
import javafx.scene.control.ChoiceBox

import scala.jdk.CollectionConverters.CollectionHasAsScala

trait ChoiceBoxActions {
  self: TestFxBaseApplicationTest =>

  def matchItems[T](uiNode: UiNode, expectedItems: Seq[T]): Unit = {
    waitForPredicate[ChoiceBox[T]](uiNode, classOf[ChoiceBox[T]], cb => {
      cb.getItems.asScala.toSeq == expectedItems
    })
  }

}
