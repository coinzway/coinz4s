package com.coinzway.coinz4s.core.rpc.estimatefee

import com.coinzway.coinz4s.core.rpc.estimatefee.EstimateFeeResponse.EstimateFee
import spray.json._

trait EstimateFeeRpcJsonFormats extends DefaultJsonProtocol {

  implicit object EstimateFeeFormat extends RootJsonReader[EstimateFee] {

    override def read(json: JsValue): EstimateFee = json match {
      case JsNumber(result) => EstimateFee(result)
      case x                => deserializationError("Expected EstimateFee as JsNumber, but got " + x)
    }
  }
}
