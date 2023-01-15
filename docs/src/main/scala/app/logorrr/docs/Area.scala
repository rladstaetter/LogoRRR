package app.logorrr.docs

object Area {
  object R1280x800 extends Area(1280, 800)

  object R1440x900 extends Area(1440, 900)

  object R1920x1080 extends Area(1920, 1080)

  val seq: Seq[Area] = Seq(R1280x800, R1440x900, R1920x1080)
}

case class Area(width: Int, height: Int) extends Product