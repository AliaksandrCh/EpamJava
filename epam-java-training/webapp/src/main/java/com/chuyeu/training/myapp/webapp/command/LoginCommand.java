package com.chuyeu.training.myapp.webapp.command;

import javax.servlet.http.HttpServletRequest;

import com.chuyeu.training.myapp.webapp.logic.LoginLogic;
import com.chuyeu.training.myapp.webapp.resource.ConfigurationManager;
import com.chuyeu.training.myapp.webapp.resource.MessageManager;

public class LoginCommand implements ActionCommand {
	
	private static final String PARAM_NAME_LOGIN = "login";
	private static final String PARAM_NAME_PASSWORD = "password";

	@Override
	public String execute(HttpServletRequest request) {

		String page = null;
		String login = request.getParameter(PARAM_NAME_LOGIN);
		String pass = request.getParameter(PARAM_NAME_PASSWORD);

		if (LoginLogic.checkLogin(login, pass)) {
			request.setAttribute("user", login);
			page = ConfigurationManager.getProperty("path.page.main");
		} else {
			request.setAttribute("errorLoginPassMessage", MessageManager.getProperty("message.loginerror"));
			page = ConfigurationManager.getProperty("path.page.login");
		}
		return page;
	}
}
