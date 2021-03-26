package net.chekuri.polygot.utilities

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.flatspec.AnyFlatSpec

class BenchmarkUtilitiesSpec extends AnyFlatSpec with LazyLogging {
  def delayedProduct(num1: Long, num2: Long, sleep_in_sec: Int = 1): BigInt = {
    Thread.sleep(sleep_in_sec * 1000)
    BigInt.apply(num1).*(num2)
  }

  "run" should "correctly execute the task | function and return results with appropriate execution duration" in {
    val num1: Long = 1
    val num2: Long = 2
    val expected: Long = 2
    val expected_exec_time_in_nanos: Long = 1000000000
    val benchmarked_actual =
      BenchmarkUtilities.run(this.delayedProduct(num1, num2, 1))
    val actual = benchmarked_actual.result
    val actual_exec_time_in_nanos = benchmarked_actual.exec_time_in_nano
    logger.debug("Comparing actual and expected...")
    logger.info(s"Actual   : $actual")
    logger.info(s"Expected : $expected")
    assert(actual.toLong == expected)
    logger.info(s"Execution time should be greater than sleep duration...")
    logger.info(s"Actual exec in nanos  : $actual_exec_time_in_nanos")
    logger.info(s"Expected exec in nanos: >$expected_exec_time_in_nanos")
    assert(actual_exec_time_in_nanos > expected_exec_time_in_nanos)
  }
}
