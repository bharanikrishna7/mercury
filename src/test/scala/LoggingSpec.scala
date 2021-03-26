import com.typesafe.scalalogging.LazyLogging
import org.scalatest.flatspec.AnyFlatSpec

class LoggingSpec extends AnyFlatSpec with LazyLogging {

  private val msg: String = "LoggingSpec Test msg..."

  "info" should "correctly log info message to console" in {
    logger.info(msg)
  }

  "debug" should "correctly log debug message to console" in {
    logger.debug(msg)
  }

  "trace" should "correctly log trace message to console" in {
    logger.trace(msg)
  }

  "warn" should "correctly log warn message to console" in {
    logger.warn(msg)
  }

  "error" should "correctly log trace message to console" in {
    logger.error(msg)
  }
}
