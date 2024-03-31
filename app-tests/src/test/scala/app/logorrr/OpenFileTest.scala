package app.logorrr

import app.logorrr.conf.Settings
import app.logorrr.io.FileId
import app.logorrr.services.LogoRRRServices
import app.logorrr.services.fileservices.MockOpenFileService
import app.logorrr.services.hostservices.MockHostServices
import app.logorrr.views.LogoRRRNodes
import app.logorrr.views.LogoRRRNodes.ref
import app.logorrr.views.logfiletab.LogFileTab
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.stage.Stage
import org.junit.jupiter.api.{AfterEach, Test}
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import java.nio.file.Paths
import java.util.concurrent.{Callable, TimeUnit}

/**
 * Extend this class which setups environment for LogoRRR
 */
trait LogoRRRApplicationTest extends ApplicationTest {

  override def init(): Unit = {
    super.init()
  }

  def checkExistance(id : String) : Unit = {
    WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = lookup(ref(id)).`match`(NodeQueryUtils.isVisible).tryQuery.isPresent
    })
  }

}

class OpenFileTest extends LogoRRRApplicationTest {

  private val fileToOpen = Paths.get("src/test/resources/app/logorrr/OpenFileTest.log")

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    val services = LogoRRRServices(new MockHostServices
      , new MockOpenFileService(Option(fileToOpen))
      , isUnderTest = true)
    LogoRRRApp.start(stage, Settings.Default, services)
    stage.toFront()
  }

  @AfterEach
  @throws[Exception]
  def tearDown(): Unit = {
    FxToolkit.hideStage()
    release(Array[KeyCode](): _*)
    release(Array[MouseButton](): _*)
  }

  /**
   * checks if an open file creates a new logfiletab with an id matching the file opened.
   */
  @Test def openFileTest(): Unit = {
    checkExistance(LogoRRRNodes.FileMenu)
    clickOn(ref(LogoRRRNodes.FileMenu))
    checkExistance(LogoRRRNodes.FileMenuOpenFile)
    clickOn(ref(LogoRRRNodes.FileMenuOpenFile))
    checkExistance(LogFileTab.idFor(FileId(fileToOpen)))
  }

}