package app.logorrr.usecases

import app.logorrr.LogoRRRApp
import app.logorrr.clv.ChunkListCell
import app.logorrr.conf.FileId
import app.logorrr.model.LogEntry
import app.logorrr.services.LogoRRRServices
import app.logorrr.views.a11y.UiNode
import app.logorrr.views.a11y.uinodes.{FileMenu, LogoRRRMenu, SettingsEditor, UiNodes}
import app.logorrr.views.logfiletab.LogoRRRChunkListView
import app.logorrr.views.ops.time.{SliderVBox, TimestampSettingsButton}
import app.logorrr.views.search.st.{ASearchTermToggleButton, RemoveSearchTermButton, SearchTermToggleButton, SearchTermToolBar}
import app.logorrr.views.search.stg.OpenSettingsDialogAndAddFavorites
import app.logorrr.views.search.{SearchButton, SearchTextField}
import app.logorrr.views.settings.timestamp.{LogViewLabel, TimestampFormatSetButton}
import app.logorrr.views.settings.{SettingsStgListView, TimestampSettingsEditor}
import app.logorrr.views.text.LogTextView
import javafx.collections.transformation.FilteredList
import javafx.geometry.{Point2D, Pos}
import javafx.scene.Node
import javafx.scene.control.{ChoiceBox, Slider, TabPane, TextField}
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.stage.Stage
import org.junit.jupiter.api.AfterEach
import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.service.query.NodeQuery
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import java.lang
import java.util.concurrent.{Callable, TimeUnit}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.jdk.CollectionConverters.CollectionHasAsScala

/**
 * provides helper methods to work with LogoRRR's UI components
 */
