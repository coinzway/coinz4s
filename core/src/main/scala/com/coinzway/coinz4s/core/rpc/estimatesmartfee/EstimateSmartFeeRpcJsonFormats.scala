package com.coinzway.coinz4s.core.rpc.estimatesmartfee

import com.coinzway.coinz4s.core.rpc.estimatesmartfee.EstimateSmartFeeResponse.EstimateSmartFee
import spray.json._

trait EstimateSmartFeeRpcJsonFormats extends DefaultJsonProtocol {
  implicit val EstimateSmartFeeFormat: RootJsonFormat[EstimateSmartFee] = jsonFormat3(EstimateSmartFee)
}
