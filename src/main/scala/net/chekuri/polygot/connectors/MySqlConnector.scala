package net.chekuri.polygot.connectors

import net.chekuri.polygot.connectors.PolygotConnectorModels.ConnectorArguments

import java.sql.{Connection, DriverManager}
import java.util.Properties

class MySqlConnector(arguments: ConnectorArguments)
    extends AbstractConnector(
      PolygotConnectorConstants.MySqlConnectorType,
      arguments
    ) {

  /** Method to prepare jdbc connection.
    *
    * @return jdbc connection.
    */
  override protected def prepare: Connection = {
    try {
      Class.forName(this.getDriver)
      if (arguments.user.isEmpty && arguments.pass.isDefined) {
        throw new IllegalArgumentException(
          "Supplied password without username. Username might be missing..."
        )
      }

      if (arguments.host.isEmpty) {
        throw new IllegalArgumentException("JDBC Host Cannot be empty.")
      }

      if (arguments.port.isEmpty) {
        throw new IllegalArgumentException("JDBC Port Cannot be empty.")
      }

      var connection_string =
        s"jdbc:mysql://${arguments.host.get}:${arguments.port.get}"

      if (arguments.database.isDefined) {
        connection_string = s"${connection_string}/${arguments.database.get}"
      }

      DriverManager.getConnection(connection_string, generateProps)
    } catch {
      case ex: Exception =>
        logger.warn(
          s"Encountered exception while preparing new $getName connection."
        )
        logger.error("Actual exception.")
        throw ex
    }
  }

  /** Method to generate properties for jdbc connection.
    *
    * @return properties using class arguments.
    */
  private def generateProps: Properties = {
    val result: Properties = new Properties()
    if (arguments.user.isDefined) {
      result.put("user", arguments.user.get)
    }
    if (arguments.pass.isDefined) {
      result.put("password", arguments.pass.get)
    }
    result.put("useSSL", "true")
    if (arguments.jdbc_props.isDefined) {
      for ((k, v) <- arguments.jdbc_props.get) {
        result.put(k, v)
      }
    }
    result
  }
}
