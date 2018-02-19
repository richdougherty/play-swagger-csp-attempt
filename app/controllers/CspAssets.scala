package controllers

import javax.inject.Inject

import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent, DefaultActionBuilder, Result}
import play.filters.headers.SecurityHeadersFilter

import scala.concurrent.{ExecutionContext, Future}

class CspAssets @Inject()(
    action: DefaultActionBuilder,
    errorHandler: HttpErrorHandler,
    meta: AssetsMetadata)(implicit ec: ExecutionContext) extends AssetsBuilder(errorHandler, meta) {

  val inlineScriptHash = "sha256-VQJGzLUGVTxuS6MyxMPDzebQrR0g3EP5mtKIGdnnGxY="

  override def at(path: String, file: String, aggressiveCaching: Boolean = false): Action[AnyContent] = action.async {
    implicit request =>
      val superAction: Action[AnyContent] = super.at(path, file, aggressiveCaching)
      val superResult: Future[Result] = superAction.apply(request)
      superResult.map { result =>
        result.withHeaders(SecurityHeadersFilter.CONTENT_SECURITY_POLICY_HEADER -> s"default-src 'self'; script-src 'self' '$inlineScriptHash'")
      }
  }

}