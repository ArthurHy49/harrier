package cn.spdb.harrier.api.security.handle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import cn.spdb.harrier.api.enums.Status;
import cn.spdb.harrier.api.utils.Result;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {
		int code = Status.USER_LOGIN_FAILURE.getCode();
		String msg = Status.USER_LOGIN_FAILURE.getMsg();
		Result result = new Result(code, msg);
		response.setStatus(200);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(result);
	}

}
