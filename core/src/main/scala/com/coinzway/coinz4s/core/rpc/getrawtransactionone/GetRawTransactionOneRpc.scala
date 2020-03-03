package com.coinzway.coinz4s.core.rpc.getrawtransactionone

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcJsonFormats
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses.RawTransaction
import com.softwaremill.sttp.SttpBackend

trait GetRawTransactionOneRpc[R[_]] extends BitcoindBaseRpcJsonFormats {
  val client: RpcClient[R]
  implicit val sttpBackend: SttpBackend[R, Nothing]

  def getRawTransactionVerbose(txid: String)(): R[NodeResponse[RawTransaction]] =
    client.request[RawTransaction]("getrawtransaction", Vector(txid, 1))

}
