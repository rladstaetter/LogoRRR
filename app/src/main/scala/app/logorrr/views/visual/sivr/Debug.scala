package app.logorrr.views.visual.sivr

object Debug {
  var cnt = 0

  def inc(): Int = synchronized({
    cnt = cnt + 1
    cnt
  })
}
