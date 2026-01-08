package app.logorrr.views

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import net.ladstatt.util.log.CanLog

/**
 * LogoRRR supports some keyboard shortcuts to make life easier for the user.
 *
 * <ul>
 *   <li>CTRL-F/CMD-F : set focus to search</li>
 * </ul>
 *
 * Object is accessed directly/globally since it makes life much easier ...
 */
object LogoRRRAccelerators extends CanLog:

  val activeSearchTextField = new SimpleObjectProperty[TextField]()

  def getActiveSearchTextField: TextField = activeSearchTextField.get()

  def setActiveSearchTextField(textField: TextField): Unit = activeSearchTextField.set(textField)



  /**
   * CTRL-F (windows) or META/COMMAND-F (mac)
   *
   * sets focus to search textfield
   * */
  val shortCutF = new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN)

  /**
   * Installs accelerators to the scene.
   *
   * The application is responsible to set the correct 'active' ui elements such that keybindings work as expected.
   *
   * For example, if the user switch between different tabs it is expected to set focus on the correct search field.
   * */
  def initAccelerators(scene: Scene): Unit =

    scene.getAccelerators.put(shortCutF, () => {
      Option(getActiveSearchTextField) match {
        case Some(tf) => tf.requestFocus()
        case None => logTrace("no textfield active")
      }
    })


