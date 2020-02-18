package com.coinzway.coinz4s.btc

import ClientObjects.RawTransactionInput
import spray.json._

object HttpParamsConverter extends DefaultJsonProtocol {
  implicit val RawTransactionInputFormat: RootJsonFormat[RawTransactionInput] = jsonFormat3(RawTransactionInput)

  def rpcParamsToJson(params: Vector[Any]): Vector[String] = params.map {
    case param: Int                                => param.toString
    case param: BigDecimal                         => param.toString
    case param: String                             => "\"" + param + "\""
    case param: Boolean                            => param.toString
    case param: ClientObjects.Recipients           => param.value.toJson.compactPrint
    case param: ClientObjects.RawTransactionInputs => param.inputs.toJson.compactPrint
  }
}
