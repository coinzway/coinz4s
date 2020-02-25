package com.coinzway.coinz4s.zcashd

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.coinzway.coinz4s.bitcoind.ClientObjects._
import com.coinzway.coinz4s.bitcoind.Responses.{GetNewAddress, UnspentTransaction}
import com.coinzway.coinz4s.core.NodeResponseT
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.{MonadError, SttpBackend}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class ZcashdClientIntegrationTest extends AsyncWordSpec with Matchers {
  implicit val akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val monadError: MonadError[Future] = akkaHttpBackend.responseMonad

  val zcashdCashClient: ZcashdCashClient[Future] =
    new ZcashdCashClient("user", "password", "zcashd", 18344)

  "ZcashdClient" should {
    "get wallet info" in {
      zcashdCashClient.walletInfo.map(result => result shouldBe Symbol("right"))
    }
    "get network info" in {
      zcashdCashClient.networkInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mining info" in {
      zcashdCashClient.miningInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mem pool info" in {
      zcashdCashClient.memPoolInfo.map(result => result shouldBe Symbol("right"))
    }
    "get blockchain info" in {
      zcashdCashClient.blockchainInfo.map(result => result shouldBe Symbol("right"))
    }
    "list unspent transactions" in {
      zcashdCashClient.listUnspentTransactions().map(result => result shouldBe Symbol("right"))
    }
    "get new address" in {
      zcashdCashClient.getNewAddress().map(result => result shouldBe Symbol("right"))
    }
    "get new address with type" in {
      zcashdCashClient.getNewAddress(None, Some(AddressType.LEGACY)).map(result => result shouldBe Symbol("right"))
    }
    "send to address" in {
      val sendToAddress = (for {
        newAddress <- NodeResponseT(zcashdCashClient.getNewAddress())
        sendToAddress <- NodeResponseT(zcashdCashClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map(result => result shouldBe Symbol("right"))
    }
    "set tx fee" in {
      zcashdCashClient.setTxFee(0.05).map(result => result shouldBe Symbol("right"))
    }
    "generatetoaddress" in {
      val res = (for {
        newAddress <- NodeResponseT(zcashdCashClient.getNewAddress())
        generateResult <- NodeResponseT(zcashdCashClient.generatetoaddress(1, newAddress.address))
      } yield generateResult).value

      res.map(result => result shouldBe Symbol("right"))
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- NodeResponseT(zcashdCashClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          zcashdCashClient.getTransaction(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      transaction.map(result => result shouldBe Symbol("right"))
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- NodeResponseT(zcashdCashClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          zcashdCashClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "list since block" in {
      val listSinceBlock = (for {
        newAddress <- NodeResponseT(zcashdCashClient.getNewAddress())
        hash <- NodeResponseT(zcashdCashClient.generatetoaddress(1, newAddress.address))
        listSinceBlock <- NodeResponseT(zcashdCashClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map(result => result shouldBe Symbol("right"))
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- NodeResponseT(zcashdCashClient.getNewAddress())
        newAddress2 <- NodeResponseT(zcashdCashClient.getNewAddress())
        sendMany <- NodeResponseT(zcashdCashClient.sendMany(recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map(result => result shouldBe Symbol("right"))
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- NodeResponseT(zcashdCashClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(zcashdCashClient.getNewAddress())
        newAddress2 <- NodeResponseT(zcashdCashClient.getNewAddress())
        createRawTransaction <- NodeResponseT(
          zcashdCashClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- NodeResponseT(zcashdCashClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(zcashdCashClient.getNewAddress())
        newAddress2 <- NodeResponseT(zcashdCashClient.getNewAddress())
        sendRawTransaction <- NodeResponseT(
          zcashdCashClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "validate address" in {
      zcashdCashClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get change address" in {
      val result = zcashdCashClient.getRawChangeAddress(Some(AddressType.BECH32))
      result.map(_ shouldBe Symbol("right"))
    }
    "create new wallet in" in {
      val newWalletName = System.nanoTime().toString
      val result = zcashdCashClient.createWallet(newWalletName)
      result.map {
        case Left(_)          => throw new RuntimeException("test failed")
        case Right(newWallet) => newWallet.name shouldBe newWalletName
      }
    }
  }

  private def rawTransactionInputs(unspentTransaction: UnspentTransaction): RawTransactionInputs =
    RawTransactionInputs(List(RawTransactionInput(unspentTransaction.txid, unspentTransaction.vout)))

  private def recipients(amount: BigDecimal, addresses: GetNewAddress*): Recipients = {
    val amountToSplit = (amount - 0.01) / addresses.length
    Recipients(addresses.map(address => address.address -> amountToSplit).toMap)
  }
}
