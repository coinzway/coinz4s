package com.coinzway.coinz4s.core

object ClientObjects {
  final case class Recipients(value: Map[String, BigDecimal])
  final case class RawTransactionInputs(inputs: List[RawTransactionInput])

  final case class RawTransactionInput(
      txid: String,
      vout: Int,
      sequence: Option[Int] = None)

  object EstimateMode extends Enumeration {
    val UNSET, ECONOMICAL, CONSERVATIVE = Value
  }

  object AddressType extends Enumeration {
    val LEGACY: AddressType.Value = Value("legacy")
    val P2SH_SEGWIT: AddressType.Value = Value("p2sh-segwit")
    val BECH32: AddressType.Value = Value("bech32")
  }
}
