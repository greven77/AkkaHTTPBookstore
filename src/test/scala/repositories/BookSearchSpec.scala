package repositories

import helpers.BookSpecHelper
import java.sql.Date
import models.{Category, BookSearch}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, MustMatchers}
import repository.{BookRepository, CategoryRepository}
import scala.concurrent.Future
import services.{ConfigService, FlywayService, MySQLService}

class BookSearchSpec extends AsyncWordSpec
    with MustMatchers
    with BeforeAndAfterAll
    with ConfigService {

  /*
   Instantiate our service in charge of migrating our schema
   We need to make sure it exists before all our tests are ran,
   and we also need to make sure our schema is destroyed after all tests are ran,
   because we need to always start from a clean slate.
   */
  val flywayService = new FlywayService(jdbcUrl, dbUser, dbPassword)

  // We need a service that provides us access to our database
  val databaseService = new MySQLService(jdbcUrl, dbUser, dbPassword)

  // We need access to our "categories" table, to provide our books with their dependencies
  val categoryRepository = new CategoryRepository(databaseService)

  // We need access to our "books" table
  val bookRepository = new BookRepository(databaseService)

  // Our class for book-related helper methods
  val bookSpecHelper = new BookSpecHelper(categoryRepository)(bookRepository)

  val bookFields = bookSpecHelper.bookFields
  val sciFiCategory = bookSpecHelper.sciFiCategory


  override def beforeAll {
    // Let's make sure our schema is created
    flywayService.migrateDatabase

    bookSpecHelper.bulkInsert
  }

  override def afterAll {
    bookSpecHelper.bulkDelete
    // Let's make sure our schema is dropped
    flywayService.dropDatabase
  }

  "Performing a BookSearch" must {

    "return an empty list if there are no matches" in {
      val bookSearch = BookSearch(title = Some("Non existent book"))
      bookRepository.search(bookSearch).map { books =>
        books.size mustBe 0
      }
    }

    "return the matching books by title" in {
      val bookSearch = BookSearch(title = Some("Akka"))
      bookRepository.search(bookSearch).map { books =>
        books.size mustBe 1
        books.head.title mustBe bookFields.head._1
      }

      val bookSearchMultiple = BookSearch(title = Some("The"))
      bookRepository.search(bookSearchMultiple).map { books =>
        books.size mustBe 2
      }
    }

    "return the books by release date" in {
      val bookSearch = BookSearch(releaseDate = Some(Date.valueOf("1993-01-01")))
      bookRepository.search(bookSearch).map { books =>
        books.size mustBe 1
        books.head.title mustBe bookFields(2)._1
      }
    }

    "return the books by category" in {
      for {
        Some(category) <- categoryRepository.findByTitle(sciFiCategory.title)
        books <- bookRepository.search(BookSearch(categoryId = category.id))
      } yield books.size mustBe 3
    }

    "return the books by author" in {
      val bookSearch = BookSearch(author = Some(". We"))
      bookRepository.search(bookSearch).map { books =>
        books.size mustBe 2
      }
    }

    "return correctly the expected books when combining searches" in {
      for {
        Some(category) <- categoryRepository.findByTitle(sciFiCategory.title)
        books <- bookRepository.search(BookSearch(categoryId = category.id, title = Some("Scala")))
      } yield books.size mustBe 0

      val bookSearch = BookSearch(author = Some("H.G."), title = Some("The"))
      bookRepository.search(bookSearch).map { books =>
        books.size mustBe 2
      }
    }
   }
  }
