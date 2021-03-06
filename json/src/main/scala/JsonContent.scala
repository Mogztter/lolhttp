package lol.json

import lol.http._

import fs2.{ Task }

import scala.io.{ Codec }

import io.circe.{ Json }
import io.circe.parser.{ parse }

/** Provides [[lol.http.ContentEncoder ContentEncoder]] and [[lol.http.ContentDecoder ContentDecoder]]
  * for `io.circe.Json` values.
  */
object JsonContent {

  /** Creates JSON encoders.
    * @param codec the charset to use to read the JSON text. Default to `UTF-8`.
    * @return a [[lol.http.ContentEncoder ContentEncoder]] for `io.circe.JSON`.
    */
  def encoder(codec: Codec = Codec.UTF8): ContentEncoder[Json] = new ContentEncoder[Json] {
    def apply(data: Json) = ContentEncoder.text()(data.toString).addHeaders(
      Headers.ContentType -> h"application/json; charset=$codec"
    )
  }

  /** Creates JSON decoders.
    * @param maxSize the maximum amout of byte to read in memory.
    * @param codec the charset to use to read the JSON text. Default to `UTF-8`.
    * @return a [[lol.http.ContentDecoder ContentDecoder]] for `io.circe.JSON`.
    */
  def decoder(maxSize: Int = ContentDecoder.MaxSize, codec: Codec = Codec.UTF8) = new ContentDecoder[Json] {
    def apply(content: Content) = {
      ContentDecoder.text()(content).flatMap { text =>
        parse(text).fold(Task.fail, Task.now)
      }
    }
  }

}
