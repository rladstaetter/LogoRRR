package app.logorrr.usecases

import app.logorrr.steps.{FileMenuActions, LogoRRRAppMenuActions}
import javafx.scene.input.{KeyCode, MouseButton}
import org.junit.jupiter.api.AfterEach
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest

/**
 * provides helper methods to work with LogoRRR's UI components
 */
class TestFxBaseApplicationTest
  extends ApplicationTest
    with FxBaseInterface
    with FileMenuActions
    with LogoRRRAppMenuActions:

  @AfterEach
  @throws[Exception]
  def tearDown(): Unit =
    quitApplication()
    FxToolkit.hideStage()
    release(Array[KeyCode]() *)
    release(Array[MouseButton]() *)


