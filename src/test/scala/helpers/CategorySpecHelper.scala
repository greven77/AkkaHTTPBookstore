package helpers

import models.Category
import org.scalatest._
import repository.CategoryRepository
import scala.concurrent.ExecutionContext

import scala.concurrent.Future

class CategorySpecHelper(categoryRepository: CategoryRepository)
  (implicit executor: ExecutionContext) {

  // A test category to reuse
  val category = Category(None, "Test category")

  // Helper function to create a category, do some assertions to it, and then we delete it
  def createAndDelete[T](category: Category = category)(assertion: Category => Future[T]) = {
    categoryRepository.create(category) flatMap { c =>
      val assertions = assertion(c)
      categoryRepository.delete(c.id.get) flatMap { _ => assertions }
    }
  }
}
