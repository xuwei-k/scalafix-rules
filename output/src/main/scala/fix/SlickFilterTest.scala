package fix

abstract class SlickFilterTest {

  case class X(a1: Int, a2: String, a3: java.sql.Timestamp)

  val profile: slick.jdbc.JdbcProfile

  import profile.api._

  abstract class MyTable(tag: Tag) extends Table[X](tag, "aaa") {
    val a1 = column[Int]("")
    val a2 = column[String]("")
    val a3 = column[java.sql.Timestamp]("")
  }

  def query: TableQuery[MyTable]

  def f1(a1: Option[Int]): DBIO[Seq[X]] =
    query.filterOpt(a1) { (x, value) => x.a1 === value }.result

  def f2(a1: Option[Int]): DBIO[Seq[X]] =
    query.filter { x =>
      a1 match {
        case Some(value) =>
          x.a1 === value
        case None =>
          false: Rep[Boolean]
      }
    }.result

  def f3(a1: Option[Int]): DBIO[Seq[X]] =
    query.filterOpt(a1) { (x, value) => x.a1 === value }.result

  def f4(a1: Option[Int]): DBIO[Seq[X]] =
    query.filter { x =>
      a1 match {
        case Some(value) if false =>
          x.a1 === value
        case _ =>
          true: Rep[Boolean]
      }
    }.result

  def f5(a1: Option[Int], a2: Option[String], a3: Option[java.sql.Timestamp], a4: Boolean): DBIO[Seq[X]] =
    query.filterOpt(a1) { (x, value) => x.a1 === value }.filterOpt(a2) { (x, value) => x.a2 === value }.filterIf(a4 && Nil.nonEmpty) { x => {
          x.a1 === 5
        } }.filterIf(!(a4 && Nil.isEmpty)) { x => x.a1 === 8 }.filterOpt(a3) { (x, value) => x.a3 === value }.result

  def f6(a1: Option[Int], a2: String, a3: Option[java.sql.Timestamp]): DBIO[Seq[X]] =
    query.filterOpt(a1) { (x, value) => x.a1 === value }.filter{ x => x.a2 === a2 }.filterOpt(a3) { (x, value) => x.a3 === value }.result

  def f7(a1: Int, a2: Option[String], a3: java.sql.Timestamp): DBIO[Seq[X]] =
    query.filter( x => x.a1 === a1 ).filterOpt(a2) { (x, value) => x.a2 === value }.filter( x => x.a3 === a3 )
      .result

  def f8(a1: Option[Int], a2: Option[String], a3: Option[java.sql.Timestamp]): DBIO[Seq[(X, X)]] =
    query
      .join(query).filterOpt(a1) { case ((x1, x2), value) => x1.a1 === value }.filterOpt(a2) { case ((x1, x2), value) => x2.a2 === value }.filter{ case (x1, x2) => a3 match {
            case Some(value) =>
              x1.a3 === value
            case _ =>
              false: Rep[Boolean]
          } }
      .result

}
