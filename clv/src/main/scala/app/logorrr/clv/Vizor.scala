package app.logorrr.clv

trait Vizor[A]:

  /** returns true if entry is active (= selected) - typically this entry is highlighted in some form */
  def isSelected(a: A): Boolean

  /** element is the first visible element in the text view (the start of the visible elements) */
  def isFirstVisible(a: A): Boolean

  /** element is the last visible element in the text view (the end of the visible elements) */
  def isLastVisible(a: A): Boolean

  /** element is visible in the text view */
  def isVisibleInTextView(a: A): Boolean

  def isVisible(a: A): Boolean = isFirstVisible(a) || isLastVisible(a) || isVisibleInTextView(a)

