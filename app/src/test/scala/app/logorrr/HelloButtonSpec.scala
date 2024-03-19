package app.logorrr

import app.logorrr.testfx.TestFxSpec
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers

class HelloButtonSpec extends TestFxSpec {

  lazy val button = new Button("click me!")

  override def start(stage: Stage): Unit = {
    button.setId("myButton")
    button.setOnAction(_ => button.setText("clicked!"))
    stage.setScene(new Scene(new StackPane(button), 100, 100))
    stage.show()
  }

  "foo" in {
    FxAssert.verifyThat(button, LabeledMatchers.hasText("click me!"))
    FxAssert.verifyThat("#myButton", LabeledMatchers.hasText("click me!"))
    FxAssert.verifyThat(".button", LabeledMatchers.hasText("click me!"))
  }
  "click on button" in {
    // when:
    clickOn(".button")

    // then:
    FxAssert.verifyThat(button, LabeledMatchers.hasText("clicked!"))
    // or (lookup by css id):
    FxAssert.verifyThat("#myButton", LabeledMatchers.hasText("clicked!"))
    // or (lookup by css class):
    FxAssert.verifyThat(".button", LabeledMatchers.hasText("clicked!"))
  }
}
