package com.fullfacing.keycloak4s.adapters.akka.http.directives

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives._
import com.fullfacing.keycloak4s.adapters.akka.http.Errors
import com.fullfacing.keycloak4s.adapters.akka.http.models.{ResourceRoles, ResourceAccess}
import com.fullfacing.keycloak4s.adapters.akka.http.services.TokenValidator
import com.fullfacing.keycloak4s.client.serialization.JsonFormats.default
import com.nimbusds.jose.Payload
import org.json4s.jackson.JsonMethods.parse

import scala.util.{Failure, Success, Try}

trait ValidationDirective {

  /**
   * Token Validation directive to secure all inner directives.
   * Extracts the token and has it validated by the implicit instance of the TokenValidator.
   * The resource_access field is extracted from the token and provided for authorisation on
   * on inner directives.
   *
   * @return  Directive with verified user's permissions
   */
  def validateToken(implicit tv: TokenValidator): Directive1[ResourceAccess] = {
    extractCredentials.flatMap {
      case Some(token) => callValidation(token.token())
      case None        => complete(Errors.errorResponse(StatusCodes.Unauthorized.intValue, "No token provided"))
    }
  }

  /** Runs the validation function. */
  private def callValidation(token: String)(implicit validator: TokenValidator): Directive1[ResourceAccess] = {
    onComplete(validator.validate(token).unsafeToFuture()).flatMap {
      case Success(r) => handleValidationResponse(r)
      case Failure(e) => complete(Errors.errorResponse(StatusCodes.InternalServerError.intValue, "An unexpected error occurred", Some(e.getMessage)))
    }
  }

  /** Handles the success/failure of the token validation. */
  private def handleValidationResponse(response: Either[Throwable, Payload]): Directive1[ResourceAccess] = response match {
    case Right(r) => provide(getUserPermissions(r))
    case Left(t)  => complete(Errors.errorResponse(StatusCodes.Unauthorized.intValue, t.getMessage))
  }

  /** Gets the resource_access field from the token and parses it into the Permissions object */
  private def getUserPermissions(result: Payload): ResourceAccess = {
    val json = parse(result.toString)

    val access: Map[String, ResourceRoles] = Try {
      (json \\ "resource_access").extract[Map[String, ResourceRoles]]
    }.getOrElse(Map.empty)

    ResourceAccess(access)
  }
}
