package net.chekuri.polygot.connectors

import com.typesafe.scalalogging.LazyLogging

object PolygotConnectorConstants extends LazyLogging {

  case class ConnectorType(name: String, driver: String)

  val MySqlConnectorType: ConnectorType =
    ConnectorType(name = "mysql", driver = "com.mysql.cj.jdbc.Driver")

  val ConnectorTypes: Set[ConnectorType] = Set[ConnectorType](
    MySqlConnectorType
  )
}
