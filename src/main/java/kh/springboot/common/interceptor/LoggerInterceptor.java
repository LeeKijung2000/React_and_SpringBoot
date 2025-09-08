package kh.springboot.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.vo.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor{

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			if(loginUser.getIsAdmin().equals("Y")) {
				log.debug("LoggerInterceptor 출력 : " + loginUser.getId() + "[관리자]");
			} else {
				log.debug("LoggerInterceptor 출력 : " + loginUser.getId() + "[일반회원]");
			}
			
		}
		
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
}
