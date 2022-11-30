package com.fullfacing.keycloak4s.admin.services

import com.fullfacing.keycloak4s.admin.client.KeycloakClient
import com.fullfacing.keycloak4s.core.models.{BruteForceResponse, KeycloakError}
import zio.IO

import java.util.UUID

case class AttackDetection(client: KeycloakClient) {

  /** Clear any login failures for all users. This can release temporary disabled users. */
  def clearAllLoginFailures(realm: String = client.realm): IO[KeycloakError, Unit] = {
    val path: Path = Seq(realm, "attack-detection", "brute-force", "users")
    client.delete[Unit](path)
  }

  /** Retrieves login failure details for a specified user. */
  def fetchUserStatus(userId: UUID, realm: String = client.realm): IO[KeycloakError, BruteForceResponse] = {
    val path: Path = Seq(realm, "attack-detection", "brute-force", "users", userId)
    client.get[BruteForceResponse](path)
  }

  /** Clear any login failures for a specified user. */
  def clearUserLoginFailure(userId: UUID, realm: String = client.realm): IO[KeycloakError, KeycloakError, Unit] = {
    val path: Path = Seq(realm, "attack-detection", "brute-force", "users", userId)
    client.delete[Unit](path)
  }
}