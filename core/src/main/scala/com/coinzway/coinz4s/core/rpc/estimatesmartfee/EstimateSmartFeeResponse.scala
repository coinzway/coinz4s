package com.coinzway.coinz4s.core.rpc.estimatesmartfee

object EstimateSmartFeeResponse {

  final case class EstimateSmartFee(
      feerate: Option[BigDecimal],
      errors: Option[List[String]],
      blocks: Int)

}
