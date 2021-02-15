package com.coinzway.coinz4s.core.rpc.bitcoindbase

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.ClientObjects.{AddressType, RawTransactionInputs, Recipients, SubtractFeeFromList}
import BitcoindBaseRpcResponses._
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.{ClientObjects, NodeResponseT}
import com.softwaremill.sttp.{MonadError, SttpBackend}

trait BitcoindBaseRpc[R[_]] extends BitcoindBaseRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]
  implicit private val monadError: MonadError[R] = sttpBackend.responseMonad

  def signRawTransaction(transactionHex: String)(): R[NodeResponse[SignedRawTransaction]]

  def walletInfo: R[NodeResponse[GetWalletInfo]] =
    client.request[GetWalletInfo]("getwalletinfo", Vector.empty)

  def networkInfo: R[NodeResponse[GetNetworkInfo]] =
    client.request[GetNetworkInfo]("getnetworkinfo", Vector.empty)

  def memPoolInfo: R[NodeResponse[GetMemPoolInfo]] =
    client.request[GetMemPoolInfo]("getmempoolinfo", Vector.empty)

  def blockchainInfo: R[NodeResponse[GetBlockChainInfo]] =
    client.request[GetBlockChainInfo]("getblockchaininfo", Vector.empty)

  def listUnspentTransactions(
      minimumConfirmations: Option[Int] = None,
      maximumConfirmations: Option[Int] = None
    )(
    ): R[NodeResponse[UnspentTransactions]] =
    client.request[UnspentTransactions](
      "listunspent",
      Vector(minimumConfirmations.getOrElse(1), maximumConfirmations.getOrElse(9999999))
    )

  def getNewAddress(): R[NodeResponse[GetNewAddress]] =
    client.request[GetNewAddress]("getnewaddress", Vector.empty)

  def getNewAddress(label: Option[String], addressType: Option[AddressType.Value])(): R[NodeResponse[GetNewAddress]] =
    client.request[GetNewAddress]("getnewaddress", label.getOrElse("") +: addressType.map(_.toString).toVector)

  def getRawChangeAddress(addressType: Option[AddressType.Value] = None): R[NodeResponse[GetRawChangeAddress]] =
    client.request[GetRawChangeAddress]("getrawchangeaddress", addressType.map(_.toString).toVector)

  def sendToAddress(
      to: String,
      amount: BigDecimal,
      comment: String = "",
      commentTo: String = "",
      subtractFeeFromAmount: Option[Boolean] = None,
      replaceable: Option[Boolean] = None
    )(
    ): R[NodeResponse[SentTransactionId]] =
    client.request[SentTransactionId](
      "sendtoaddress",
      Vector(
        to,
        amount,
        comment,
        commentTo,
        subtractFeeFromAmount.getOrElse(false),
        replaceable.getOrElse(false)
      )
    )

  def setTxFee(btcPerKb: BigDecimal)(): R[NodeResponse[SetTxFee]] =
    client.request[SetTxFee]("settxfee", Vector(btcPerKb))

  def getTransaction(txid: String)(): R[NodeResponse[Transaction]] =
    client.request[Transaction]("gettransaction", Vector(txid))

  def getRawTransactionVerbose(txid: String)(): R[NodeResponse[RawTransaction]] =
    client.request[RawTransaction]("getrawtransaction", Vector(txid, true))

  def listSinceBlock(
      headerHash: String,
      targetConfirmations: Int = 1,
      includeWatchOnly: Boolean = false
    )(
    ): R[NodeResponse[ListSinceBlockResponse]] =
    client.request[ListSinceBlockResponse]("listsinceblock", Vector(headerHash, targetConfirmations, includeWatchOnly))

  def sendMany(
      recipients: ClientObjects.Recipients,
      minConf: Option[Int] = None,
      comment: Option[String] = None,
      subtractFeeFrom: Option[SubtractFeeFromList] = None,
      replaceable: Option[Boolean] = None
    )(
    ): R[NodeResponse[SentTransactionId]] =
    client
      .request[SentTransactionId](
        "sendmany",
        Vector(
          "",
          recipients,
          minConf.getOrElse(1),
          comment.getOrElse(""),
          subtractFeeFrom.getOrElse(SubtractFeeFromList(List.empty)),
          replaceable.getOrElse(false)
        )
      )

  def createRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(): R[NodeResponse[TransactionHex]] =
    client.request[TransactionHex]("createrawtransaction", Vector(inputs, outputs))

  def sendRawTransaction(signedHex: String)(): R[NodeResponse[SentTransactionId]] =
    client.request[SentTransactionId]("sendrawtransaction", Vector(signedHex))

  def sendRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(): R[NodeResponse[SentTransactionId]] =
    (for {
      rawTransaction <- NodeResponseT(createRawTransaction(inputs, outputs))
      signedTransaction <- NodeResponseT(signRawTransaction(rawTransaction.hex))
      sentTransactionId <- NodeResponseT(sendRawTransaction(signedTransaction.hex))
    } yield sentTransactionId).value

  def validateAddress(address: String)(): R[NodeResponse[ValidateAddress]] =
    client.request[ValidateAddress]("validateaddress", Vector(address))

  def getRawMempool(): R[NodeResponse[GetRawMempoolResponse]] = {
    val verbose = false
    client.request[GetRawMempoolResponse]("getrawmempool", Vector(verbose))
  }
}
