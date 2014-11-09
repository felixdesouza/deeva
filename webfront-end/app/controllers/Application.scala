package controllers

import deeva.Debug
import play.api._
import play.api.mvc._
import play.api.Logger

object Application extends Controller {

  //def debugger = new Debug()
  Logger.debug(System.getProperty("asdf"))

  def index = Action {

    Ok(views.html.index())
  }

}