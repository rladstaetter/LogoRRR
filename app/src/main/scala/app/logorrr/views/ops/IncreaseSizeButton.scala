package app.logorrr.views.ops

import javafx.beans.property.Property
import javafx.scene.Node

object IncreaseSizeButton:

  def apply(jfxId: String, graphic: Node, step: Int, max: Int, sizeProperty: Property[Number]): IncreaseSizeButton =
    val btn = new IncreaseSizeButton(jfxId, step, max, graphic)
    btn.sizeProperty.bindBidirectional(sizeProperty)
    btn







class IncreaseSizeButton(jfxId: String
                         , step: Int
                         , maxSize: Int
                         , graphic: Node) extends SizeButton(graphic, "+"):
  setId(jfxId)
  setOnAction(_ => {
    val nextSize = getSize.intValue + step
    if nextSize < maxSize then {
      setSize(nextSize)
    } else {
      setSize(maxSize)
    }
  })

