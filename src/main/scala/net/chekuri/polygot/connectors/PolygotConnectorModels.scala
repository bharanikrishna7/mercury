package net.chekuri.polygot.connectors

object PolygotConnectorModels {

  case class ConnectorArguments(
      host: Option[String],
      port: Option[Int],
      database: Option[String],
      user: Option[String],
      pass: Option[String],
      jdbc_props: Option[Map[String, AnyRef]]
  )

  case class ConnectionResultColumn(
      name: String,
      `type`: String,
      type_id: Int
  )

  case class ConnectionResults(
      columns: List[ConnectionResultColumn],
      records: List[List[String]]
  )

  case class DecoratedConnectionResults(
      columns: List[ConnectionResultColumn],
      records: List[List[String]],
      metadata_parse_duration: Long,
      resultset_fetch_duration: Long
  )

  case class JdbcConnectionResults(
      columns: List[ConnectionResultColumn],
      records: List[List[String]],
      uuid: String,
      connection_init_duration: Long,
      query_execution_duration: Long,
      metadata_parse_duration: Long,
      resultset_fetch_duration: Long
  ) {
    def toString(limit: Boolean = false): String = {
      val aggregator = new StringBuffer()
      val col_count = columns.size
      val index_limit = if (limit) { Math.min(records.length, 10) }
      else records.length
      for (record_index <- 0 until index_limit) {
        val record = records(record_index)
        aggregator.append('\n')
        aggregator.append(s" Record Index : #$record_index\n")
        aggregator.append(s"---------------------\n")
        for (index <- 0 until col_count) {
          aggregator.append(
            s"${columns(index).name} [${columns(index).`type`}] : ${record(index)}\n"
          )
        }
      }
      aggregator.toString
    }

    override def toString: String = this.toString(limit = false)
  }
}
