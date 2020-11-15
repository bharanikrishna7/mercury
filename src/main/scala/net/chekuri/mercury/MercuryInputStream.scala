package net.chekuri.mercury

import java.io.InputStream

class MercuryInputStream extends InputStream {

  import java.io.IOException

  /** The underlying stream.
    */
  private var stream: InputStream = null

  /** Bytes that have been peeked at.
    */
  private var peekBytes: Array[Byte] = null

  /** How many bytes have been peeked at.
    */
  private var peekLength: Int = 0

  /** The constructor accepts an InputStream to setup the
    * object.
    *
    * @param is
    * The InputStream to parse.
    */
  def this(is: InputStream) {
    this()
    this.stream = is
    this.peekBytes = new Array[Byte](10)
    this.peekLength = 0
  }

  /** Peek at the next character from the stream.
    *
    * @return The next character.
    * @throws IOException
    * If an I/O exception occurs.
    */
  @throws[IOException]
  def peek: Int = peek(0)

  /** Peek at a specified depth.
    *
    * @param depth
    * The depth to check.
    * @return The character peeked at.
    * @throws IOException
    * If an I/O exception occurs.
    */
  @throws[IOException]
  def peek(depth: Int): Int = { // does the size of the peek buffer need to be extended?
    if (this.peekBytes.length <= depth) {
      val temp = new Array[Byte](depth + 10)
      for (i <- 0 until this.peekBytes.length) {
        temp(i) = this.peekBytes(i)
      }
      this.peekBytes = temp
    }
    // does more data need to be read?
    if (depth >= this.peekLength) {
      val offset = this.peekLength
      val length = (depth - this.peekLength) + 1
      val lengthRead = this.stream.read(this.peekBytes, offset, length)
      if (lengthRead == -1) return -1
      this.peekLength = depth + 1
    }
    this.peekBytes(depth)
  }

  /*
   * Read a single byte from the stream.
   *
   * @throws IOException
   * If an I/O exception occurs. @return The character that
   * was read from the stream.
   */
  @throws[IOException]
  def read(): Int = {
    if (this.peekLength == 0) return this.stream.read
    val result = this.peekBytes(0)
    this.peekLength -= 1
    for (i <- 0 until this.peekLength) {
      this.peekBytes(i) = this.peekBytes(i + 1)
    }
    result
  }
}
