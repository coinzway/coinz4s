package com.coinzway.coinz4s.bitcoind

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpc
import com.coinzway.coinz4s.core.rpc.estimatesmartfee.EstimateSmartFeeRpc
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses.SignedRawTransaction
import com.coinzway.coinz4s.core.rpc.walletbase.WalletBaseRpc
import com.softwaremill.sttp._

class BitcoindClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int,
    wallet: Option[String]
  )(implicit val sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindBaseRpc[R]
    with EstimateSmartFeeRpc[R]
    with WalletBaseRpc[R] {
  val client = new RpcClient(user, password, host, port, wallet)

  override def signRawTransaction(transactionHex: String)(): R[NodeResponse[SignedRawTransaction]] =
    signRawTransactionWithWallet(transactionHex)
}
