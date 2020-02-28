package com.coinzway.coinz4s.core.rpc.walletbase

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcJsonFormats
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses.{CreateWallet, SignedRawTransaction}
import com.softwaremill.sttp.SttpBackend

trait WalletBaseRpc[R[_]] extends BitcoindBaseRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def signRawTransactionWithWallet(transactionHex: String)(): R[NodeResponse[SignedRawTransaction]] =
    client.request[SignedRawTransaction]("signrawtransactionwithwallet", Vector(transactionHex))

  def createWallet(walletName: String): R[NodeResponse[CreateWallet]] =
    client.request[CreateWallet]("createwallet", Vector(walletName))
}
