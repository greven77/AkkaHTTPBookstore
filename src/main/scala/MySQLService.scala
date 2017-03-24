package services

//import slick.driver.{JdbcProfile, PostgresDriver}
//import slick.driver.PostgresDriver.api._

import slick.driver.{JdbcProfile, MySQLDriver}
import slick.driver.MySQLDriver.api._

class MySQLService(jdbcUrl: String, dbUser: String, dbPassword: String) extends DatabaseService {

  // Setup our database driver, Postgres in this case
  //  val driver: JdbcProfile = PostgresDriver
  val driver: JdbcProfile = MySQLDriver

  // Create a database connection
  val db: Database = Database.forURL(jdbcUrl, dbUser, dbPassword)
  db.createSession()
}
