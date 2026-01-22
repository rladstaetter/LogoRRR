package app.logorrr.conf.mut

import javafx.beans.property.*
import net.ladstatt.util.log.TinyLog
import upickle.default.*

import java.time.*
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}


trait DateTimePatternHolder:
  val dateTimePatternProperty: SimpleStringProperty = new SimpleStringProperty()

  def getDateTimePatternCol: String = dateTimePatternProperty.get()

  def setDateTimePattern(dateTimePattern: String): Unit = dateTimePatternProperty.set(dateTimePattern)

  def bindDateTimePatternColProperty(dateTimePatternColProperty: StringPropertyBase): Unit = dateTimePatternColProperty.bind(dateTimePatternColProperty)

  def unbindDateTimePatternColProperty(): Unit = dateTimePatternProperty.unbind()









  


