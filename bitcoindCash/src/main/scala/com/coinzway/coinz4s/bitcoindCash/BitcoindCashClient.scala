package com.coinzway.coinz4s.bitcoindCash

import com.coinzway.coinz4s.core.BaseResponses.NodeResponse
import com.coinzway.coinz4s.core.rpc.RpcClient
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpc
import com.coinzway.coinz4s.core.rpc.bitcoindbase.BitcoindBaseRpcResponses.SignedRawTransaction
import com.coinzway.coinz4s.core.rpc.generatetoaddress.GenerateToAddressRpc
import com.coinzway.coinz4s.core.rpc.walletbase.WalletBaseRpc
import com.softwaremill.sttp.SttpBackend

class BitcoindCashClient[R[_]](
    user: String,
    password: String,
    host: String,
    port: Int,
    wallet: Option[String]
  )(implicit val sttpBackend: SttpBackend[R, Nothing])
    extends BitcoindBaseRpc[R]
    with WalletBaseRpc[R]
    with GenerateToAddressRpc[R] {
  val client = new RpcClient(user, password, host, port, wallet)

  override def signRawTransaction(transactionHex: String)(): R[NodeResponse[SignedRawTransaction]] =
    signRawTransactionWithWallet(transactionHex)

}
