package app.logorrr.usecases.settings

import app.logorrr.TestFiles
import app.logorrr.conf.{LogoRRRGlobals, TimeSettings}
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.settings.TimestampSettingsEditor
import javafx.scene.control.TextField
import org.junit.jupiter.api.Test

/**
 * Tests settings dialogue
 */
class TimeSettingsEditorTest extends SingleFileApplicationTest(TestFiles.simpleLog0):

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
    openFile(fileId)
    val customPattern = TimeSettings.DefaultPattern.replace(".", ",")
    val customStartCol = 22
    val customEndCol = customStartCol + customPattern.length

    withOpenedSettingsEditor {
      waitAndClickVisibleItem(TimestampSettingsEditor.EnableInitalizeButton)
      waitForVisibility(TimestampSettingsEditor.PatternTextField)
      waitForVisibility(TimestampSettingsEditor.StartColTextField)
      waitForVisibility(TimestampSettingsEditor.EndColTextField)

      val settings = LogoRRRGlobals.timeSettings
      assert(settings.mkImmutable() == TimeSettings.Default)
      // query textfields
      assert(lookup[TextField](TimestampSettingsEditor.PatternTextField).getText == TimeSettings.DefaultPattern)
      assert(lookup[TextField](TimestampSettingsEditor.StartColTextField).getText == TimeSettings.DefaultStartCol.toString)
      assert(lookup[TextField](TimestampSettingsEditor.EndColTextField).getText == TimeSettings.DefaultEndCol.toString)


      replaceText(lookup[TextField](TimestampSettingsEditor.PatternTextField), customPattern)
      waitAndClickVisibleItem(TimestampSettingsEditor.StartColTextField).eraseText(TimeSettings.DefaultStartCol.toString.length).write(customStartCol.toString)
      waitAndClickVisibleItem(TimestampSettingsEditor.EndColTextField).eraseText(TimeSettings.DefaultEndCol.toString.length).write(customEndCol.toString)
    }

    assert(LogoRRRGlobals.timeSettings.mkImmutable() == TimeSettings(customStartCol, customEndCol, customPattern), "Settings don't match")



