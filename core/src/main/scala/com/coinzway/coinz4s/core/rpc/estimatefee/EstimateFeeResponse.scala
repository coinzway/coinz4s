package com.coinzway.coinz4s.core.rpc.estimatefee

import com.coinzway.coinz4s.core.BaseResponses._

object EstimateFeeResponse {
  final case class EstimateFee(fee: BigDecimal) extends CorrectResponse
}
