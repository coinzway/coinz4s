package com.coinzway.coinz4s.dogecoind

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.coinzway.coinz4s.core.ClientObjects._
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses.{GetNewAddress, UnspentTransaction}
import com.coinzway.coinz4s.core.NodeResponseT
import com.coinzway.coinz4s.testutils.IntegrationTestConfig
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.softwaremill.sttp.{MonadError, SttpBackend}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode

class DogecoindClientIntegrationTest extends AsyncWordSpec with Matchers with IntegrationTestConfig {
  implicit val akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val monadError: MonadError[Future] = akkaHttpBackend.responseMonad
  val dogecoindClient: DogecoindClient[Future] = new DogecoindClient(conf.user, conf.password, conf.host, conf.port)

  "DogecoindClient" should {
    "get wallet info" in {
      dogecoindClient.walletInfo.map(result => result shouldBe Symbol("right"))
    }
    "get network info" in {
      dogecoindClient.networkInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mem pool info" in {
      dogecoindClient.memPoolInfo.map(result => result shouldBe Symbol("right"))
    }
    "get blockchain info" in {
      dogecoindClient.blockchainInfo.map(result => result shouldBe Symbol("right"))
    }
    "list unspent transactions" in {
      dogecoindClient.listUnspentTransactions().map(result => result shouldBe Symbol("right"))
    }
    "get new address" in {
      dogecoindClient.getNewAddress().map(result => result shouldBe Symbol("right"))
    }
    "send to address" in {
      val sendToAddress = (for {
        newAddress <- NodeResponseT(dogecoindClient.getNewAddress())
        sendToAddress <- NodeResponseT(dogecoindClient.sendToAddress(newAddress.address, 1000, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map(result => result shouldBe Symbol("right"))
    }
    "set tx fee" in {
      dogecoindClient.setTxFee(0.05).map(result => result shouldBe Symbol("right"))
    }
    "generatetoaddress" in {
      val res = (for {
        newAddress <- NodeResponseT(dogecoindClient.getNewAddress())
        generateResult <- NodeResponseT(dogecoindClient.generatetoaddress(1, newAddress.address))
      } yield generateResult).value

      res.map(result => result shouldBe Symbol("right"))
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- NodeResponseT(dogecoindClient.listUnspentTransactions())
        transaction <- NodeResponseT(dogecoindClient.getTransaction(unspentTransaction.unspentTransactions.head.txid))
      } yield transaction).value

      transaction.map(result => result shouldBe Symbol("right"))
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- NodeResponseT(dogecoindClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          dogecoindClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "list since block" in {
      val listSinceBlock = (for {
        newAddress <- NodeResponseT(dogecoindClient.getNewAddress())
        hash <- NodeResponseT(dogecoindClient.generatetoaddress(1, newAddress.address))
        listSinceBlock <- NodeResponseT(dogecoindClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map(result => result shouldBe Symbol("right"))
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- NodeResponseT(dogecoindClient.getNewAddress())
        newAddress2 <- NodeResponseT(dogecoindClient.getNewAddress())
        sendMany <- NodeResponseT(dogecoindClient.sendMany(recipients(10000, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map(result => result shouldBe Symbol("right"))
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- NodeResponseT(dogecoindClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(dogecoindClient.getNewAddress())
        newAddress2 <- NodeResponseT(dogecoindClient.getNewAddress())
        createRawTransaction <- NodeResponseT(
          dogecoindClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- NodeResponseT(dogecoindClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(dogecoindClient.getNewAddress())
        newAddress2 <- NodeResponseT(dogecoindClient.getNewAddress())
        sendRawTransaction <- NodeResponseT(
          dogecoindClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "validate address" in {
      dogecoindClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get change address" in {
      val result = dogecoindClient.getRawChangeAddress(Some(AddressType.BECH32))
      result.map(_ shouldBe Symbol("right"))
    }
  }

  private def rawTransactionInputs(unspentTransaction: UnspentTransaction): RawTransactionInputs =
    RawTransactionInputs(List(RawTransactionInput(unspentTransaction.txid, unspentTransaction.vout)))

  private def recipients(amount: BigDecimal, addresses: GetNewAddress*): Recipients = {
    val amountToSplit = ((amount - 0.01) / addresses.length).setScale(8, RoundingMode.DOWN)
    Recipients(addresses.map(address => address.address -> amountToSplit).toMap)
  }
}