abstract class TestFxBaseApplicationTest extends ApplicationTest:

  def services: LogoRRRServices

  @throws[Exception]
  override def start(stage: Stage): Unit = {
    LogoRRRApp.start(stage, services)
  }

  // --- functions which define user action steps
  protected def clickOnSetFormatButton(fileId: FileId): Unit = {
    clickOn(TimestampFormatSetButton.uiNode(fileId))
  }

  /** open a file and the timestamp settings dialog */
  protected def openFileAndTimestampDialogue(fileId: FileId): Unit = {
    openFile(fileId)
    waitAndClickVisibleItem(TimestampSettingsButton.uiNode(fileId))
  }

  /** sets correct start and end columns which contains a timestamp */
  protected def setCorrectStartAndEndColumns(fileId: FileId) =
    openFileAndTimestampDialogue(fileId)
    // set position twice (?!) - this is a bug but atm the best implementation available.
    waitAndClickVisibleItem(LogViewLabel.uiNode(fileId, 1, 0))
    clickOn(LogViewLabel.uiNode(fileId, 1, 0))
    clickOn(LogViewLabel.uiNode(fileId, 1, 23))

  def expectLabelText(fileId: FileId, pos: Pos, expectedText: String): Unit = {
    waitForBredicate(lookup[SliderVBox](SliderVBox.uiNode(fileId, pos)), _.label.getText == expectedText)
  }

  def interacT[T](f: => T): FxRobot = interact(new Callable[T] {
    override def call(): T = f
  })

  def withOpenedSettingsEditor(f: => Unit): Unit =
    waitAndClickVisibleItem(LogoRRRMenu.Self)
    waitAndClickVisibleItem(LogoRRRMenu.Settings)
    f
    waitAndClickVisibleItem(SettingsEditor.CloseButton)

  def lookupListView(): SettingsStgListView = lookup[SettingsStgListView](SettingsEditor.SettingsStgListView)

  protected def openSettingsEditorAndPerform(fn: SettingsStgListView => Unit): Unit =
    withOpenedSettingsEditor(fn(lookupListView()))


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


  def lookupLogTextView(fileId: FileId): LogTextView =
    val logTextViewUiElem = LogTextView.uiNode(fileId)
    lookup(logTextViewUiElem.ref).query[LogTextView]

  def lookupChunkListView(fileId: FileId): LogoRRRChunkListView =
    lookup(LogoRRRChunkListView.uiNode(fileId).ref).query[LogoRRRChunkListView]

  def nthCell(clv: LogoRRRChunkListView, cellIndex: Int): ChunkListCell[LogEntry] =
    from(clv).lookup(".list-cell").nth(cellIndex).query[ChunkListCell[LogEntry]]()


  def search(fileId: FileId, terms: String*): Unit = terms.foreach(t => searchFor(fileId, t))

  def searchFor(fileId: FileId, needle: String): FxRobotInterface =
    clickOn(SearchTextField.uiNode(fileId)).write(needle)
    clickOn(SearchButton.uiNode(fileId))

  def existsSearchTermToggleButton(fileId: FileId, searchTerm: String): Boolean =
    Option(lookup(ASearchTermToggleButton.uiNode(fileId, searchTerm))).isDefined

  def lookupSearchTerms(fileId: FileId): FilteredList[Node] =
    val toolbar: SearchTermToolBar = lookup[SearchTermToolBar](SearchTermToolBar.uiNode(fileId))
    toolbar.getItems.filtered(n => n.isInstanceOf[SearchTermToggleButton])


  // wipes all search terms
  def clearAllSearchTerms(fileId: FileId): Unit =
    var finished = false
    while (!finished)
      val items = lookupSearchTerms(fileId)
      finished = items.isEmpty
      if !finished then clickOn(RemoveSearchTermButton.uiNode(fileId, items.get(0).asInstanceOf[ASearchTermToggleButton].getValue))


  def clickOnFavoritesButton(fileId: FileId): Unit =
    waitAndClickVisibleItem(OpenSettingsDialogAndAddFavorites.uiNode(fileId))
    interactNoWait(() => new Callable[Unit] {
      override def call(): Unit = waitAndClickVisibleItem(OpenSettingsDialogAndAddFavorites.uiNode(fileId))
    })
    waitAndClickVisibleItem(TimestampSettingsEditor.EnableInitalizeButton)
    waitAndClickVisibleItem(SettingsEditor.CloseButton)


  @AfterEach
  @throws[Exception]
  def tearDown(): Unit =
    quitApplication()
    FxToolkit.hideStage()
    release(Array[KeyCode]() *)
    release(Array[MouseButton]() *)


  def quitApplication(): Unit =
    waitAndClickVisibleItem(LogoRRRMenu.Self)
    waitAndClickVisibleItem(LogoRRRMenu.CloseApplication)

  protected def openFile(fileId: FileId): Unit =
    waitAndClickVisibleItem(FileMenu.Self)
    waitAndClickVisibleItem(FileMenu.OpenFile)
    waitForVisibility(SearchTextField.uiNode(fileId))

  protected def closeAllFiles(): FxRobotInterface =
    clickOn(FileMenu.Self)
    waitForVisibility(FileMenu.CloseAll)
    clickOn(FileMenu.CloseAll)


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

  def waitAndClickVisibleItem(node: UiNode): FxRobotInterface =
    waitForVisibility(node)
    clickOn(node)

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

  def waitForBredicate[A](a :A, predicate: A => Boolean, timeout: FiniteDuration = 2.seconds): Unit =
    WaitForAsyncUtils.waitFor(timeout.toSeconds, TimeUnit.SECONDS, new Callable[java.lang.Boolean] {
      override def call(): java.lang.Boolean = {
        predicate(a)
      }
    })

  def expectCountOfOpenFiles(expectedCount: Int): Unit = {
    waitForBredicate[TabPane](lookup[TabPane](UiNodes.MainTabPane), _.getTabs.size == expectedCount)
  }

  def checkForEmptyTabPane(): Unit =
    waitForBredicate[TabPane](lookup[TabPane](UiNodes.MainTabPane), _.getTabs.isEmpty)

  def checkForNonEmptyTabPane(): Unit =
    waitForBredicate[TabPane](lookup[TabPane](UiNodes.MainTabPane), tabPane => !tabPane.getTabs.isEmpty)



