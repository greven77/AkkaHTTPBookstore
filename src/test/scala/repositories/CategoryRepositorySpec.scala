package repositories

import models.Category
import org.scalatest.{Assertion, AsyncWordSpec, BeforeAndAfterAll, MustMatchers}
import repository.CategoryRepository
import services.{ConfigService, FlywayService, MySQLService}
// We import the global execution context for our database service
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// We use AsyncWordSpec to be able to test Future[Assertion]
class CategoryRepositorySpec extends AsyncWordSpec
    with MustMatchers
    with BeforeAndAfterAll
    with ConfigService {

  /*
   Instantiate our service in charge of migrating our schema.
   We need to make sure it exists before all our tests are ran,
   and we also need to make sure our schema is destroyed after all tests are ran,
   because we need to always start from a clean slate.
   */
  val flywayService = new FlywayService(jdbcUrl, dbUser, dbPassword)

  // We need a service that provides us access to our database
  val databaseService = new MySQLService(jdbcUrl, dbUser, dbPassword)

  // We need access to our "categories" table
  val categoryRepository = new CategoryRepository(databaseService)

  // A test category to reuse
  val category = Category(None, "Test category")

  override def beforeAll {
    // Let's make sure our schema is created
    flywayService.migrateDatabase
  }

  override def afterAll {
    // Let's make sure our schema is dropped
    flywayService.dropDatabase
  }

  // Helper function to create a category, do some assertions to it, and then we delete it
  def createAndDelete(category: Category = category)(assertion: Category => Future[Assertion]):
      Future[Assertion] = {
    categoryRepository.create(category).flatMap { c =>
      val assertions = assertion(c)
      categoryRepository.delete(c.id.get) flatMap { _ => assertions }
    }
  }

  "A CategoryRepository" must {

    "be empty at the beginning" in {
      categoryRepository.all map { cs => cs.size mustBe 0 }
    }

    "create valid categories" in {

      createAndDelete() { c =>
        c.id mustBe defined
        categoryRepository.all map { cs => cs.size mustBe 1 }
      }
    }

    "not find a category by title if it doesn't exist" in {
      categoryRepository.findByTitle("not a valid title") map { c => c must not be defined }
    }

    "find a category by title if it exists" in {
      createAndDelete() { c =>
        categoryRepository.findByTitle(c.title) map { c => c mustBe defined }
      }
    }

    "delete a category by id if it exists" in {
      categoryRepository.create(category) flatMap { c =>
        categoryRepository.delete(c.id.get) flatMap { _ =>
          categoryRepository.all.map { cs => cs.size mustBe 0 }
        }
      }
    }
  }
}
