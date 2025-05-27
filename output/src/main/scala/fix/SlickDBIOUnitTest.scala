package fix

import slick.dbio.DBIO

class SlickDBIOUnitTest {
  def f1: DBIO[Unit] = DBIO.unit
}
