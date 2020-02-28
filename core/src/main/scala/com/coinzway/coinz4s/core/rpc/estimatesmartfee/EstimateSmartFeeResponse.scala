package com.coinzway.coinz4s.core.rpc.estimatesmartfee

import com.coinzway.coinz4s.core.BaseResponses._

object EstimateSmartFeeResponse {

  final case class EstimateSmartFee(
      feerate: Option[BigDecimal],
      errors: Option[List[String]],
      blocks: Int)
      extends CorrectResponse

}
