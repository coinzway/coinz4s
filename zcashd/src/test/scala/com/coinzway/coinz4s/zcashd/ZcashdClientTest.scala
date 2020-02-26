package com.coinzway.coinz4s.zcashd

import com.coinzway.coinz4s.bitcoind.ClientObjects
import com.coinzway.coinz4s.bitcoind.ClientObjects.{AddressType, RawTransactionInput, RawTransactionInputs}
import com.coinzway.coinz4s.bitcoind.Responses.{CoinbaseInput, TransactionInput}
import com.coinzway.coinz4s.core.BaseResponses.GeneralErrorResponse
import com.softwaremill.sttp._
import com.softwaremill.sttp.testing.SttpBackendStub
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json._

class ZcashdClientTest extends AnyFlatSpec with Matchers with TestDataHelper {
  val user = "user"
  val password = "password"
  val host = "localhost"
  val port = 1337

  implicit val stubBackend: SttpBackendStub[Id, Nothing] = SttpBackendStub.synchronous.whenRequestMatchesPartial {
    case RequestT(Method.POST, uri, body: StringBody, _, _, _, _) if uri == uri"http://$host:$port" =>
      Response.ok(loadJsonResponseFromTestData(extractMethod(body.s)))
  }
  val zcashdCashClient: ZcashdCashClient[Id] = new ZcashdCashClient(user, password, host, port)

