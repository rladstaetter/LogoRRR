package app.logorrr.usecases.settings

import app.logorrr.TestFiles
import app.logorrr.conf.{LogoRRRGlobals, TimestampSettings}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.settings.TimestampSettingsEditor
import javafx.geometry.Point2D
import javafx.scene.control.TextField
import org.junit.jupiter.api.{Assertions, Test}

/**
 * Tests settings dialogue
 */
class TimestampSettingsEditorTest extends SingleFileApplicationTest(TestFiles.simpleLog0)
  with SettingsEditorTestActions:

  /**
   * Tests timestamp settings:
   *
   * - Click on 'initialize' button
   * - test visibility of timestamp pattern textfield, start and end col textfields
   * - tests that each field contains default values (= test against hardcoded defaults)
   * - check against global settings
   * - change settings to other values
   * - check against global settings again
   */
  @Test def checkGeneralTimestampsettingsDialogue(): Unit =
    withOpenedSettingsEditor:
      waitAndClickVisibleItem(TimestampSettingsEditor.EnableInitalizeButton)
      waitForVisibility(TimestampSettingsEditor.PatternTextField)
      waitForVisibility(TimestampSettingsEditor.StartColTextField)
      waitForVisibility(TimestampSettingsEditor.EndColTextField)

      LogoRRRGlobals.getTimestampSettings match {
        case Some(settings) =>
          assert(settings.mkImmutable() == TimestampSettings.Default)
          // query textfields
          assert(lookup[TextField](TimestampSettingsEditor.PatternTextField).getText == TimestampSettings.DefaultPattern)
          assert(lookup[TextField](TimestampSettingsEditor.StartColTextField).getText == TimestampSettings.DefaultStartCol.toString)
          assert(lookup[TextField](TimestampSettingsEditor.EndColTextField).getText == TimestampSettings.DefaultEndCol.toString)

          val customPattern = TimestampSettings.DefaultPattern.replace(".", ",")
          val customStartCol = 22
          val customEndCol = customStartCol + customPattern.length

          replaceText(lookup[TextField](TimestampSettingsEditor.PatternTextField), customPattern)
          waitAndClickVisibleItem(TimestampSettingsEditor.StartColTextField).eraseText(TimestampSettings.DefaultStartCol.toString.length).write(customStartCol.toString)
          waitAndClickVisibleItem(TimestampSettingsEditor.EndColTextField).eraseText(TimestampSettings.DefaultEndCol.toString.length).write(customEndCol.toString)
          LogoRRRGlobals.getTimestampSettings match {
            case Some(value) => assert(value.mkImmutable() == TimestampSettings(customStartCol, customEndCol, customPattern))
            case None => Assertions.fail("should not be None")
          }
        case None => Assertions.fail("Timestamp settings should be set to default")
      }


