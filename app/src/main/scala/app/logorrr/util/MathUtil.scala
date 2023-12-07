package app.logorrr.util

import scala.math.BigDecimal.RoundingMode

object MathUtil {

  def roundUp(doubleNumber: Double): Int = {
    BigDecimal.double2bigDecimal(doubleNumber).setScale(0, RoundingMode.UP).intValue
  }

  def roundDown(doubleNumber: Double): Int = {
    BigDecimal.double2bigDecimal(doubleNumber).setScale(0, RoundingMode.DOWN).intValue
  }

}
