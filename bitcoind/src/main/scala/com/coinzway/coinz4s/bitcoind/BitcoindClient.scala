package com.coinzway.coinz4s.bitcoind

import com.coinzway.coinz4s.bitcoind.ClientObjects.{AddressType, EstimateMode, RawTransactionInputs, Recipients}
import com.coinzway.coinz4s.bitcoind.Responses._
import com.coinzway.coinz4s.core.BaseResponses.{CorrectResponse, GeneralErrorResponse, NodeResponse}
import com.coinzway.coinz4s.core.NodeResponseT
import com.softwaremill.sttp._
import spray.json._

import scala.util.{Failure, Success, Try}

class BitcoindClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int,
    wallet: Option[String]
  )(implicit sttpBackend: SttpBackend[R, Nothing])
    extends JsonFormats {
  implicit private val monadError: MonadError[R] = sttpBackend.responseMonad

  private val request = {
    val walletPath = wallet.map(w => s"/wallet/$w").getOrElse("")
    val uri = s"http://$host:$port$walletPath"
    sttp.auth
      .basic(user, password)
      .post(Uri.parse(uri).getOrElse(throw new RuntimeException(s"invalid wallet connection url: $uri")))
  }

  private def as[T <: CorrectResponse](implicit reader: JsonReader[T]): ResponseAs[NodeResponse[T], Nothing] =
    asString.map { r =>
      val responseObject = r.parseJson.asJsObject
      responseObject.fields("result") match {
        case JsNull =>
          Left(GeneralErrorResponse(responseObject.fields.get("error").map(_.toString).getOrElse("Unknown error")))
        case json: JsValue =>
          Try(json.convertTo[T]) match {
            case Success(success) => Right(success)
            case Failure(_)       => Left(GeneralErrorResponse(s"Error parsing JSON, got: ${json.compactPrint}"))
          }
      }
    }

  def walletInfo: R[NodeResponse[GetWalletInfo]] =
    request.body(method("getwalletinfo")).response(as[GetWalletInfo]).send()

  def networkInfo: R[NodeResponse[GetNetworkInfo]] =
    request.body(method("getnetworkinfo")).response(as[GetNetworkInfo]).send()

  def memPoolInfo: R[NodeResponse[GetMemPoolInfo]] =
    request.body(method("getmempoolinfo")).response(as[GetMemPoolInfo]).send()

  def blockchainInfo: R[NodeResponse[GetBlockChainInfo]] =
    request.body(method("getblockchaininfo")).response(as[GetBlockChainInfo]).send()

  def estimateSmartFee(
      confTarget: Int,
      estimateMode: Option[EstimateMode.Value] = None
    )(
    ): R[NodeResponse[EstimateSmartFee]] =
    request
      .body(method("estimatesmartfee", confTarget +: estimateMode.map(_.toString).toVector))
      .response(as[EstimateSmartFee])
      .send()

  def listUnspentTransactions(
      minimumConfirmations: Option[Int] = None,
      maximumConfirmations: Option[Int] = None
    )(
    ): R[NodeResponse[UnspentTransactions]] =
    request
      .body(method("listunspent", Vector(minimumConfirmations.getOrElse(1), maximumConfirmations.getOrElse(9999999))))
      .response(as[UnspentTransactions])
      .send()

  def getNewAddress(): R[NodeResponse[GetNewAddress]] =
    request
      .body(method("getnewaddress"))
      .response(as[GetNewAddress])
      .send()

  def getNewAddress(label: Option[String], addressType: Option[AddressType.Value])(): R[NodeResponse[GetNewAddress]] =
    request
      .body(method("getnewaddress", label.getOrElse("") +: addressType.map(_.toString).toVector))
      .response(as[GetNewAddress])
      .send()

  def getRawChangeAddress(addressType: Option[AddressType.Value] = None): R[NodeResponse[GetRawChangeAddress]] =
    request
      .body(method("getrawchangeaddress", addressType.map(_.toString).toVector))
      .response(as[GetRawChangeAddress])
      .send()

  def sendToAddress(
      to: String,
      amount: BigDecimal,
      comment: String = "",
      commentTo: String = ""
    )(
    ): R[NodeResponse[SentTransactionId]] =
    request
      .body(method("sendtoaddress", Vector(to, amount, comment, commentTo)))
      .response(as[SentTransactionId])
      .send()

  def setTxFee(btcPerKb: BigDecimal)(): R[NodeResponse[SetTxFee]] =
    request
      .body(method("settxfee", Vector(btcPerKb)))
      .response(as[SetTxFee])
      .send()

  def generatetoaddress(number: Int, address: String)(): R[NodeResponse[HeaderHashes]] =
    request
      .body(method("generatetoaddress", Vector(number, address)))
      .response(as[HeaderHashes])
      .send()

  def getTransaction(txid: String)(): R[NodeResponse[Transaction]] =
    request
      .body(method("gettransaction", Vector(txid)))
      .response(as[Transaction])
      .send()

  def getRawTransactionVerbose(txid: String)(): R[NodeResponse[RawTransaction]] =
    request
      .body(method("getrawtransaction", Vector(txid, true)))
      .response(as[RawTransaction])
      .send()

  def listSinceBlock(
      headerHash: String,
      targetConfirmations: Int = 1,
      includeWatchOnly: Boolean = false
    )(
    ): R[NodeResponse[ListSinceBlockResponse]] =
    request
      .body(method("listsinceblock", Vector(headerHash, targetConfirmations, includeWatchOnly)))
      .response(as[ListSinceBlockResponse])
      .send()

  def sendMany(recipients: ClientObjects.Recipients)(): R[NodeResponse[SentTransactionId]] =
    request
      .body(method("sendmany", Vector("", recipients)))
      .response(as[SentTransactionId])
      .send()

  def createRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(): R[NodeResponse[TransactionHex]] =
    request
      .body(method("createrawtransaction", Vector(inputs, outputs)))
      .response(as[TransactionHex])
      .send()

  def signRawTransaction(transactionHex: String)(): R[NodeResponse[SignedRawTransaction]] =
    request
      .body(method("signrawtransactionwithwallet", Vector(transactionHex)))
      .response(as[SignedRawTransaction])
      .send()

  def sendRawTransaction(signedHex: String)(): R[NodeResponse[SentTransactionId]] =
    request
      .body(method("sendrawtransaction", Vector(signedHex)))
      .response(as[SentTransactionId])
      .send()

  def sendRawTransaction(inputs: RawTransactionInputs, outputs: Recipients)(): R[NodeResponse[SentTransactionId]] =
    (for {
      rawTransaction <- NodeResponseT(createRawTransaction(inputs, outputs))
      signedTransaction <- NodeResponseT(signRawTransaction(rawTransaction.hex))
      sentTransactionId <- NodeResponseT(sendRawTransaction(signedTransaction.hex))
    } yield sentTransactionId).value

  def validateAddress(address: String)(): R[NodeResponse[ValidateAddress]] =
    request
      .body(method("validateaddress", Vector(address)))
      .response(as[ValidateAddress])
      .send()

  implicit private def flatten[T <: CorrectResponse](response: R[Response[NodeResponse[T]]]): R[NodeResponse[T]] = {
    import com.softwaremill.sttp.monadSyntax._
    response.map(response => response.body.left.map(error => GeneralErrorResponse(error)).joinRight)
  }

  private def method(methodName: String, params: Vector[Any] = Vector.empty) =
    if (params.isEmpty) {
      s"""{"method": "$methodName"}"""
    } else {
      val formattedParams = HttpParamsConverter.rpcParamsToJson(params)
      s"""{"method": "$methodName", "params": [${formattedParams.mkString(",")}]}"""
    }

  def createWallet(walletName: String): R[NodeResponse[CreateWallet]] =
    request
      .body(method("createwallet", Vector(walletName)))
      .response(as[CreateWallet])
      .send()

}
