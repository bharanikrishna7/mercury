package net.chekuri.polygot.connectors

import com.typesafe.scalalogging.LazyLogging
import net.chekuri.polygot.connectors.PolygotConnectorModels.ConnectorArguments
import org.scalatest.flatspec.AnyFlatSpec

class MySqlConnectorSpec extends AnyFlatSpec with LazyLogging {
  private val args = ConnectorArguments(
    host = Some("mysql-rfam-public.ebi.ac.uk"),
    port = Some(4497),
    database = Some("Rfam"),
    user = Some("rfamro"),
    pass = None,
    jdbc_props = None
  )

  "select" should "correctly execute query and return resultset" in {
    val query = "SELECT * FROM Rfam.clan LIMIT 10;"
    val connection = new MySqlConnector(arguments = args)
    val exec = connection.select(query = query)
    logger.info(exec.toString)
  }

  "fetchCatalog" should "correctly retrieve schema information" in {
    val connection = new MySqlConnector(arguments = args)
    val exec = connection.fetchCatalog
    logger.info(exec.toString)
  }

  "update" should "correctly execute update query" in {
    val query = "SET @name = 43;"
    val connection = new MySqlConnector(arguments = args)
    val exec = connection.update(query = query)
    logger.info(exec.toString)
  }

  "fetchTablesByCatalog" should "correctly retrieve tables by catalog name" in {
    val connection = new MySqlConnector(arguments = args)
    val tables = connection.fetchTablesByCatalog("Rfam")
    logger.info(tables.toString)
  }

  "fetchTables" should "correctly retrieve tables when no filter is supplied" in {
    val connection = new MySqlConnector(arguments = args)
    val tables = connection.fetchTables(None, None, None, None)
    logger.info(tables.toString)
  }

  "fetchColumnsByCatalog" should "correctly retrieve columns by catalog name" in {
    val connection = new MySqlConnector(arguments = args)
    val columns = connection.fetchColumnsByCatalog("Rfam")
    logger.info(columns.toString)
  }

  "fetchColumns" should "correctly retrieve columns when no filter is supplied" in {
    val connection = new MySqlConnector(arguments = args)
    val columns = connection.fetchColumns(None, None, None, None)
    logger.info(columns.toString)
  }
}
