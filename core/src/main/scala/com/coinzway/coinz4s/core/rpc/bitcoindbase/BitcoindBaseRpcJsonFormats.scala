package com.coinzway.coinz4s.core.rpc.bitcoindbase

import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses._
import spray.json._

trait BitcoindBaseRpcJsonFormats extends DefaultJsonProtocol {
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
      if (json.asJsObject.fields.contains("coinbase"))
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
  implicit val CreateWalletFormat: RootJsonFormat[CreateWallet] = jsonFormat1(CreateWallet)

  implicit object GetBlockCountFormat extends RootJsonFormat[GetBlockCount] {

    override def read(json: JsValue): GetBlockCount = json match {
      case JsNumber(n) => GetBlockCount(n.toInt)
      case _           => GetBlockCount(0)
    }

    override def write(obj: GetBlockCount): JsValue = JsNumber(obj.n)
  }

  implicit object GetBlockStatsFormat extends RootJsonFormat[GetBlockStats] {

    override def read(json: JsValue): GetBlockStats = GetBlockStats(
      avgfee = fromField[Int](json, "avgfee"),
      avgfeerate = fromField[Int](json, "avgfeerate"),
      avgtxsize = fromField[Int](json, "avgtxsize"),
      blockhash = fromField[String](json, "blockhash"),
      feerate_percentiles = fromField[List[Int]](json, "feerate_percentiles"),
      height = fromField[Int](json, "height"),
      ins = fromField[Int](json, "ins"),
      maxfee = fromField[Int](json, "maxfee"),
      maxfeerate = fromField[Int](json, "maxfeerate"),
      maxtxsize = fromField[Int](json, "maxtxsize"),
      medianfee = fromField[Int](json, "medianfee"),
      mediantime = fromField[Int](json, "mediantime"),
      mediantxsize = fromField[Int](json, "mediantxsize"),
      minfee = fromField[Int](json, "minfee"),
      minfeerate = fromField[Int](json, "minfeerate"),
      mintxsize = fromField[Int](json, "mintxsize"),
      outs = fromField[Int](json, "outs"),
      subsidy = fromField[Int](json, "subsidy"),
      swtotal_size = fromField[Int](json, "swtotal_size"),
      swtotal_weight = fromField[Int](json, "swtotal_weight"),
      swtxs = fromField[Int](json, "swtxs"),
      time = fromField[Int](json, "time"),
      total_out = fromField[Int](json, "total_out"),
      total_size = fromField[Int](json, "total_size"),
      total_weight = fromField[Int](json, "total_weight"),
      totalfee = fromField[Int](json, "totalfee"),
      txs = fromField[Int](json, "txs"),
      utxo_increase = fromField[Int](json, "utxo_increase"),
      utxo_size_inc = fromField[Int](json, "utxo_size_inc")
    )

    override def write(obj: GetBlockStats): JsValue = {
      val map = Map(
        "avgfee" -> obj.avgfee.toJson,
        "avgfeerate" -> obj.avgfeerate.toJson,
        "avgtxsize" -> obj.avgtxsize.toJson,
        "blockhash" -> obj.blockhash.toJson,
        "feerate_percentiles" -> obj.feerate_percentiles.toJson,
        "height" -> obj.height.toJson,
        "ins" -> obj.ins.toJson,
        "maxfee" -> obj.maxfee.toJson,
        "maxfeerate" -> obj.maxfeerate.toJson,
        "maxtxsize" -> obj.maxtxsize.toJson,
        "medianfee" -> obj.medianfee.toJson,
        "mediantime" -> obj.mediantime.toJson,
        "mediantxsize" -> obj.mediantxsize.toJson,
        "minfee" -> obj.minfee.toJson,
        "minfeerate" -> obj.minfeerate.toJson,
        "mintxsize" -> obj.mintxsize.toJson,
        "outs" -> obj.outs.toJson,
        "subsidy" -> obj.subsidy.toJson,
        "swtotal_size" -> obj.swtotal_size.toJson,
        "swtotal_weight" -> obj.swtotal_weight.toJson,
        "swtxs" -> obj.swtxs.toJson,
        "time" -> obj.time.toJson,
        "total_out" -> obj.total_out.toJson,
        "total_size" -> obj.total_size.toJson,
        "total_weight" -> obj.total_weight.toJson,
        "totalfee" -> obj.totalfee.toJson,
        "txs" -> obj.txs.toJson,
        "utxo_increase" -> obj.utxo_increase.toJson,
        "utxo_size_inc" -> obj.utxo_size_inc.toJson
      )
      JsObject(map.filter { case (_, v) => v != JsNull })
    }

  }

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

  implicit object GetRawMempoolResponseFormat extends RootJsonReader[GetRawMempoolResponse] {

    override def read(json: JsValue): GetRawMempoolResponse = json match {
      case JsArray(txids) =>
        GetRawMempoolResponse(txids.map {
          case txid: JsString => txid.value
          case other          => deserializationError("Expected txid value as JsString, but got " + other)
        })
      case x => deserializationError("Expected array of strings, but got " + x)
    }
  }

  implicit object ValidateAddressFormat extends RootJsonReader[ValidateAddress] {

    override def read(json: JsValue): ValidateAddress = json.asJsObject.getFields("isvalid") match {
      case Seq(JsBoolean(x)) => ValidateAddress(x)
      case x                 => deserializationError("Expected ValidateAddress as JsBoolean, but got " + x)
    }
  }

}
