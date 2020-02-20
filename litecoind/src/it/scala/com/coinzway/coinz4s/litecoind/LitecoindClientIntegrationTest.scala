package com.coinzway.coinz4s.litecoind

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

class LitecoindClientIntegrationTest extends AsyncWordSpec with Matchers {
  implicit val akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val monadError: MonadError[Future] = akkaHttpBackend.responseMonad
  val litecoindClient: LitecoindClient[Future] = new LitecoindClient("user", "password", "litecoind", 19332)

  "LitecoindClient" should {
    "get wallet info" in {
      litecoindClient.walletInfo.map(result => result shouldBe Symbol("right"))
    }
    "get network info" in {
      litecoindClient.networkInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mining info" in {
      litecoindClient.miningInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mem pool info" in {
      litecoindClient.memPoolInfo.map(result => result shouldBe Symbol("right"))
    }
    "get blockchain info" in {
      litecoindClient.blockchainInfo.map(result => result shouldBe Symbol("right"))
    }
    "estimate smart fee" in {
      litecoindClient.estimateSmartFee(6, Some(EstimateMode.CONSERVATIVE)).map { result =>
        result shouldBe Symbol("right")
      }
    }
    "list unspent transactions" in {
      litecoindClient.listUnspentTransactions().map(result => result shouldBe Symbol("right"))
    }
    "get new address" in {
      litecoindClient.getNewAddress().map(result => result shouldBe Symbol("right"))
    }
    "get new address with type" in {
      litecoindClient.getNewAddress(None, Some(AddressType.LEGACY)).map(result => result shouldBe Symbol("right"))
    }
    "send to address" in {
      val sendToAddress = (for {
        newAddress <- NodeResponseT(litecoindClient.getNewAddress())
        sendToAddress <- NodeResponseT(litecoindClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map(result => result shouldBe Symbol("right"))
    }
    "set tx fee" in {
      litecoindClient.setTxFee(0.05).map(result => result shouldBe Symbol("right"))
    }
    "generatetoaddress" in {
      val res = (for {
        newAddress <- NodeResponseT(litecoindClient.getNewAddress())
        generateResult <- NodeResponseT(litecoindClient.generatetoaddress(1, newAddress.address))
      } yield generateResult).value

      res.map(result => result shouldBe Symbol("right"))
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- NodeResponseT(litecoindClient.listUnspentTransactions())
        transaction <- NodeResponseT(litecoindClient.getTransaction(unspentTransaction.unspentTransactions.head.txid))
      } yield transaction).value

      transaction.map(result => result shouldBe Symbol("right"))
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- NodeResponseT(litecoindClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          litecoindClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "list since block" in {
      val listSinceBlock = (for {
        newAddress <- NodeResponseT(litecoindClient.getNewAddress())
        hash <- NodeResponseT(litecoindClient.generatetoaddress(1, newAddress.address))
        listSinceBlock <- NodeResponseT(litecoindClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map(result => result shouldBe Symbol("right"))
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- NodeResponseT(litecoindClient.getNewAddress())
        newAddress2 <- NodeResponseT(litecoindClient.getNewAddress())
        sendMany <- NodeResponseT(litecoindClient.sendMany(recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map(result => result shouldBe Symbol("right"))
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- NodeResponseT(litecoindClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(litecoindClient.getNewAddress())
        newAddress2 <- NodeResponseT(litecoindClient.getNewAddress())
        createRawTransaction <- NodeResponseT(
          litecoindClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- NodeResponseT(litecoindClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(litecoindClient.getNewAddress())
        newAddress2 <- NodeResponseT(litecoindClient.getNewAddress())
        sendRawTransaction <- NodeResponseT(
          litecoindClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "validate address" in {
      litecoindClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get change address" in {
      val result = litecoindClient.getRawChangeAddress(Some(AddressType.BECH32))
      result.map(_ shouldBe Symbol("right"))
    }
    "create new wallet in" in {
      val newWalletName = System.nanoTime().toString
      val result = litecoindClient.createWallet(newWalletName)
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
