package com.coinzway.coinz4s.bitcoind

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

class BitcoindClientIntegrationTest extends AsyncWordSpec with Matchers with IntegrationTestConfig {
  implicit val akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]] = AkkaHttpBackend()
  implicit val monadError: MonadError[Future] = akkaHttpBackend.responseMonad

  val bitcoinClient: BitcoindClient[Future] =
    new BitcoindClient(conf.user, conf.password, conf.host, conf.port, Some(""))

  "BitcoindClient" should {
    "get wallet info" in {
      bitcoinClient.walletInfo.map(result => result shouldBe Symbol("right"))
    }
    "get network info" in {
      bitcoinClient.networkInfo.map(result => result shouldBe Symbol("right"))
    }
    "get mem pool info" in {
      bitcoinClient.memPoolInfo.map(result => result shouldBe Symbol("right"))
    }
    "get blockchain info" in {
      bitcoinClient.blockchainInfo.map(result => result shouldBe Symbol("right"))
    }
    "estimate smart fee" in {
      bitcoinClient.estimateSmartFee(6, Some(EstimateMode.CONSERVATIVE)).map { result =>
        result shouldBe Symbol("right")
      }
    }
    "list unspent transactions" in {
      bitcoinClient.listUnspentTransactions().map(result => result shouldBe Symbol("right"))
    }
    "get new address" in {
      bitcoinClient.getNewAddress().map(result => result shouldBe Symbol("right"))
    }
    "get new address with type" in {
      bitcoinClient.getNewAddress(None, Some(AddressType.LEGACY)).map(result => result shouldBe Symbol("right"))
    }
    "send to address" in {
      val sendToAddress = (for {
        newAddress <- NodeResponseT(bitcoinClient.getNewAddress())
        sendToAddress <- NodeResponseT(bitcoinClient.sendToAddress(newAddress.address, 10, "comment", "commentTo"))
      } yield sendToAddress).value

      sendToAddress.map(result => result shouldBe Symbol("right"))
    }
    "set tx fee" in {
      bitcoinClient.setTxFee(0.05).map(result => result shouldBe Symbol("right"))
    }
    "generatetoaddress" in {
      val res = (for {
        newAddress <- NodeResponseT(bitcoinClient.getNewAddress())
        generateResult <- NodeResponseT(bitcoinClient.generatetoaddress(1, newAddress.address))
      } yield generateResult).value

      res.map(result => result shouldBe Symbol("right"))
    }
    "get transaction" in {
      val transaction = (for {
        unspentTransaction <- NodeResponseT(bitcoinClient.listUnspentTransactions())
        transaction <- NodeResponseT(bitcoinClient.getTransaction(unspentTransaction.unspentTransactions.head.txid))
      } yield transaction).value

      transaction.map(result => result shouldBe Symbol("right"))
    }
    "get raw transaction" in {
      val rawTransaction = (for {
        unspentTransaction <- NodeResponseT(bitcoinClient.listUnspentTransactions())
        transaction <- NodeResponseT(
          bitcoinClient.getRawTransactionVerbose(unspentTransaction.unspentTransactions.head.txid)
        )
      } yield transaction).value

      rawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "list since block" in {
      val listSinceBlock = (for {
        newAddress <- NodeResponseT(bitcoinClient.getNewAddress())
        hash <- NodeResponseT(bitcoinClient.generatetoaddress(1, newAddress.address))
        listSinceBlock <- NodeResponseT(bitcoinClient.listSinceBlock(hash.hashes.head))
      } yield listSinceBlock).value

      listSinceBlock.map(result => result shouldBe Symbol("right"))
    }
    "send many" in {
      val sendMany = (for {
        newAddress1 <- NodeResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- NodeResponseT(bitcoinClient.getNewAddress())
        sendMany <- NodeResponseT(bitcoinClient.sendMany(recipients(1, newAddress1, newAddress2)))
      } yield sendMany).value

      sendMany.map(result => result shouldBe Symbol("right"))
    }
    "create raw transaction" in {
      val createRawTransaction = (for {
        input <- NodeResponseT(bitcoinClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- NodeResponseT(bitcoinClient.getNewAddress())
        createRawTransaction <- NodeResponseT(
          bitcoinClient.createRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield createRawTransaction).value

      createRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "send raw transaction" in {
      val sendRawTransaction = (for {
        input <- NodeResponseT(bitcoinClient.listUnspentTransactions())
        newAddress1 <- NodeResponseT(bitcoinClient.getNewAddress())
        newAddress2 <- NodeResponseT(bitcoinClient.getNewAddress())
        sendRawTransaction <- NodeResponseT(
          bitcoinClient.sendRawTransaction(
            rawTransactionInputs(input.unspentTransactions.head),
            recipients(input.unspentTransactions.head.amount, newAddress1, newAddress2)
          )
        )
      } yield sendRawTransaction).value

      sendRawTransaction.map(result => result shouldBe Symbol("right"))
    }
    "validate address" in {
      bitcoinClient.validateAddress("bcrt1qahztuh9phvwj8auphfeqsw5hfhphssjf3mze8k").map { result =>
        result shouldBe Symbol("right")
      }
    }
    "get change address" in {
      val result = bitcoinClient.getRawChangeAddress(Some(AddressType.BECH32))
      result.map(_ shouldBe Symbol("right"))
    }
    "create new wallet in" in {
      val newWalletName = System.nanoTime().toString
      val result = bitcoinClient.createWallet(newWalletName)
      result.map {
        case Left(_) => throw new RuntimeException("test failed")
        case Right(newWallet) =>
          newWallet.name shouldBe newWalletName
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
