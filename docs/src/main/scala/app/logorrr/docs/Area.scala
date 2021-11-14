package app.logorrr.docs

object Area {
  object R1280x800 extends Area(1280, 800)

  object R1440x900 extends Area(1440, 900)

  object R2560x1600 extends Area(2560, 1600)

  object R2880x1800 extends Area(2880, 1800)

  val seq: Seq[Area] = Seq(R1280x800, R1440x900, R2560x1600, R2880x1800)
}

case class Area(width: Int, height: Int) extends Product