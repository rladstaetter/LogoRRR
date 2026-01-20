package app.logorrr.conf.mut

import app.logorrr.conf.mut.DateTimePatternHolder
import javafx.beans.property.*
import net.ladstatt.util.log.TinyLog
import upickle.default.*

import java.time.*
import java.time.format.DateTimeFormatter
import scala.util.{Failure, Success, Try}


trait StartColHolder:
  val startColProperty: IntegerProperty = new SimpleIntegerProperty()

  def getStartCol: Integer = startColProperty.get()

  def setStartCol(startCol: Integer): Unit = startColProperty.set(startCol)

  def bindStartColProperty(startColProperty: IntegerPropertyBase): Unit = startColProperty.bind(startColProperty)

  def unbindStartColProperty(): Unit = startColProperty.unbind()







  


