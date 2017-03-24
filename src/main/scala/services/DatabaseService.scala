package services

import slick.driver.JdbcProfile
//import slick.driver.PostgresDriver.api._
import slick.driver.MySQLDriver.api._

trait DatabaseService {

  val driver: JdbcProfile
  val db: Database
}
