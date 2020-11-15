package net.chekuri.mercury.state

import net.chekuri.mercury.TokenizerContext

class EndTokenizerState extends TokenizerState {
  def this(context: TokenizerContext) = {
    this()
    this.context = context
  }

  override def eatChars: Unit = {
    context.token = "\0"
    logger.debug("mercury reached end of stream, nothing more to consume.")
  }

  override var context: TokenizerContext = _
}
