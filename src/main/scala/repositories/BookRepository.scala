package repository

import models._
import services.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

class BookRepository(val databaseService: DatabaseService)(implicit executor: ExecutionContext)
    extends BookTable {

  import databaseService._
  import databaseService.driver.api._

  // Here we just return the whole table query
  def all: Future[Seq[Book]] = db.run(books.result)

  // We add the book to our existing table query
  def create(book: Book): Future[Book] = {
    //db.run(books returning books += book)
    val booksReturningRow =
      books returning books.map(_.id) into { (b, id) =>
        b.copy(id = id)
      }
    db.run(booksReturningRow += book)
  }

  // Here we just filter our table query by id
  def findById(id: Long): Future[Option[Book]] = db.run(books.filter(_.id === id).result.headOption)

  // Here we find the respective book and then we delete it
  def delete(id: Long): Future[Int] = db.run(books.filter(_.id === id).delete)
}
