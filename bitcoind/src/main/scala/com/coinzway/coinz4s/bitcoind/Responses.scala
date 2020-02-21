package com.coinzway.coinz4s.bitcoind

import com.coinzway.coinz4s.core.BaseResponses._

object Responses {

  final case class GetWalletInfo(
      walletversion: Int,
      balance: BigDecimal,
      unconfirmed_balance: BigDecimal,
      txcount: Int,
      keypoololdest: Int,
      keypoolsize: Int,
      unlocked_until: Option[Int])
      extends CorrectResponse

  final case class GetNetworkInfo(
      version: Int,
      subversion: String,
      protocolversion: Int,
      timeoffset: Int,
      connections: Int,
      proxy: Option[String],
      relayfee: BigDecimal,
      localservices: String,
      networks: Vector[Network])
      extends CorrectResponse

  final case class GetMiningInfo(
      blocks: Int,
      currentblockweight: Int,
      currentblocktx: Int,
      difficulty: Int,
      networkhashps: Int,
      pooledtx: Int,
      chain: String,
      warnings: Option[String])
      extends CorrectResponse

  final case class GetMemPoolInfo(
      size: Int,
      bytes: Int,
      usage: Int,
      maxmempool: Int,
      mempoolminfee: Int)
      extends CorrectResponse

  final case class GetBlockChainInfo(
      chain: String,
      blocks: Int,
      headers: Int,
      bestblockhash: String,
      difficulty: BigDecimal,
      mediantime: Int,
      verificationprogress: BigDecimal,
      chainwork: String,
      pruned: Boolean,
      pruneheight: Option[Int])
      extends CorrectResponse

  final case class EstimateSmartFee(
      feerate: Option[BigDecimal],
      errors: Option[List[String]],
      blocks: Int)
      extends CorrectResponse

  final case class UnspentTransactions(unspentTransactions: Vector[UnspentTransaction]) extends CorrectResponse

  final case class UnspentTransaction(
      txid: String,
      vout: Int,
      address: String,
      scriptPubKey: String,
      amount: BigDecimal,
      confirmations: Long,
      spendable: Boolean,
      solvable: Boolean)

  final case class GetNewAddress(address: String) extends CorrectResponse

  final case class GetRawChangeAddress(address: String) extends CorrectResponse

  final case class SentTransactionId(id: String) extends CorrectResponse

  final case class HeaderHashes(hashes: Seq[String]) extends CorrectResponse

  final case class SetTxFee(result: Boolean) extends CorrectResponse

  final case class ListSinceBlockResponse(transactions: List[ListSinceBlockTransaction], lastblock: String)
      extends CorrectResponse

  final case class ListSinceBlockTransaction(
      address: String,
      category: String,
      amount: BigDecimal,
      vout: Option[Int],
      fee: Option[BigDecimal],
      confirmations: Long,
      trusted: Option[Boolean],
      generated: Option[Boolean],
      blockhash: Option[String],
      blockindex: Option[Int],
      blocktime: Option[Long],
      txid: String,
      walletconflicts: List[String],
      time: Long,
      timereceived: Option[Long],
      comment: Option[String],
      to: Option[String],
      `bip125-replaceable`: String,
      abandoned: Option[Boolean])

  final case class Transaction(
      amount: BigDecimal,
      fee: Option[BigDecimal],
      confirmations: Int,
      generated: Option[Boolean],
      blockhash: Option[String],
      blockindex: Option[Int],
      blocktime: Option[Int],
      txid: String,
      walletconflicts: List[String],
      time: Long,
      timereceived: Long,
      `bip125-replaceable`: String,
      comment: Option[String],
      to: Option[String],
      details: List[TransactionDetails],
      hex: String)
      extends CorrectResponse

  sealed trait Input
  final case class TransactionInput(txid: String, vout: Long) extends Input
  final case class CoinbaseInput(coinbase: String, sequence: Long) extends Input

  final case class TransactionOutput(value: BigDecimal, n: Long)

  final case class RawTransaction(
      txid: String,
      size: Int,
      vin: List[Input],
      vout: List[TransactionOutput],
      confirmations: Option[Long])
      extends CorrectResponse

  final case class TransactionDetails(
      involvesWatchonly: Option[Boolean],
      address: Option[String],
      category: String,
      amount: BigDecimal,
      vout: Int,
      fee: Option[BigDecimal],
      abandoned: Option[Boolean])

  final case class TransactionHex(hex: String) extends CorrectResponse
  final case class SignedRawTransaction(hex: String, complete: Boolean) extends CorrectResponse

  final case class ValidateAddress(isvalid: Boolean) extends CorrectResponse
  final case class CreateWallet(name: String) extends CorrectResponse
}
