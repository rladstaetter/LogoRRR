package app.logorrr.clv.color

/**
 * Groups colors of boxes/rectangles together
 *
 * @param color           color of given block
 * @param upperBorderCol  color of upper border
 * @param leftBorderCol   color of left border
 * @param bottomBorderCol color of bottom border
 * @param rightBorderCol  color of right border@param color
 */
case class BlockColor(color: Int
                      , upperBorderCol: Int
                      , leftBorderCol: Int
                      , bottomBorderCol: Int
                      , rightBorderCol: Int)
