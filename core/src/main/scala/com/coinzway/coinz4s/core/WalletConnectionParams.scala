package com.coinzway.coinz4s.core

case class Conf(wallet: WalletConnectionParams)

case class WalletConnectionParams(
    user: String,
    password: String,
    host: String,
    port: Int)
