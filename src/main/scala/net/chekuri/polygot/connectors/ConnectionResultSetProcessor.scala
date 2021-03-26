package net.chekuri.polygot.connectors

import com.typesafe.scalalogging.LazyLogging
import net.chekuri.polygot.connectors.PolygotConnectorModels.{
  ConnectionResultColumn,
  ConnectionResults,
  DecoratedConnectionResults
}
import net.chekuri.polygot.utilities.BenchmarkUtilities

import java.sql.{ResultSet, ResultSetMetaData}

class ConnectionResultSetProcessor extends LazyLogging {
  def extract(resultset: ResultSet): DecoratedConnectionResults = {
    val metadata = resultset.getMetaData
    val benchmarked_column_extract =
      BenchmarkUtilities.run(this.extractResultSetMetadata(metadata))
    val column_info_extraction_time =
      benchmarked_column_extract.exec_time_in_nano
    val columns = benchmarked_column_extract.result
    val benchmarked_record_extract =
      BenchmarkUtilities.run(this.extractRecords(columns, resultset))
    val record_extract_time = benchmarked_record_extract.exec_time_in_nano
    val records = benchmarked_record_extract.result
    DecoratedConnectionResults(
      columns = columns,
      records = records.records,
      metadata_parse_duration = column_info_extraction_time,
      resultset_fetch_duration = record_extract_time
    )
  }

  protected def extractRecords(
      columns: List[ConnectionResultColumn],
      resultset: ResultSet
  ): ConnectionResults = {
    var rows: List[List[String]] = List[List[String]]()
    while (resultset.next()) {
      var row: List[String] = List[String]()
      for (col <- columns) {
        row = resultset.getString(col.name) :: row
      }
      rows = row.reverse :: rows
    }
    ConnectionResults(columns = columns, records = rows.reverse)
  }

  protected def extractResultSetMetadata(
      metadata: ResultSetMetaData
  ): List[ConnectionResultColumn] = {
    var columns: List[ConnectionResultColumn] = List[ConnectionResultColumn]()
    val count = metadata.getColumnCount
    for (index <- 1 to count) {
      columns = ConnectionResultColumn(
        name = metadata.getColumnName(index),
        `type` = metadata.getColumnTypeName(index),
        type_id = metadata.getColumnType(index)
      ) :: columns
    }
    columns.reverse
  }
}
