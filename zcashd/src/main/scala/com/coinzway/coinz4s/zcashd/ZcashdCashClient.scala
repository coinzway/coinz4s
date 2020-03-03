package com.coinzway.coinz4s.zcashd

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.{BitcoindBaseRpc, BitcoindBaseRpcResponses}
import com.coinzway.coinz4s.core.rpc.estimatesmartfee.EstimateSmartFeeRpc
import com.coinzway.coinz4s.core.rpc.generate.GenerateRpc
import com.coinzway.coinz4s.core.rpc.getrawtransactionone.GetRawTransactionOneRpc
import com.coinzway.coinz4s.core.rpc.noWalletbase.NoWalletBaseRpc
import com.softwaremill.sttp.SttpBackend

class ZcashdCashClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int
  )(implicit val sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindBaseRpc[R]
    with EstimateSmartFeeRpc[R]
    with NoWalletBaseRpc[R]
    with GenerateRpc[R]
    with GetRawTransactionOneRpc[R] {
  override val client = new RpcClient(user, password, host, port, None)

  override def getRawTransactionVerbose(txid: String)(): R[NodeResponse[BitcoindBaseRpcResponses.RawTransaction]] =
    super.getRawTransactionVerbose(txid)()
}
