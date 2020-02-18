package com.coinzway.coinz4s.btc

final case class Address(
    address: String,
    port: Int,
    score: Int)

final case class Network(
    name: String,
    limited: Boolean,
    reachable: Boolean,
    proxy: String,
    localaddress: Option[Vector[Address]])

final case class Softfork(
    id: String,
    version: Int,
    enforce: Option[Int],
    status: Boolean,
    found: Option[Int],
    required: Option[Int],
    window: Option[Int])
