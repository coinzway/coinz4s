package com.coinzway.coinz4s.core.rpc.noWalletbase

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcJsonFormats
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses.SignedRawTransaction
import com.softwaremill.sttp.SttpBackend

trait NoWalletBaseRpc[R[_]] extends BitcoindBaseRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def signRawTransaction(transactionHex: String)(): R[NodeResponse[SignedRawTransaction]] =
    client.request[SignedRawTransaction]("signrawtransaction", Vector(transactionHex))
}
