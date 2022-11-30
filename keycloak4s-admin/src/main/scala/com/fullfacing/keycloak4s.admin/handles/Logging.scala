package com.fullfacing.keycloak4s.admin.handles

import java.util.UUID
import com.fullfacing.keycloak4s.core.logging.Logging._
import com.fullfacing.keycloak4s.core.logging._
import com.fullfacing.keycloak4s.core.models.RequestInfo
import org.slf4j.{Logger, LoggerFactory}
import zio.{Cause, ZIO}

object Logging {

  def tokenRequest(realm: => String, cId: => UUID): Unit =
    ZIO.logTrace(s"${cIdLog(cId)}Requesting Access Token from Keycloak for Realm $gr$realm$cy.$rs")

  def tokenReceived(realm: => String, cId: => UUID): Unit =
    ZIO.logTrace(s"${cIdLog(cId)}Access Token retrieved for Realm $gr$realm$cy.$rs")

  def tokenRequestFailed(realm: String, cId: UUID, ex: Throwable): Unit =
    ZIO.logErrorCause(s"${cIdErr(cId)}Access Token could not be retrieved for Realm $realm.", Cause.fail(ex))

  /* REFRESH TOKEN LOGGING **/

  def tokenRefresh(realm: => String, cId: => UUID): Unit =
    ZIO.logTrace(s"${cIdLog(cId)}Refreshing Access Token for Realm $gr$realm$cy.$rs")

  def tokenRefreshed(realm: => String, cId: => UUID): Unit =
    ZIO.logTrace(s"${cIdLog(cId)}Access Token refreshed for Realm $gr$realm$cy.$rs")

  def tokenRefreshFailed(realm: String, cId: UUID, ex: Throwable): Unit =
    ZIO.logWarning(s"${cIdErr(cId)}Access Token could not be refreshed for Realm $realm.", ex)

  /* ADMIN API REST LOGGING **/

  def requestSent(requestInfo: => RequestInfo, cId: => UUID): Unit = {
    lazy val bodyTrace = requestInfo.body.fold("")(b => s"Body: \n$gr$b")

    ZIO.logDebug(s"${cIdLog(cId)}$gr${requestInfo.protocol}$cy request sent to Keycloak Admin API.$rs")
    ZIO.logTrace(s"${cIdLog(cId)}$gr${requestInfo.protocol} ${requestInfo.path}$cy request sent to Keycloak Admin API. $bodyTrace$rs")
  }

  def retryUnauthorized(requestInfo: => RequestInfo, cId: => UUID): Unit = {
    ZIO.warn(s"${cIdLog(cId)}$gr${requestInfo.protocol}$cy request failed with an authorization error. Retrying...$rs")
  }

  def requestSuccessful(response: => String, cId: => UUID): Unit = {
    lazy val resp = if (response == "") "NoContent" else response
    ZIO.logDebugIff(s"${cIdLog(cId)}Request was successful.$rs")
    ZIO.logTrace(s"${cIdLog(cId)}Request was successful. Response received: $resp$rs")
  }

  def requestFailed(cId: UUID, ex: Throwable): Unit =
    ZIO.logErrorCause(s"${cIdErr(cId)}Request to Keycloak Admin API failed.", Cause.fail(ex))

  /* Logging Helpers **/
  def handleLogging[A, B <: Throwable](resp: Either[B, A])(success: A => Unit, failure: B => Unit): Either[B, A] = resp match {
    case Left(e)  => failure(e); resp
    case Right(r) => success(r); resp
  }

  def logLeft[A, B <: Throwable](either: Either[B, A])(partialLog: B => Unit): Either[B, A] = {
    either.left.map { ex =>
      partialLog(ex)
      ex
    }
  }
}