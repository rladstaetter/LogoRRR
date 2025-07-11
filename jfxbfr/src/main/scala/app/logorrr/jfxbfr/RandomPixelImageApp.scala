package app.logorrr.jfxbfr

import javafx.animation.{Animation, KeyFrame, Timeline}
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.image.{ImageView, PixelBuffer, PixelFormat, WritableImage}
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.{Callback, Duration}

import java.nio.IntBuffer
import scala.util.Random

/**
 * Scala application demonstrating updating a WritableImage with random pixel data
 * using vanilla JavaFX classes. The image is updated using PixelBuffer and its updateBuffer method.
 */
class RandomPixelImageApp extends Application {

  // Define image dimensions
  private val ImageWidth = 2000
  private val ImageHeight = 2000

  val rectangle2D = new Rectangle2D(0, 0, ImageWidth, ImageHeight)

  // Get the ARGB pixel format instance
  private val pixelFormat = PixelFormat.getIntArgbPreInstance

  // Create an IntBuffer to hold the pixel data.
  // The size is width * height because each pixel is an Int (ARGB).
  private val pixelBufferData: IntBuffer = IntBuffer.allocate(ImageWidth * ImageHeight)

  // Create a PixelBuffer from the IntBuffer, WritableImage, and PixelFormat
  private val pixelBuffer = new PixelBuffer[IntBuffer](
    ImageWidth,
    ImageHeight,
    pixelBufferData,
    pixelFormat
  )

  // Create a WritableImage with the specified dimensions
  private val writableImage = new WritableImage(pixelBuffer)

  // Create an ImageView to display the WritableImage
  private val imageView = new ImageView(writableImage)

  /**
   * Fills the pixelBufferData with new random ARGB values.
   * Each pixel is represented by an integer where:
   * - Bits 24-31: Alpha (A)
   * - Bits 16-23: Red (R)
   * - Bits 8-15: Green (G)
   * - Bits 0-7: Blue (B)
   */
  private def fillBufferWithRandomData(pixelBufferData : IntBuffer): Unit = {
    // Reset buffer position to 0 before putting new data
    pixelBufferData.rewind()

    for (_ <- 0 until ImageHeight) {
      for (_ <- 0 until ImageWidth) {
        // Generate random ARGB values
        val alpha = 255 // Full opacity
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)

        // Combine ARGB into a single integer
        val argb = (alpha << 24) | (red << 16) | (green << 8) | blue
        // Put the ARGB integer into the buffer
        pixelBufferData.put(argb)
      }
    }
    // After filling, rewind the buffer again so updateBuffer can read from the beginning
    pixelBufferData.rewind()
  }

  /**
   * Updates the WritableImage by filling the buffer with new random data
   * and then calling pixelBuffer.updateBuffer().
   */
  private def updateImage(): Unit = {

    // Call updateBuffer to push the changes from the IntBuffer to the WritableImage
    pixelBuffer.updateBuffer(new Callback[PixelBuffer[IntBuffer], Rectangle2D] {
      override def call(p: PixelBuffer[IntBuffer]): Rectangle2D = {
        fillBufferWithRandomData(p.getBuffer)
        rectangle2D
      }
    })
  }

  override def start(primaryStage: Stage): Unit = {
    // Initial fill and update of the image
    updateImage()

    // Create a Timeline to periodically update the image
    val timeline = new Timeline()
    timeline.setCycleCount(Animation.INDEFINITE) // Run indefinitely
    timeline.getKeyFrames.add(
      new KeyFrame(
        Duration.millis(100), // Update every 100 milliseconds
        new EventHandler[ActionEvent] {
          override def handle(event: ActionEvent): Unit = {
            updateImage() // Call updateImage on each key frame
          }
        }
      )
    )
    timeline.play() // Start the animation

    // Set up the root pane and add the ImageView
    val root = new StackPane()
    root.getChildren.add(imageView)

    // Set up the scene and stage
    val scene = new Scene(root, 500, 500)
    primaryStage.setTitle("Vanilla JavaFX Random Pixel Image Updater")
    primaryStage.setScene(scene)
    primaryStage.show()
  }
}

/**
 * Companion object to launch the JavaFX application.
 */
object RandomPixelImageApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[RandomPixelImageApp], args: _*)
  }
}
