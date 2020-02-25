package com.coinzway.coinz4s.bitcoindCash

import spray.json._

object TestData {

  val walletInfoResponse: JsValue = readJson("wallet-info-response.json")
  val networkInfoResponse: JsValue = readJson("network-info-response.json")
  val memPoolInfoResponse: JsValue = readJson("mem-pool-info-response.json")
  val blockchainInfoResponse: JsValue = readJson("blockchain-info-response.json")
  val listUnspentResponse: JsValue = readJson("list-unspent-response.json")
  val getNewAddressResponse: JsValue = readJson("get-new-address-response.json")
  val getRawChangeAddressResponse: JsValue = readJson("get-raw-change-address-response.json")
  val sendToAddressResponse: JsValue = readJson("sendtoaddress-response.json")
  val generateToAddressResponse: JsValue = readJson("generate-to-address-response.json")
  val parseErrorResponse: JsValue = readJson("parse-error-response.json")
  val insufficientFundsResponse: JsValue = readJson("insufficient-funds-response.json")
  val setTxFeeResponse: JsValue = readJson("set-tx-fee-response.json")
  val setTxFeeOutOfRangeResponse: JsValue = readJson("set-tx-tee-out-of-range-response.json")
  val getTransactionResponse: JsValue = readJson("get-transaction-response.json")
  val getRawTransactionResponseVerbose: JsValue = readJson("get-raw-transaction-response-verbose.json")
  val getRawTransactionResponseVerboseCoinbase: JsValue = readJson("get-raw-transaction-response-verbose-coinbase.json")
  val listSinceBlockResponse: JsValue = readJson("list-since-block-response.json")
  val sendManyResponse: JsValue = readJson("send-many-response.json")
  val createRawTransaction: JsValue = readJson("create-raw-transaction.json")
  val signRawTransactionWithWallet: JsValue = readJson("sign-raw-transaction-with-wallet.json")
  val sendRawTransaction: JsValue = readJson("send-raw-transaction.json")
  val validateAddress: JsValue = readJson("validate-address-response.json")
  val createWalletResponse: JsValue = readJson("create-wallet-response.json")
  val invalidJsonResponse: JsValue = readJson("invalid-json-response.json")

  private def readJson(name: String): JsValue = {
    val json = scala.io.Source.fromResource(name).getLines.mkString
    JsonParser(json)
  }

}
