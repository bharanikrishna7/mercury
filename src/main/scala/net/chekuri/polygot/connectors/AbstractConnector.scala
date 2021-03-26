package net.chekuri.polygot.connectors

import com.typesafe.scalalogging.LazyLogging
import net.chekuri.polygot.connectors.PolygotConnectorConstants.ConnectorType
import net.chekuri.polygot.connectors.PolygotConnectorModels.{
  ConnectionResultColumn,
  ConnectorArguments,
  DecoratedConnectionResults,
  JdbcConnectionResults
}
import net.chekuri.polygot.utilities.BenchmarkUtilities
import java.sql.{Connection, DatabaseMetaData, ResultSet, Statement}
import java.util.UUID

/** Polygot Connector template/base class
  * which will contain all the common methods
  * used by various derived Polygot JDBC connectors.
  * @param connector connector type.
  * @param arguments connection arguments.
  */
abstract class AbstractConnector(
    connector: ConnectorType,
    arguments: ConnectorArguments
) extends LazyLogging {
  logger.debug(
    s"Initializing `${connector.name}` Connector on thread : ${Thread.currentThread().getId}"
  )
  logger.trace(s"Driver Name : ${connector.driver}")

  val uuid: UUID = UUID.randomUUID()
  logger.debug(s"Connector's Unique Identifier: ${uuid.toString}")

  private def resultset_processor = new ConnectionResultSetProcessor

  // this ensures that driver is present and appropriately
  // loaded when the class is initialized.
  Class.forName(connector.driver)

  def getName: String = connector.name

  def getDriver: String = connector.driver

  /** Abstract method which will be used to
    * define how connection for each Polygot
    * JDBC Connector is prepared.
    * @return java sql connection object.
    */
  protected def prepare: Connection

  /** Method to select catalog information
    * associated with the java.sql.Connection
    * generated from connection type and arguments.
    * @return Jdbc Connection Result object containing catalog information.
    */
  def fetchCatalog: PolygotConnectorModels.JdbcConnectionResults = {
    logger.trace("Prepare connection...")
    val benchmarked_connection = BenchmarkUtilities.run(prepare)
    val connection: Connection = benchmarked_connection.result
    val connection_init_duration = benchmarked_connection.exec_time_in_nano
    val benchmarked_metadata = BenchmarkUtilities.run(connection.getMetaData)
    val metadata_exec_duration = benchmarked_metadata.exec_time_in_nano
    val metadata: DatabaseMetaData = benchmarked_metadata.result
    val benchmarked_schemas = BenchmarkUtilities.run(metadata.getCatalogs)
    val schemas: ResultSet = benchmarked_schemas.result
    val schema_fetch_duration = benchmarked_schemas.exec_time_in_nano

    val processed = resultset_processor.extract(schemas)
    connection.close()
    JdbcConnectionResults(
      columns = processed.columns,
      records = processed.records,
      uuid = this.uuid.toString,
      connection_init_duration = connection_init_duration,
      query_execution_duration = metadata_exec_duration + schema_fetch_duration,
      metadata_parse_duration = processed.metadata_parse_duration,
      resultset_fetch_duration = processed.resultset_fetch_duration
    )
  }

  /** Method to fetch tables. Users can filter the table
    * list using one or more of the parameters.
    *
    * If no parameter is supplied, it'll return all tables
    * which can be seen using supplied credentials privileges.
    * @param catalog catalog name
    * @param schemaPattern schema pattern
    * @param tableNamePattern table name pattern
    * @param tableTypes table types array (please refer to result set
    *                    since different sql dialects can have different
    *                    table types).
    * @return List of tables present in connection satisfying supplied filter
    *         conditions.
    */
  def fetchTables(
      catalog: Option[String],
      schemaPattern: Option[String],
      tableNamePattern: Option[String],
      tableTypes: Option[Array[String]]
  ): PolygotConnectorModels.JdbcConnectionResults = {
    if (
      catalog.isEmpty && schemaPattern.isEmpty && tableNamePattern.isEmpty && tableTypes.isEmpty
    ) {
      logger.warn(
        "No filtering mechanism supplied while fetching table list...."
      )
      logger.warn(
        "This can be network intensive process if there are lot of tables..."
      )
      logger.warn(
        "Please use this method without filter(s) at your own risk..."
      )
    }

    logger.trace("Prepare connection...")
    val benchmarked_connection = BenchmarkUtilities.run(prepare)
    val connection: Connection = benchmarked_connection.result
    val connection_init_duration = benchmarked_connection.exec_time_in_nano
    val benchmarked_metadata = BenchmarkUtilities.run(connection.getMetaData)
    val metadata_exec_duration = benchmarked_metadata.exec_time_in_nano
    val metadata: DatabaseMetaData = benchmarked_metadata.result
    val benchmarked_tables =
      BenchmarkUtilities.run(
        metadata.getTables(
          catalog.orNull,
          schemaPattern.orNull,
          tableNamePattern.orNull,
          tableTypes.orNull
        )
      )
    val tables: ResultSet = benchmarked_tables.result
    val tables_fetch_duration = benchmarked_tables.exec_time_in_nano

    val processed = resultset_processor.extract(tables)
    connection.close()
    JdbcConnectionResults(
      columns = processed.columns,
      records = processed.records,
      uuid = this.uuid.toString,
      connection_init_duration = connection_init_duration,
      query_execution_duration = metadata_exec_duration + tables_fetch_duration,
      metadata_parse_duration = processed.metadata_parse_duration,
      resultset_fetch_duration = processed.resultset_fetch_duration
    )
  }

  /** Method to fetch tables using catalog information.
    * @param catalog catalog name.
    * @return Jdbc Connection Result object containing tables associated with supplied catalog.
    */
  def fetchTablesByCatalog(
      catalog: String
  ): PolygotConnectorModels.JdbcConnectionResults =
    this.fetchTables(catalog = Some(catalog), None, None, None)

  /** Method to fetch tables using catalog information.
    * @param schemaPattern schema pattern (similar to regex in queries).
    * @return Jdbc Connection Result object containing tables associated matching supplied schema pattern.
    */
  def fetchTablesBySchemaPattern(
      schemaPattern: String
  ): PolygotConnectorModels.JdbcConnectionResults =
    this.fetchTables(None, schemaPattern = Some(schemaPattern), None, None)

  /** Method to fetch columns. Users can filter the column
    * list using one or more of the parameters.
    *
    * If no parameter is supplied, it'll return all columns
    * which can be seen using supplied credentials privileges.
    * @param catalog catalog name
    * @param schemaPattern schema pattern
    * @param tableNamePattern table name pattern
    * @param columnNamePattern column name pattern
    * @return List of columns present in connection satisfying supplied filter
    *         conditions.
    */
  def fetchColumns(
      catalog: Option[String],
      schemaPattern: Option[String],
      tableNamePattern: Option[String],
      columnNamePattern: Option[String]
  ): PolygotConnectorModels.JdbcConnectionResults = {
    if (
      catalog.isEmpty && schemaPattern.isEmpty && tableNamePattern.isEmpty && columnNamePattern.isEmpty
    ) {
      logger.warn(
        "No filtering mechanism supplied while fetching table list...."
      )
      logger.warn(
        "This can be network intensive process if there are lot of tables..."
      )
      logger.warn(
        "Please use this method without filter(s) at your own risk..."
      )
    }

    logger.trace("Prepare connection...")
    val benchmarked_connection = BenchmarkUtilities.run(prepare)
    val connection: Connection = benchmarked_connection.result
    val connection_init_duration = benchmarked_connection.exec_time_in_nano
    val benchmarked_metadata = BenchmarkUtilities.run(connection.getMetaData)
    val metadata_exec_duration = benchmarked_metadata.exec_time_in_nano
    val metadata: DatabaseMetaData = benchmarked_metadata.result
    val benchmarked_columns =
      BenchmarkUtilities.run(
        metadata.getColumns(
          catalog.orNull,
          schemaPattern.orNull,
          tableNamePattern.orNull,
          columnNamePattern.orNull
        )
      )
    val columns: ResultSet = benchmarked_columns.result
    val columns_fetch_duration = benchmarked_columns.exec_time_in_nano

    val processed = resultset_processor.extract(columns)
    connection.close()
    JdbcConnectionResults(
      columns = processed.columns,
      records = processed.records,
      uuid = this.uuid.toString,
      connection_init_duration = connection_init_duration,
      query_execution_duration =
        metadata_exec_duration + columns_fetch_duration,
      metadata_parse_duration = processed.metadata_parse_duration,
      resultset_fetch_duration = processed.resultset_fetch_duration
    )
  }

  /** Method to fetch columns using catalog information.
    * @param catalog catalog name.
    * @return Jdbc Connection Result object containing columns associated with supplied catalog.
    */
  def fetchColumnsByCatalog(catalog: String): JdbcConnectionResults =
    this.fetchColumns(catalog = Some(catalog), None, None, None)

  /** Method to execute select query using the java.sql.Connection
    * generated from connection type and arguments.
    * @param query select query (must be ASCII escaped).
    * @return Jdbc Connection Result object containing query results.
    */
  def select(query: String): PolygotConnectorModels.JdbcConnectionResults = {
    logger.trace("Prepare connection...")
    val benchmarked_connection = BenchmarkUtilities.run(prepare)
    val connection: Connection = benchmarked_connection.result
    val connection_init_duration = benchmarked_connection.exec_time_in_nano
    val benchmarked_statement_generation =
      BenchmarkUtilities.run(connection.createStatement())
    val statement_generation_duration =
      benchmarked_statement_generation.exec_time_in_nano
    val statement: Statement = benchmarked_statement_generation.result
    val benchmarked_query_exec =
      BenchmarkUtilities.run(statement.executeQuery(query))
    val resultset: ResultSet = benchmarked_query_exec.result
    val query_exec_duration = benchmarked_query_exec.exec_time_in_nano

    val processed = resultset_processor.extract(resultset)
    connection.close()
    JdbcConnectionResults(
      columns = processed.columns,
      records = processed.records,
      uuid = this.uuid.toString,
      connection_init_duration = connection_init_duration,
      query_execution_duration =
        statement_generation_duration + query_exec_duration,
      metadata_parse_duration = processed.metadata_parse_duration,
      resultset_fetch_duration = processed.resultset_fetch_duration
    )
  }

  /** Method to execute non-select query using the java.sql.Connection
    * generated from connection type and arguments.
    * @param query update | insert | create | set | delete query (must be ASCII escaped).
    * @return Jdbc Connection Result object containing number of entities updated | created.
    */
  def update(query: String): PolygotConnectorModels.JdbcConnectionResults = {
    logger.trace("Prepare connection...")
    val benchmarked_connection = BenchmarkUtilities.run(prepare)
    val connection: Connection = benchmarked_connection.result
    val connection_init_duration = benchmarked_connection.exec_time_in_nano
    val benchmarked_statement_generation =
      BenchmarkUtilities.run(connection.createStatement())
    val statement_generation_duration =
      benchmarked_statement_generation.exec_time_in_nano
    val statement: Statement = benchmarked_statement_generation.result
    val benchmarked_query_exec =
      BenchmarkUtilities.run(statement.executeLargeUpdate(query))
    val resultset: Long = benchmarked_query_exec.result
    val query_exec_duration = benchmarked_query_exec.exec_time_in_nano
    val processed = DecoratedConnectionResults(
      columns = List[ConnectionResultColumn](
        ConnectionResultColumn("update_count", "long", java.sql.Types.NUMERIC)
      ),
      records = List[List[String]](List[String](resultset.toString)),
      metadata_parse_duration = 0,
      resultset_fetch_duration = 0
    )
    connection.close()
    JdbcConnectionResults(
      columns = processed.columns,
      records = processed.records,
      uuid = this.uuid.toString,
      connection_init_duration = connection_init_duration,
      query_execution_duration =
        statement_generation_duration + query_exec_duration,
      metadata_parse_duration = processed.metadata_parse_duration,
      resultset_fetch_duration = processed.resultset_fetch_duration
    )
  }
}
