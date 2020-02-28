package com.coinzway.coinz4s.dashd

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.coinzway.coinz4s.bitcoind.ClientObjects._
import com.coinzway.coinz4s.bitcoind.Responses.{GetNewAddress, UnspentTransaction}
import com.coinzway.coinz4s.core.NodeResponseT
import com.coinzway.coinz4s.testutils.IntegrationTestConfig
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.{MonadError, SttpBackend}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class DashdClientIntegrationTest extends AsyncWordSpec with Matchers with IntegrationTestConfig {
  implicit val akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val monadError: MonadError[Future] = akkaHttpBackend.responseMonad
  val dashdClient: DashdClient[Future] = new DashdClient(conf.user, conf.password, conf.host, conf.port)

  "DashdClient" should {
    "get wallet info" in {
      dashdClient.walletInfo.map(result => result shouldBe Symbol("right"))
    }
    "get network info" in {
      dashdClient.networkInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mem pool info" in {
      dashdClient.memPoolInfo.map(result => result shouldBe Symbol("right"))
    }
    "get blockchain info" in {
      dashdClient.blockchainInfo.map(result => result shouldBe Symbol("right"))
    }
    "estimate smart fee" in {
      dashdClient.estimateSmartFee(6, Some(EstimateMode.CONSERVATIVE)).map(result => result shouldBe Symbol("right"))
    }
    "list unspent transactions" in {
      dashdClient.listUnspentTransactions().map(result => result shouldBe Symbol("right"))
    }
    "get new address" in {
      dashdClient.getNewAddress().map(result => result shouldBe Symbol("right"))
    }
    "send to address" in {
      val sendToAddress = (for {
        newAddress <- NodeResponseT(dashdClient.getNewAddress())
        sendToAddress <- NodeResponseT(dashdClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map(result => result shouldBe Symbol("right"))
    }
    "set tx fee" in {
      dashdClient.setTxFee(0.05).map(result => result shouldBe Symbol("right"))
    }
    "generatetoaddress" in {
      val res = (for {
        newAddress <- NodeResponseT(dashdClient.getNewAddress())
        generateResult <- NodeResponseT(dashdClient.generatetoaddress(1, newAddress.address))
      } yield generateResult).value

      res.map(result => result shouldBe Symbol("right"))
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- NodeResponseT(dashdClient.listUnspentTransactions())
        transaction <- NodeResponseT(dashdClient.getTransaction(unspentTransaction.unspentTransactions.head.txid))
      } yield transaction).value

      transaction.map(result => result shouldBe Symbol("right"))
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- NodeResponseT(dashdClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          dashdClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "list since block" in {
      val listSinceBlock = (for {
        newAddress <- NodeResponseT(dashdClient.getNewAddress())
        hash <- NodeResponseT(dashdClient.generatetoaddress(1, newAddress.address))
        listSinceBlock <- NodeResponseT(dashdClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map(result => result shouldBe Symbol("right"))
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- NodeResponseT(dashdClient.getNewAddress())
        newAddress2 <- NodeResponseT(dashdClient.getNewAddress())
        sendMany <- NodeResponseT(dashdClient.sendMany(recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map(result => result shouldBe Symbol("right"))
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- NodeResponseT(dashdClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(dashdClient.getNewAddress())
        newAddress2 <- NodeResponseT(dashdClient.getNewAddress())
        createRawTransaction <- NodeResponseT(
          dashdClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- NodeResponseT(dashdClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(dashdClient.getNewAddress())
        newAddress2 <- NodeResponseT(dashdClient.getNewAddress())
        sendRawTransaction <- NodeResponseT(
          dashdClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "validate address" in {
      dashdClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get change address" in {
      val result = dashdClient.getRawChangeAddress()
      result.map(_ shouldBe Symbol("right"))
    }
  }

  private def rawTransactionInputs(unspentTransaction: UnspentTransaction): RawTransactionInputs =
    RawTransactionInputs(List(RawTransactionInput(unspentTransaction.txid, unspentTransaction.vout)))

  private def recipients(amount: BigDecimal, addresses: GetNewAddress*): Recipients = {
    val amountToSplit = (amount - 0.01) / addresses.length
    Recipients(addresses.map(address => address.address -> amountToSplit).toMap)
  }
}
