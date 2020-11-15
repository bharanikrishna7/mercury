package net.chekuri.mercury.state
import net.chekuri.mercury.TokenizerContext

class BeginTokenizerState extends TokenizerState {
  def this(context: TokenizerContext) = {
    this()
    this.context = context
  }

  override def eatChars: Unit = {
    context.token = ""
    logger.debug("mercury tokenizer state successfully initialized.")
  }

  override var context: TokenizerContext = _
}
