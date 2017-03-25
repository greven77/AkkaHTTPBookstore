import org.scalatest.Sequential
import repositories._

class DatabaseSuite extends Sequential(
  new CategoryRepositorySpec,
  new BookRepositorySpec,
  new BookSearchSpec,
  new BookEndpointSpec,
  new CategoryEndpointSpec
) {

}
