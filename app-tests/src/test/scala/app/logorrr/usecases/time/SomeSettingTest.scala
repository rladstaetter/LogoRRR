package app.logorrr.usecases.time

import app.logorrr.TestFiles
import app.logorrr.usecases.SingleFileApplicationTest
import app.logorrr.views.ops.time.TimerSlider
import javafx.geometry.Pos

abstract class SomeSettingTest extends SingleFileApplicationTest(TestFiles.timedLog):

  def performActions(): Unit =
    val earilestTimestamp = "2023-08-02 21:16:33,193"
    val latestTimestamp = "2023-08-02 21:16:38,656"

    // 3. Verify initial lowest and highest timestamps
    expectLabelText(fileId, Pos.CENTER_LEFT, earilestTimestamp)
    expectLabelText(fileId, Pos.CENTER_RIGHT, latestTimestamp)

    // 4. Lookup slider references
    val leftSlider = lookup[TimerSlider](TimerSlider.uiNode(fileId, Pos.CENTER_LEFT))
    val rightSlider = lookup[TimerSlider](TimerSlider.uiNode(fileId, Pos.CENTER_RIGHT))

    // 5. Change the right slider (simulate moving to the start)
    interacT(rightSlider.setValue(rightSlider.getMin))
    org.testfx.util.WaitForAsyncUtils.waitForFxEvents()
    expectLabelText(fileId, Pos.CENTER_RIGHT, earilestTimestamp)

    // 6. Put right slider back to the end
    interacT(rightSlider.setValue(rightSlider.getMax))
    org.testfx.util.WaitForAsyncUtils.waitForFxEvents()
    expectLabelText(fileId, Pos.CENTER_RIGHT, latestTimestamp)

    // 7. Move left slider to the highest point (max)
    interacT(leftSlider.setValue(leftSlider.getMax))
    org.testfx.util.WaitForAsyncUtils.waitForFxEvents()
    expectLabelText(fileId, Pos.CENTER_LEFT, latestTimestamp)
