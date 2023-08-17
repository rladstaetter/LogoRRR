package app.logorrr.docs

object Area {
  object R1280x800 extends Area(1280, 800, 528, 288)

  object R1440x900 extends Area(1440, 900, 608, 338)

  object R2560x1600 extends Area(2560, 1600, 1168, 688)

  object R2880x1800 extends Area(2880, 1800, 1328, 788)

  val seq: Seq[Area] = Seq(R1280x800, R1440x900, R2560x1600, R2880x1800)
}

/**
 *
 * @param width
 * @param height
 * @param screenshotWidth width when using apples screenshot utility + retina display
 * @param screenshotHeight height when using apples screenshot utility + retina display
 */
case class Area(width: Int
                , height: Int
                , screenshotWidth: Int
                , screenshotHeight: Int) extends Product