/*
rule = SlickFilter
 */
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
    query.filter { x =>
      a1 match {
        case Some(value) =>
          x.a1 === value
        case None =>
          true: Rep[Boolean]
      }
    }.result

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
    query.filter { x =>
      a1 match {
        case Some(value) =>
          x.a1 === value
        case _ =>
          true: Rep[Boolean]
      }
    }.result

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
    query.filter { x =>
      (a1 match {
        case Some(value) =>
          x.a1 === value
        case None =>
          true: Rep[Boolean]
      }) && (
        a2 match {
          case Some(value) =>
            x.a2 === value
          case _ =>
            true: Rep[Boolean]
        }
      ) && (
        if (a4 && Nil.nonEmpty) {
          x.a1 === 5
        } else {
          true: Rep[Boolean]
        }
      ) && (
        if (a4 && Nil.isEmpty)
          true: Rep[Boolean]
        else
          x.a1 === 8
      ) && (
        a3 match {
          case Some(value) =>
            x.a3 === value
          case _ =>
            true: Rep[Boolean]
        }
      )
    }.result

  def f6(a1: Option[Int], a2: String, a3: Option[java.sql.Timestamp]): DBIO[Seq[X]] =
    query.filter { x =>
      (a1 match {
        case Some(value) =>
          x.a1 === value
        case _ =>
          true: Rep[Boolean]
      }) && (
        x.a2 === a2
      ) && (
        a3 match {
          case Some(value) =>
            x.a3 === value
          case None =>
            true: Rep[Boolean]
        }
      )
    }.result

  def f7(a1: Int, a2: Option[String], a3: java.sql.Timestamp): DBIO[Seq[X]] =
    query
      .filter(x =>
        (
          x.a1 === a1
        ) && (
          a2 match {
            case Some(value) =>
              x.a2 === value
            case _ =>
              true: Rep[Boolean]
          }
        ) && (
          x.a3 === a3
        )
      )
      .result

  def f8(a1: Option[Int], a2: Option[String], a3: Option[java.sql.Timestamp]): DBIO[Seq[(X, X)]] =
    query
      .join(query)
      .filter { case (x1, x2) =>
        (a1 match {
          case Some(value) =>
            x1.a1 === value
          case None =>
            true: Rep[Boolean]
        }) && (
          a2 match {
            case Some(value) =>
              x2.a2 === value
            case _ =>
              true: Rep[Boolean]
          }
        ) && (
          a3 match {
            case Some(value) =>
              x1.a3 === value
            case _ =>
              false: Rep[Boolean]
          }
        )
      }
      .result

}
