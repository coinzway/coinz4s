package com.coinzway.coinz4s.core.rpc.estimatefee

object EstimateFeeResponse {
  final case class EstimateFee(fee: BigDecimal)
}
