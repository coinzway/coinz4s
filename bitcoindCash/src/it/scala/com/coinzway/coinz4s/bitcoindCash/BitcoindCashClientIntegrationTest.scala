package com.coinzway.coinz4s.bitcoindCash

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

class BitcoindCashClientIntegrationTest extends AsyncWordSpec with Matchers {
  implicit val akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val monadError: MonadError[Future] = akkaHttpBackend.responseMonad

  val bitcoindCashClient: BitcoindCashClient[Future] =
    new BitcoindCashClient("user", "password", "bitcoind_cash", 18443)

  "BitcoindCashClient" should {
    "get wallet info" in {
      bitcoindCashClient.walletInfo.map(result => result shouldBe Symbol("right"))
    }
    "get network info" in {
      bitcoindCashClient.networkInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mem pool info" in {
      bitcoindCashClient.memPoolInfo.map(result => result shouldBe Symbol("right"))
    }
    "get blockchain info" in {
      bitcoindCashClient.blockchainInfo.map(result => result shouldBe Symbol("right"))
    }
    "list unspent transactions" in {
      bitcoindCashClient.listUnspentTransactions().map(result => result shouldBe Symbol("right"))
    }
    "get new address" in {
      bitcoindCashClient.getNewAddress().map(result => result shouldBe Symbol("right"))
    }
    "get new address with type" in {
      bitcoindCashClient.getNewAddress(None, Some(AddressType.LEGACY)).map(result => result shouldBe Symbol("right"))
    }
    "send to address" in {
      val sendToAddress = (for {
        newAddress <- NodeResponseT(bitcoindCashClient.getNewAddress())
        sendToAddress <- NodeResponseT(bitcoindCashClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map(result => result shouldBe Symbol("right"))
    }
    "set tx fee" in {
      bitcoindCashClient.setTxFee(0.05).map(result => result shouldBe Symbol("right"))
    }
    "generatetoaddress" in {
      val res = (for {
        newAddress <- NodeResponseT(bitcoindCashClient.getNewAddress())
        generateResult <- NodeResponseT(bitcoindCashClient.generatetoaddress(1, newAddress.address))
      } yield generateResult).value

      res.map(result => result shouldBe Symbol("right"))
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- NodeResponseT(bitcoindCashClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          bitcoindCashClient.getTransaction(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      transaction.map(result => result shouldBe Symbol("right"))
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- NodeResponseT(bitcoindCashClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          bitcoindCashClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "list since block" in {
      val listSinceBlock = (for {
        newAddress <- NodeResponseT(bitcoindCashClient.getNewAddress())
        hash <- NodeResponseT(bitcoindCashClient.generatetoaddress(1, newAddress.address))
        listSinceBlock <- NodeResponseT(bitcoindCashClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map(result => result shouldBe Symbol("right"))
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- NodeResponseT(bitcoindCashClient.getNewAddress())
        newAddress2 <- NodeResponseT(bitcoindCashClient.getNewAddress())
        sendMany <- NodeResponseT(bitcoindCashClient.sendMany(recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map(result => result shouldBe Symbol("right"))
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- NodeResponseT(bitcoindCashClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(bitcoindCashClient.getNewAddress())
        newAddress2 <- NodeResponseT(bitcoindCashClient.getNewAddress())
        createRawTransaction <- NodeResponseT(
          bitcoindCashClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- NodeResponseT(bitcoindCashClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(bitcoindCashClient.getNewAddress())
        newAddress2 <- NodeResponseT(bitcoindCashClient.getNewAddress())
        sendRawTransaction <- NodeResponseT(
          bitcoindCashClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "validate address" in {
      bitcoindCashClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get change address" in {
      val result = bitcoindCashClient.getRawChangeAddress()
      result.map(_ shouldBe Symbol("right"))
    }
    "create new wallet in" in {
      val newWalletName = System.nanoTime().toString
      val result = bitcoindCashClient.createWallet(newWalletName)
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
