package com.fullfacing.keycloak4s.services

import cats.effect.Effect
import com.fullfacing.keycloak4s.client.KeycloakClient
import com.fullfacing.keycloak4s.models.{ManagementPermission, Role, User}
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue

import scala.collection.immutable.Seq

class Roles[R[_]: Effect, S](implicit keyCloakClient: KeycloakClient[R, S]) {

  private val clients_path = "clients"
  private val roles_path   = "roles"

  /**
   * Create a new role for the realm or client
   *
   * @param realm realm name (not id!)
   * @param id    id of client (not client-id)
   * @param role
   * @return
   */
  def createClientRole(realm: String, id: String, role: Role): R[???] = {
    val path = Seq(realm, clients_path, id, roles_path)
    keyCloakClient.post[Role, ???](role, path)
  }

  /**
   * Get all roles for the realm or client
   *
   * @param realm realm name (not id!)
   * @param id    id of client (not client-id)
   * @return
   */
  def fetch(realm: String, id: String): R[List[Role]] = {
    val path = Seq(realm, clients_path, id, roles_path)
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Get a role by name
   *
   * @param realm     realm name (not id!)
   * @param id        id of client (not client-id)
   * @param roleName  role’s name (not id!)
   * @return
   */
  def getByName(realm: String, id: String, roleName: String): R[Role] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName)
    keyCloakClient.get[Role](path)
  }

  /**
   * Update a role by name
   *
   * @param realm     realm name (not id!)
   * @param id        id of client (not client-id)
   * @param roleName  role’s name (not id!)
   * @param role
   * @return
   */
  def updateByName(realm: String, id: String, roleName: String, role: Role): R[???] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName)
    keyCloakClient.put[Role, ???](role, path, Seq.empty[KeyValue])
  }

  /**
   * Delete a role by name
   *
   * @param realm     realm name (not id!)
   * @param id        id of client (not client-id)
   * @param roleName  role’s name (not id!)
   * @return
   */
  def removeByName(realm: String, id: String, roleName: String): R[Unit] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName)
    keyCloakClient.delete[Unit](path, Seq.empty[KeyValue])
  }

  /**
   * Add a composite to the role
   *
   * @param realm     realm name (not id!)
   * @param id        id of client (not client-id)
   * @param roleName  role’s name (not id!)
   * @param roles
   * @return
   */
  def addComposites(realm: String, id: String, roleName: String, roles: List[Role]): R[Unit] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "composites")
    keyCloakClient.post[List[Role], Unit](roles, path)
  }

  /**
   * Get composites of the role
   *
   * @param realm     realm name (not id!)
   * @param id        id of client (not client-id)
   * @param roleName  role’s name (not id!)
   * @return
   */
  def fetchRoleComposites(realm: String, id: String, roleName: String): R[List[Role]] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "composites")
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Remove roles from the role’s composite
   *
   * @param realm    realm name (not id!)
   * @param id       id of client (not client-id)
   * @param roleName role’s name (not id!)
   * @param roles    roles to remove
   * @return
   */
  def removeCompositeRoles(realm: String, id: String, roleName: String, roles: List[Role]): R[Unit] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "composites")
    keyCloakClient.delete[List[Role], Unit](roles, path, Seq.empty[KeyValue])
  }

  /**
   * An app-level roles for the specified app for the role’s composite
   *
   * @param realm    realm name (not id!)
   * @param id       id of client (not client-id)
   * @param roleName role’s name (not id!)
   * @param client
   * @return
   */
  def fetchCompositesAppLevelRoles(realm: String, id: String, roleName: String, client: String): R[List[Role]] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "composites", "clients", client)
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Get realm-level roles of the role’s composite
   *
   * @param realm    realm name (not id!)
   * @param id       id of client (not client-id)
   * @param roleName role’s name (not id!)
   * @return
   */
  def fetchCompositesRealmLevelRoles(realm: String, id: String, roleName: String): R[List[Role]] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "composites", "realm")
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Return object stating whether role Authorisation permissions have been initialized or not and a reference
   *
   * @param realm    realm name (not id!)
   * @param id       id of client (not client-id)
   * @param roleName role’s name (not id!)
   * @return
   */
  def roleAuthPermissionsInitialised(realm: String, id: String, roleName: String): R[ManagementPermission] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "management", "permissions")
    keyCloakClient.get[ManagementPermission](path)
  }

  /**
   *
   * @param realm    realm name (not id!)
   * @param id       id of client (not client-id)
   * @param roleName role’s name (not id!)
   * @param ref
   * @return
   */
  def setAuthPermissions(realm: String, id: String, roleName: String, ref: ManagementPermission): R[ManagementPermission] = { // TODO Determine functionality
    val path = Seq(realm, clients_path, id, roles_path, roleName, "management", "permissions")
    keyCloakClient.put[ManagementPermission, ManagementPermission](ref, path, Seq.empty[KeyValue])
  }

  /**
   * Return List of Users that have the specified role name
   *
   * @param realm    realm name (not id!)
   * @param id       id of client (not client-id)
   * @param roleName role’s name (not id!)
   * @param first
   * @param max
   * @return
   */
  def usersByRoleName(realm: String, id: String, roleName: String, first: Option[Int], max: Option[Int]): R[List[User]] = {
    val path = Seq(realm, clients_path, id, roles_path, roleName, "users")
    val query = createQuery(("first", first), ("max", max))
    keyCloakClient.get[List[User]](path, query)
  }

  /**
   * Create a new role for the realm or client
   *
   * @param realm Realm name
   * @param role
   * @return
   */
  def createRealmRole(realm: String, role: Role): R[???] = {
    val path = Seq(realm, roles_path)
    keyCloakClient.post[???](role, path)
  }

  /**
   * Get all roles for the realm or client
   *
   * @param realm Realm name
   * @return
   */
  def fetchRealmRoles(realm: String): R[List[Role]] = {
    val path = Seq(realm, roles_path)
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Get a role by name
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @return
   */
  def getRealmRoleByRoleName(realm: String, roleName: String): R[Role] = {
    val path = Seq(realm, roles_path, roleName)
    keyCloakClient.get[Role](path)
  }

  /**
   * Update a role by name
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @param role     Updated role
   * @return
   */
  def updateRealmRoleByName(realm: String, roleName: String, role: Role): R[???] = {
    val path = Seq(realm, roles_path, roleName)
    keyCloakClient.put[Role, ???](role, path)
  }

  /**
   * Delete a role by name
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @param role     Role to be deleted
   * @return
   */
  def deleteRealmRoleByName(realm: String, roleName: String, role: String): R[Unit] = {
    val path = Seq(realm, roles_path, roleName)
    keyCloakClient.delete[String, Unit](role, path, Seq.empty[KeyValue])
  }

  /**
   * Add a composite to the role
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @param roles    Composite roles to be added
   * @return
   */
  def addCompositeToRealmRole(realm: String, roleName: String, roles: List[Role]): R[Unit] = {
    val path = Seq(realm, roles_path, roleName, "composites")
    keyCloakClient.post[List[Role], Unit](roles, path)
  }

  /**
   * Get composites of the role
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @return
   */
  def getRealmRoleComposites(realm: String, roleName: String): R[List[Role]] = {
    val path = Seq(realm, roles_path, roleName, "composites")
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Remove roles from the role’s composite
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @param roles    roles to be removed
   * @return
   */
  def removeRolesFromRolesComposite(realm: String, roleName: String, roles: List[Role]): R[Unit] = {
    val path = Seq(realm, roles_path, roleName, "composites")
    keyCloakClient.delete[List[Role], Unit](roles, path, Seq.empty[KeyValue])
  }

  /**
   * An app-level roles for the specified app for the role’s composite
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @param client
   * @return
   */
  def fetchRolesCompositesAppLevelRoles(realm: String, roleName: String, client: String): R[List[Role]] = {
    val path = Seq(realm, roles_path, roleName, "composites", "clients", client)
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Get realm-level roles of the role’s composite
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @return
   */
  def fetchRolesCompositeRealmLevelRoles(realm: String, roleName: String): R[List[Role]] = {
    val path = Seq(realm, roles_path, roleName, "composites", "realm")
    keyCloakClient.get[List[Role]](path)
  }

  /**
   * Return object stating whether role Authoirzation permissions have been initialized or not and a reference
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @return
   */
  def realmRoleAuthInitialised(realm: String, roleName: String): R[ManagementPermission] = {
    val path = Seq(realm, roles_path, roleName, "management", "permissions")
    keyCloakClient.get[ManagementPermission](path)
  }

//  /**
//   * To be determined
//   *
//   * @param realm    realm name (not id!)
//   * @param roleName role’s name (not id!)
//   * @param ref
//   * @return
//   */
//  def TBD(realm: String, roleName: String, ref: ManagementPermission): R[ManagementPermission] = { //TODO determine functionality
//    val path = Seq(realm, roles_path, roleName, "management", "permissions")
//    keyCloakClient.put[ManagementPermission, ManagementPermission](ref, path, Seq.empty[KeyValue])
//  }

  /**
   * Return List of Users that have the specified role name
   *
   * @param realm    realm name (not id!)
   * @param roleName role’s name (not id!)
   * @param first
   * @param max
   * @return
   */
  def fetchUsersByRoleName(realm: String, roleName: String, first: Option[Int], max: Option[Int]): R[User] = {
    val path  = Seq(realm, roles_path, roleName, "users")
    val query = createQuery(("first", first), ("max", max))
    keyCloakClient.get[User](path, query)
  }
}
