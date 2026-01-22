package app.logorrr.conf.mut

import app.logorrr.conf.{SimpleRange, TimestampSettings}
import javafx.beans.property.{IntegerPropertyBase, StringPropertyBase}

object MutTimestampSettings:

  def apply(ts : TimestampSettings) : MutTimestampSettings =
    val mt = new MutTimestampSettings
    mt.set(ts)
    mt


class MutTimestampSettings extends DateTimePatternHolder
  with StartColHolder
  with EndColHolder:

  def resetToDefault(): Unit = set(TimestampSettings.Default)

  def mkImmutable(): TimestampSettings =
    TimestampSettings(getStartCol, getEndCol, getDateTimePatternCol)

  def set(timestampSettings: TimestampSettings): Unit =
    setStartCol(timestampSettings.startCol)
    setEndCol(timestampSettings.endCol)
    setDateTimePattern(timestampSettings.dateTimePattern)

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
