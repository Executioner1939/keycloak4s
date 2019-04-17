package com.fullfacing.keycloak4s.adapters.akka.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fullfacing.keycloak4s.adapters.akka.http.Implicits._
import com.fullfacing.keycloak4s.adapters.akka.http.directives.{AuthorisationDirectives, ValidationDirective}

object HttpApi extends ValidationDirective with AuthorisationDirectives {

  val api: Route = validateToken(tv) { implicit p =>
    path("cars") {
      authoriseResource("cars") { m =>
        get(m) {
          complete(s"GET /cars \n $p")
        } ~
        post(m) {
          complete(s"POST /cars \n $p")
        } ~
        put(m) {
          complete(s"PUT /cars \n $p")
        } ~
        patch(m) {
          complete(s"PATCH /cars \n $p")
        } ~
        delete(m) {
          complete(s"DELETE /cars \n $p")
        }
      }
    } ~
    path("planes") {
      withAuth("planes") {
        complete(s"/planes $p")
      }
    }
  }
}