  it should "return walletinfo" in {
    zcashdCashClient.walletInfo match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(walletInfo) =>
        walletInfo.balance shouldBe BigDecimal("3306.79687127")
        walletInfo.unconfirmed_balance shouldBe BigDecimal(0)
    }
  }

  it should "generate blocks" in {
    zcashdCashClient.generatetoaddress(2, "mxC1MksGZQAARADNQutrT5FPVn76bqmgZW") match {
      case Left(x) => throw new RuntimeException("unexpected zcashd response " + x)
      case Right(generated) =>
        generated.hashes should contain theSameElementsAs Seq(
          "36252b5852a5921bdfca8701f936b39edeb1f8c39fffe73b0d8437921401f9af",
          "5f2956817db1e386759aa5794285977c70596b39ea093b9eab0aa4ba8cd50c06"
        )
    }
  }

  it should "return networkinfo" in {
    zcashdCashClient.networkInfo match {
      case Left(_)            => throw new RuntimeException("unexpected zcashd response")
      case Right(networkInfo) => networkInfo.connections shouldBe 0
    }
  }

  it should "return memPoolInfo" in {
    zcashdCashClient.memPoolInfo match {
      case Left(_)            => throw new RuntimeException("unexpected zcashd response")
      case Right(memPoolInfo) => memPoolInfo.size shouldBe 1
    }
  }

  it should "return blockchainInfo" in {
    zcashdCashClient.blockchainInfo match {
      case Left(_)               => throw new RuntimeException("unexpected zcashd response")
      case Right(blockchainInfo) => blockchainInfo.chain shouldBe "regtest"
    }
  }

  it should "return unspent transactions" in {
    zcashdCashClient.listUnspentTransactions(minimumConfirmations = Some(0), maximumConfirmations = Some(99999999)) match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(unspentTransactions) =>
        unspentTransactions.unspentTransactions.size shouldBe 2
        unspentTransactions.unspentTransactions.head.address shouldBe "tmGCHJK49G9PfmawFMM37oCFW8DtuQogMJB"
    }
  }

  it should "return new address" in {
    zcashdCashClient.getNewAddress() match {
      case Left(_)           => throw new RuntimeException("unexpected zcashd response")
      case Right(newAddress) => newAddress.address should have size 35
    }
  }

  it should "return new address for p2sh-segwit address type" in {
    zcashdCashClient.getNewAddress(None, Some(AddressType.P2SH_SEGWIT)) match {
      case Left(_)           => throw new RuntimeException("unexpected zcashd response")
      case Right(newAddress) => newAddress.address should have size 35
    }
  }

  it should "return change address" in {
    zcashdCashClient.getRawChangeAddress() match {
      case Left(_)           => throw new RuntimeException("unexpected zcashd response")
      case Right(newAddress) => newAddress.address shouldBe "tmEmY6fCU1VgoHtqTNaAAA54h2eREz8HA3R"
    }
  }

  it should "new address should handle parse error" in {
    zcashdCashClient.getNewAddress(Some("parseError"), None) match {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.parseErrorResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid zcashd response")
    }
  }

  "sendtoaddress" should "send and return transation id" in {
    zcashdCashClient.sendToAddress("nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 0.001) match {
      case Left(_)              => throw new RuntimeException("unexpected zcashd response")
      case Right(transactionId) => transactionId.id should have size 64
    }
  }

  it should "handle insufficient funds errors" in {
    zcashdCashClient.sendToAddress("nt54hMq9ghkvTBqmw3BoLjPBGBPWU1RexJ", 101) match {
      case Left(x) =>
        x shouldBe a[GeneralErrorResponse]
        x.errorMessage.parseJson shouldBe TestData.insufficientFundsResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid zcashd response")
    }
  }

  it should "set transaction fee" in {
    zcashdCashClient.setTxFee(BigDecimal(0.0003)) match {
      case Left(_)         => throw new RuntimeException("unexpected zcashd response")
      case Right(response) => response.result shouldBe true
    }
  }

  it should "respond with error for out of range tx fee" in {
    zcashdCashClient.setTxFee(BigDecimal(-1)) match {
      case Left(err) =>
        err shouldBe a[GeneralErrorResponse]
        err.errorMessage.parseJson shouldBe TestData.setTxFeeOutOfRangeResponse.asJsObject.fields("error")
      case Right(_) => throw new RuntimeException("expected invalid zcashd response")
    }
  }

  it should "get transaction by id" in {
    val txid = "34572253f55579876a53945077aca40b3a27b06dbe1ecb4a4f5e8d453b64f2cf"
    zcashdCashClient.getTransaction(txid) match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(response) =>
        response.fee shouldBe Some(BigDecimal(-0.00000373))
        response.details should have size 2
    }
  }

  it should "get raw transaction by id" in {
    val txid = "34572253f55579876a53945077aca40b3a27b06dbe1ecb4a4f5e8d453b64f2cf"
    zcashdCashClient.getRawTransactionVerbose(txid) match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(response) =>
        response.vin should have size 2
        val txId = response.vin.head.asInstanceOf[TransactionInput].txid
        txId shouldBe "315445c55750a5296f605f65f08bba44d5b373ce25dbc2b4a7f9482b4eea679e"
    }
  }

  it should "get raw transaction by id - coinbase as input" in {
    val txid = "transaction-with-coinbase"
    zcashdCashClient.getRawTransactionVerbose(txid) match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(response) =>
        response.vin should have size 1
        val coinbase = response.vin.head.asInstanceOf[CoinbaseInput].coinbase
        coinbase shouldBe "02de010101"
    }
  }

  "listsinceblock" should "return hash of last block and list of transactions since given block" in {
    val blockhash = "4fed3588db4a6e40597620bd957beb959eacf502291e83a39898a740211727b8"
    val targetConfirmations = 3
    val includeWatchOnly = false
    zcashdCashClient.listSinceBlock(blockhash, targetConfirmations, includeWatchOnly) match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(response) =>
        response.lastblock shouldBe "4fed3588db4a6e40597620bd957beb959eacf502291e83a39898a740211727b8"
        response.transactions should have size 2
    }
  }

  "sendmany" should "return transaction id" in {
    val txId = "b5d1a82d7fd1f0e566bb0aabed172019854e2dff0ae729dc446beefd17c5c0cc"
    val sendManyMap = ClientObjects.Recipients(Map("address1" -> 0.1, "address2" -> 0.3))
    zcashdCashClient.sendMany(recipients = sendManyMap) match {
      case Left(_)              => throw new RuntimeException("unexpected zcashd response")
      case Right(transactionId) => transactionId.id shouldBe txId
    }
  }

  "createrawtransaction" should "return transaction hex" in {
    val hex = "02000000000180969800000000001976a914f5b32cc7579d678b60780846128b0f98f74cd10e88ac00000000"
    val inputs = RawTransactionInputs(
      List(RawTransactionInput("b5d1a82d7fd1f0e566bb0aabed172019854e2dff0ae729dc446beefd17c5c0cc", 1, None))
    )
    val outputs = ClientObjects.Recipients(Map("address1" -> 0.1, "address2" -> 0.3))
    zcashdCashClient.createRawTransaction(inputs, outputs) match {
      case Left(_)               => throw new RuntimeException("unexpected zcashd response")
      case Right(transactionHex) => transactionHex.hex shouldBe hex
    }
  }

  "signrawtransaction" should "return signed transaction" in {
    val hex = "02000000000180969800000000001976a914f5b32cc7579d678b60780846128b0f98f74cd10e88ac00000000"
    val signedHex =
      "02000000010205704f11711b204e691c257ac7ab84a0014e38dda5c35e1936d11fc7030432000000004948304502210087962368e1f03ddc03b96ef934d2058abe080e9f551f69929c75f2fe7324036e02201369850a0b1c4f7b3148631f3c50063644d7959abb5e97cda9db43dd9b6e867d01ffffffff0160720195000000001976a914835328a1b2103387912fcf054cc138c38064b08b88ac00000000"
    zcashdCashClient.signRawTransactionWithWallet(hex) match {
      case Left(_) => throw new RuntimeException("unexpected zcashd response")
      case Right(signedRawTransaction) =>
        signedRawTransaction.hex shouldBe signedHex
        signedRawTransaction.complete shouldBe true
    }
  }

  "sendrawtransaction" should "return signed transaction" in {
    val txId = "abd8d0a5f6c7ca5836ada0aa214fdc7c6e9488281f0369d01527b7a57eaf7fb0"
    val signedHex =
      "02000000010205704f11711b204e691c257ac7ab84a0014e38dda5c35e1936d11fc7030432000000004948304502210087962368e1f03ddc03b96ef934d2058abe080e9f551f69929c75f2fe7324036e02201369850a0b1c4f7b3148631f3c50063644d7959abb5e97cda9db43dd9b6e867d01ffffffff0160720195000000001976a914835328a1b2103387912fcf054cc138c38064b08b88ac00000000"
    zcashdCashClient.sendRawTransaction(signedHex) match {
      case Left(_)              => throw new RuntimeException("unexpected zcashd response")
      case Right(transactionId) => transactionId.id shouldBe txId

    }
  }

  "validateaddress" should "return if address is valid" in {
    val addr = "bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k"
    zcashdCashClient.validateAddress(addr) match {
      case Left(_)             => throw new RuntimeException("unexpected zcashd response")
      case Right(validAddress) => validAddress.isvalid shouldBe true
    }
  }

  "any endpoint" should "return error for error response" in {
    val response = zcashdCashClient.validateAddress("invalid-json")
    val leftValue = response.left.getOrElse(throw new RuntimeException("This should return Left"))
    leftValue.errorMessage shouldBe "Error parsing JSON, got: {\"immature_balance\":0,\"paytxfee\":0}"
  }

}
