package app.logorrr.views.ops

import javafx.beans.property.Property
import javafx.scene.Node

object DecreaseSizeButton:

  def apply(jfxId: String
            , graphic: Node
            , step: Int
            , min: Int
            , sizeProperty: Property[Number]): DecreaseSizeButton =
    val btn = new DecreaseSizeButton(jfxId, step, min, graphic)
    btn.sizeProperty.bindBidirectional(sizeProperty)
    btn

class DecreaseSizeButton(jfxId: String
                         , step: Int
                         , minSize: Int
                         , graphic: Node) extends SizeButton(graphic, "-"):

  setId(jfxId)
  setOnAction(_ => {
    val nextSize = getSize.intValue() - step
    if nextSize > minSize then {
      setSize(nextSize)
    } else {
      setSize(minSize)
    }
  })



