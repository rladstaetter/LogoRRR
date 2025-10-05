package app.logorrr.views.ops.time

import app.logorrr.conf.mut.MutLogFileSettings
import app.logorrr.io.FileId
import app.logorrr.model.LogEntry
import app.logorrr.views.ops.PulsatingAnimationTimer
import app.logorrr.views.text.LogTextView
import app.logorrr.views.a11y.{UiNode, UiNodeFileIdAware}
import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.beans.binding.StringBinding
import javafx.beans.property.{IntegerProperty, SimpleIntegerProperty}
import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{Button, Label, Tooltip}
import javafx.scene.layout.StackPane
import org.kordamp.ikonli.fontawesome6.{FontAwesomeRegular, FontAwesomeSolid}
import org.kordamp.ikonli.javafx.FontIcon

import scala.collection.mutable.ListBuffer

object ReplayStackPane extends UiNodeFileIdAware {

  val availableSpeeds: Seq[Double] = Seq(1.0, 2.0, 4.0, 10.0, 100.0)

  val DefaultSpeedIndex: Int = 0

  override def uiNode(id: FileId): UiNode = UiNode(id, classOf[ReplayButton])
}

class ReplayStackPane(mutLogFileSettings: MutLogFileSettings
                      , filteredList: ObservableList[LogEntry]
                      , lowerSlider: TimerSlider
                      , upperSlider: TimerSlider
                      , logTextView: LogTextView) extends StackPane {

  val replaySpeedIndexProperty = new SimpleIntegerProperty(ReplayStackPane.DefaultSpeedIndex)

  setUserData(ReplayStackPane.availableSpeeds.head)
  setId(ReplayStackPane.uiNode(mutLogFileSettings.getFileId).value)

  // set visible only if we have a valid timestamp
  visibleProperty().bind(mutLogFileSettings.hasTimestampSetting)

  val binding: StringBinding = new StringBinding {
    bind(replaySpeedIndexProperty)

    override def computeValue(): String = ReplayStackPane.availableSpeeds(replaySpeedIndexProperty.get).toInt.toString
  }

  val label = new Label()
  label.setTranslateX(10)
  label.setTranslateY(-10)
  label.setStyle("-fx-font-size: 8px;")
  label.textProperty().bind(binding)

  // since timerbutton is a stackpane, this css commands are necessary to have the same effect as
  // defined in primer-light.css
  val replayButton = new ReplayButton(filteredList, lowerSlider, upperSlider, replaySpeedIndexProperty, logTextView)

  getChildren.addAll(replayButton, label)

  def reset(): Unit = {
    replaySpeedIndexProperty.set(ReplayStackPane.DefaultSpeedIndex)
    replayButton.stopTimer()
  }

}

class ReplayButton(filteredList: ObservableList[LogEntry]
                   , lowerSlider: TimerSlider
                   , upperSlider: TimerSlider
                   , replaySpeedIndexProperty: IntegerProperty
                   , logTextView: LogTextView) extends Button {

  var timeline: Timeline = _
  private val icon = new FontIcon(FontAwesomeSolid.PLAY_CIRCLE)
  private val iconLight = new FontIcon(FontAwesomeRegular.PLAY_CIRCLE)
  private val tooltip = new Tooltip("replay log")
  var animationTimer: PulsatingAnimationTimer = _

  setGraphic(icon)
  setTooltip(tooltip)
  setOnAction(_ => {
    // calculate speed
    val speedFactor: Double = ReplayStackPane.availableSpeeds(replaySpeedIndexProperty.get())
    lowerSlider.setValue(lowerSlider.getMin)
    upperSlider.setValue(upperSlider.getMin)
    Option(timeline) match {
      case Some(tl) => tl.stop()
      case None =>
    }
    val frames = mkFrames()
    timeline = new Timeline(frames.toList: _*)
    timeline.setRate(speedFactor)
    timeline.setCycleCount(Animation.INDEFINITE)
    timeline.play()
    val newVal = (replaySpeedIndexProperty.get() + 1) % ReplayStackPane.availableSpeeds.size
    replaySpeedIndexProperty.setValue(newVal)
    TimeUtil.calcTimeInfo(filteredList) match {
      case Some(timeInfo) =>
        Option(animationTimer) match {
          case Some(value) => value.stop()
          case None =>
        }
        animationTimer = new PulsatingAnimationTimer(this, iconLight, icon, tooltip, tooltip.getText, timeInfo.duration)
        animationTimer.start()
      case None =>
    }
  })

  def stopTimer(): Unit = {
    replaySpeedIndexProperty.set(ReplayStackPane.DefaultSpeedIndex)
    animationTimer.stop()
    Option(timeline).foreach(_.stop())
  }

  def sliderEvent(duration: javafx.util.Duration): EventHandler[ActionEvent] = (_: ActionEvent) => {
    if (upperSlider.getValue < upperSlider.getMax) {
      upperSlider.setValue(upperSlider.getMin + duration.toMillis)
      logTextView.scrollTo(filteredList.size() - 1)
    }
  }

  def mkFrames(): ListBuffer[KeyFrame] = {
    val keyFrames = new ListBuffer[KeyFrame]
    filteredList.forEach((t: LogEntry) => {
      t.someJfxDuration match {
        case Some(duration) => keyFrames.addOne(new KeyFrame(duration, sliderEvent(duration)))
        case None =>
      }
    })

    keyFrames.addOne(new KeyFrame(keyFrames.last.getTime.add(javafx.util.Duration.seconds(1)), (_: ActionEvent) => stopTimer()))
    keyFrames
  }

}
