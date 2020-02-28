package com.coinzway.coinz4s.zcashd

import spray.json._

trait TestDataHelper {

  protected def extractMethod(body: String): (String, Vector[String]) = {
    val entityJson = body.parseJson.asJsObject
    val method = entityJson.fields("method") match {
      case JsString(m) => m.toString
      case other       => deserializationError(s"expected method as String but got: $other")
    }

    val params = entityJson.fields.get("params").map {
      case JsArray(values) =>
        values.map {
          case JsString(s)  => s
          case JsNumber(n)  => n.toString
          case JsBoolean(b) => b.toString
          case JsObject(a)  => a.toString
          case JsArray(o)   => o.toString()
          case other        => deserializationError(s"expected JsArray to be String but got: $other")
        }
      case other => deserializationError(s"expected params as JsArray but got: $other")
    }

    (method, params.getOrElse(Vector.empty[String]))
  }

  protected def loadJsonResponseFromTestData(arg: (String, Vector[String])): String =
    arg match {
      case (method, params) =>
        val json = method match {
          case _ if params.contains("parseError")                 => TestData.parseErrorResponse
          case "getwalletinfo"                                    => TestData.walletInfoResponse
          case "getnetworkinfo"                                   => TestData.networkInfoResponse
          case "getmempoolinfo"                                   => TestData.memPoolInfoResponse
          case "getblockchaininfo"                                => TestData.blockchainInfoResponse
          case "listunspent"                                      => TestData.listUnspentResponse
          case "getnewaddress"                                    => TestData.getNewAddressResponse
          case "getrawchangeaddress"                              => TestData.getRawChangeAddressResponse
          case "generatetoaddress"                                => TestData.generateToAddressResponse
          case "sendfrom" if params.contains("insufficientFunds") => TestData.insufficientFundsResponse
          case "sendtoaddress" if params(1).toDouble > 100        => TestData.insufficientFundsResponse
          case "sendtoaddress"                                    => TestData.sendToAddressResponse
          case "settxfee" if params(0).toDouble < 0               => TestData.setTxFeeOutOfRangeResponse
          case "settxfee"                                         => TestData.setTxFeeResponse
          case "gettransaction"                                   => TestData.getTransactionResponse
          case "getrawtransaction" if params.contains("transaction-with-coinbase") =>
            TestData.getRawTransactionResponseVerboseCoinbase
          case "getrawtransaction"                                  => TestData.getRawTransactionResponseVerbose
          case "listsinceblock"                                     => TestData.listSinceBlockResponse
          case "sendmany"                                           => TestData.sendManyResponse
          case "createrawtransaction"                               => TestData.createRawTransaction
          case "signrawtransaction"                                 => TestData.signRawTransaction
          case "sendrawtransaction"                                 => TestData.sendRawTransaction
          case "validateaddress" if params.contains("invalid-json") => TestData.invalidJsonResponse
          case "validateaddress"                                    => TestData.validateAddress

          case _ => JsNumber(-1)
        }
        json.prettyPrint
    }
}
