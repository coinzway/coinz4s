package com.coinzway.coinz4s.core.rpc.bitcoindbase

object BitcoindBaseRpcResponses {

  final case class GetWalletInfo(
      walletversion: Int,
      balance: BigDecimal,
      unconfirmed_balance: BigDecimal,
      txcount: Int,
      keypoololdest: Int,
      keypoolsize: Int)

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

  final case class GetMemPoolInfo(
      size: Int,
      bytes: Int,
      usage: Int,
      maxmempool: Option[Int],
      mempoolminfee: Option[Int])

  final case class GetBlockChainInfo(
      chain: String,
      blocks: Int,
      headers: Int,
      bestblockhash: String,
      difficulty: BigDecimal,
      verificationprogress: BigDecimal,
      chainwork: String,
      pruned: Boolean,
      pruneheight: Option[Int])

  final case class UnspentTransactions(unspentTransactions: Vector[UnspentTransaction])

  final case class UnspentTransaction(
      txid: String,
      vout: Int,
      address: String,
      scriptPubKey: String,
      amount: BigDecimal,
      confirmations: Long,
      spendable: Boolean,
      solvable: Option[Boolean])

  final case class GetNewAddress(address: String)

  final case class GetRawChangeAddress(address: String)

  final case class SentTransactionId(id: String)

  final case class HeaderHashes(hashes: Seq[String])

  final case class SetTxFee(result: Boolean)

  final case class ListSinceBlockResponse(transactions: List[ListSinceBlockTransaction], lastblock: String)

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
      `bip125-replaceable`: Option[String],
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
      `bip125-replaceable`: Option[String],
      comment: Option[String],
      to: Option[String],
      details: List[TransactionDetails],
      hex: String)

  sealed trait Input
  final case class TransactionInput(txid: String, vout: Long) extends Input
  final case class CoinbaseInput(coinbase: String, sequence: Long) extends Input

  final case class TransactionOutput(value: BigDecimal, n: Long)

  final case class RawTransaction(
      txid: String,
      size: Option[Int],
      vin: List[Input],
      vout: List[TransactionOutput],
      confirmations: Option[Long])

  final case class TransactionDetails(
      involvesWatchonly: Option[Boolean],
      address: Option[String],
      category: String,
      amount: BigDecimal,
      vout: Int,
      fee: Option[BigDecimal],
      abandoned: Option[Boolean])

  final case class TransactionHex(hex: String)
  final case class SignedRawTransaction(hex: String, complete: Boolean)

  final case class ValidateAddress(isvalid: Boolean)
  final case class CreateWallet(name: String)

  final case class Address(
      address: String,
      port: Int,
      score: Int)

  final case class Network(
      name: String,
      limited: Boolean,
      reachable: Boolean,
      proxy: String,
      localaddress: Option[Vector[Address]])

  final case class GetRawMempoolResponse(mempoolTxids: Vector[String])
}
