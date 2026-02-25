package app.logorrr.conf.mut

import app.logorrr.conf.TimeSettings
import app.logorrr.util.TimeUtil
import javafx.beans.binding.{BooleanBinding, ObjectBinding}
import javafx.beans.property.{IntegerPropertyBase, Property, StringPropertyBase}

import java.time.format.DateTimeFormatter

object MutTimeSettings:

  def apply(ts: TimeSettings): MutTimeSettings =
    val mt = new MutTimeSettings
    mt.set(ts)
    mt


class MutTimeSettings extends DateTimePatternHolder
  with StartColHolder
  with EndColHolder:

  def resetToDefault(): Unit = set(TimeSettings.Default)

  def mkImmutable(): TimeSettings =
    TimeSettings(getStartCol, getEndCol, getDateTimePattern)

  def set(other: MutTimeSettings): Unit = {
    setStartCol(other.getStartCol)
    setEndCol(other.getEndCol)
    setDateTimePattern(other.getDateTimePattern)
  }

  def set(timeSettings: TimeSettings): Unit =
    setStartCol(timeSettings.startCol)
    setEndCol(timeSettings.endCol)
    setDateTimePattern(timeSettings.dateTimePattern)

  def dateTimeFormatterBinding: ObjectBinding[DateTimeFormatter] = new ObjectBinding[DateTimeFormatter]() {
    this.bind(dateTimePatternProperty)

    override def computeValue(): DateTimeFormatter = {
      TimeUtil.mkDateTimeFormatter(dateTimePatternProperty.get())
    }
  }

  def bind(startColProperty: IntegerPropertyBase
           , endColProperty: IntegerPropertyBase
           , dateTimePatternProperty: StringPropertyBase): Unit = {
    bindStartColProperty(startColProperty)
    bindEndColProperty(endColProperty)
    bindDateTimePatternColProperty(dateTimePatternProperty)
  }

  def unbind(): Unit = {
    unbindStartColProperty()
    unbindEndColProperty()
    unbindDateTimePatternColProperty()
  }

  val validBinding: BooleanBinding = new BooleanBinding {

    this.bind(startColProperty, endColProperty, dateTimePatternProperty)

    override def computeValue(): Boolean =
      startColProperty.get() < endColProperty.get() && Option(dateTimePatternProperty.get).exists(_.nonEmpty)

  }

  val allProps: Set[Property[?]] =
    Set(
      startColProperty
      , endColProperty
      , dateTimePatternProperty)