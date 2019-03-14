package com.fullfacing.keycloak4s.models

case class RequiredActionProvider(alias: Option[String],
                                  config: Option[Map[String, AnyRef]],
                                  defaultAction: Option[Boolean],
                                  enabled: Option[Boolean],
                                  name: Option[String],
                                  priority: Option[Int],
                                  providerId: Option[String])
