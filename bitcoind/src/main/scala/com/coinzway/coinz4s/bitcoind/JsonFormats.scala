package com.coinzway.coinz4s.bitcoind

import com.coinzway.coinz4s.bitcoind.Responses._
import spray.json._

trait JsonFormats extends DefaultJsonProtocol {
  implicit val AddressFormat: RootJsonFormat[Address] = jsonFormat3(Address)
  implicit val NetworkFormat: RootJsonFormat[Network] = jsonFormat5(Network)

  implicit val GetWalletInfoFormat: RootJsonFormat[GetWalletInfo] = jsonFormat6(GetWalletInfo)
  implicit val GetNetworkInfoFormat: RootJsonFormat[GetNetworkInfo] = jsonFormat9(GetNetworkInfo)
  implicit val GetMemPoolInfoFormat: RootJsonFormat[GetMemPoolInfo] = jsonFormat5(GetMemPoolInfo)
  implicit val GetBlockChainInfoFormat: RootJsonFormat[GetBlockChainInfo] = jsonFormat9(GetBlockChainInfo)

  implicit val UnspentTransactionFormat: RootJsonFormat[UnspentTransaction] = jsonFormat8(UnspentTransaction)

  implicit val TransactionDetailsFormat: RootJsonFormat[TransactionDetails] = jsonFormat7(TransactionDetails)
  implicit val TransactionFormat: RootJsonFormat[Transaction] = jsonFormat16(Transaction)

  implicit val TransactionInputFormat: RootJsonFormat[TransactionInput] = jsonFormat2(TransactionInput)
  implicit val TransactionOutputFormat: RootJsonFormat[TransactionOutput] = jsonFormat2(TransactionOutput)
  implicit val CoinbaseInputFormat: RootJsonFormat[CoinbaseInput] = jsonFormat2(CoinbaseInput)

  implicit object InputFormat extends RootJsonFormat[Input] {

    override def write(obj: Input): JsValue = obj match {
      case in: TransactionInput    => in.toJson
      case coinbase: CoinbaseInput => coinbase.toJson
    }

    override def read(json: JsValue): Input =
      if (json.asJsObject.fields.get("coinbase").isDefined)
        json.convertTo[CoinbaseInput]
      else
        json.convertTo[TransactionInput]
  }

  implicit val RawTransactionFormat: RootJsonFormat[RawTransaction] = jsonFormat5(RawTransaction)

  implicit val ListSinceBlockTransactionFormat: RootJsonFormat[ListSinceBlockTransaction] = jsonFormat19(
    ListSinceBlockTransaction
  )

  implicit val ListSinceBlockResponseFormat: RootJsonFormat[ListSinceBlockResponse] = jsonFormat2(
    ListSinceBlockResponse
  )

  implicit val SignedRawTransactionFormat: RootJsonFormat[SignedRawTransaction] = jsonFormat2(SignedRawTransaction)
  implicit val EstimateSmartFeeFormat: RootJsonFormat[EstimateSmartFee] = jsonFormat3(EstimateSmartFee)
  implicit val CreateWalletFormat: RootJsonFormat[CreateWallet] = jsonFormat1(CreateWallet)

  implicit object TransactionHexFormat extends RootJsonReader[TransactionHex] {

    override def read(json: JsValue): TransactionHex = json match {
      case JsString(hex) => TransactionHex(hex)
      case x             => deserializationError("Expected TransactionHex as JsString, but got " + x)
    }
  }

  implicit object GetNewAddressFormat extends RootJsonReader[GetNewAddress] {

    override def read(json: JsValue): GetNewAddress = json match {
      case JsString(x) => GetNewAddress(x)
      case x           => deserializationError("Expected GetNewAddress as JsString, but got " + x)
    }
  }

  implicit object GetRawChangeAddressFormat extends RootJsonReader[GetRawChangeAddress] {

    override def read(json: JsValue): GetRawChangeAddress = json match {
      case JsString(x) => GetRawChangeAddress(x)
      case x           => deserializationError("Expected GetRawChangeAddress as JsString, but got " + x)
    }
  }

  implicit object SentTransactionIdFormat extends RootJsonReader[SentTransactionId] {

    override def read(json: JsValue): SentTransactionId = json match {
      case JsString(x) => SentTransactionId(x)
      case x           => deserializationError("Expected SentTransactionId as JsString, but got " + x)
    }
  }

  implicit object HeaderHashesFormat extends RootJsonReader[HeaderHashes] {

    override def read(json: JsValue): HeaderHashes = json match {
      case JsArray(hashes) =>
        HeaderHashes(hashes.map {
          case JsString(s) => s
          case other       => deserializationError("Expected header hash value as JsString, but got " + other)
        })
      case x => deserializationError("Expected HeaderHashes as JsArray[HeaderHash], but got " + x)
    }
  }

  implicit object SetTxFeeFormat extends RootJsonReader[SetTxFee] {

    override def read(json: JsValue): SetTxFee = json match {
      case JsBoolean(x) => SetTxFee(x)
      case x            => deserializationError("Expected SetTxFee as JsBoolean, but got " + x)
    }
  }

  implicit object UnspentTransactionsFormat extends RootJsonReader[UnspentTransactions] {

    override def read(json: JsValue): UnspentTransactions = json match {
      case JsArray(unspentTransactions) =>
        UnspentTransactions(unspentTransactions.map {
          case unspentTransaction: JsObject => unspentTransaction.convertTo[UnspentTransaction]
          case other                        => deserializationError("Expected unspent transaction value as JsString, but got " + other)
        })
      case x => deserializationError("Expected UnspentTransactions as JsArray[UnspentTransaction], but got " + x)
    }
  }

  implicit object ValidateAddressFormat extends RootJsonReader[ValidateAddress] {

    override def read(json: JsValue): ValidateAddress = json.asJsObject.getFields("isvalid") match {
      case Seq(JsBoolean(x)) => ValidateAddress(x)
      case x                 => deserializationError("Expected ValidateAddress as JsBoolean, but got " + x)
    }
  }

}
