package net.chekuri.polygot.utilities

import com.typesafe.scalalogging.LazyLogging

object BenchmarkUtilities extends LazyLogging {

  case class BenchMarkResults[R](result: R, exec_time_in_nano: Long)

  /** Method to run benchmarks.
    *
    * @param block Code block to benchmark.
    * @tparam R code block's result type.
    * @return Returns Benchmark results.
    */
  def run[R](block: => R): BenchMarkResults[R] = {
    import java.lang.System.nanoTime

    val start_time: Long = nanoTime
    val result = block
    val stop_time: Long = nanoTime
    val execution_time: Long = stop_time - start_time
    BenchMarkResults(result, execution_time)
  }
}
