package app.logorrr.docs.icns

import app.logorrr.docs.Area

object IconDef {
  val seq = Seq(
    IconDef(Area(16, 16, 16, 16), "icon_16x16.png")
    , IconDef(Area(32, 32, 32, 32), "icon_16x16@2x.png")
    , IconDef(Area(32, 32, 32, 32), "icon_32x32.png")
    , IconDef(Area(64, 64, 64, 64), "icon_32x32@2x.png")
    , IconDef(Area(128, 128, 128, 128), "icon_128x128.png")
    , IconDef(Area(256, 256, 256, 256), "icon_128x128@2x.png")
    , IconDef(Area(256, 256, 256, 256), "icon_256x256.png")
    , IconDef(Area(512, 512, 512, 512), "icon_256x256@2x.png")
    , IconDef(Area(512, 512, 512, 512), "icon_512x512.png")
    , IconDef(Area(1024, 1024, 1024, 1024), "icon_512x512@2x.png")
  )

}

case class IconDef(area: Area, iconName: String) extends Product