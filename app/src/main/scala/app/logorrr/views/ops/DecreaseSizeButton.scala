package app.logorrr.views.ops

import app.logorrr.conf.FileId
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

class DecreaseSizeButton(calcId: SimpleObjectProperty[FileId] => String
                         , graphic: Node
                         , step: Int
                         , boundary: Int) extends SizeButton(calcId, graphic, step, boundary, _ - _, _ > _, "-")


