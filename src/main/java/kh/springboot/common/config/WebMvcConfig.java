package kh.springboot.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kh.springboot.common.interceptor.CheckAdminInterceptor;
import kh.springboot.common.interceptor.CheckLoginInterceptor;
import kh.springboot.common.interceptor.LoggerInterceptor;
import kh.springboot.common.interceptor.TestInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")					// 맵핑 URL 설정 ex. addResourceHandler("/image/**") /image/어쩌고
				.addResourceLocations("file:///c:/uploadFiles/", "file:///c:/profiles/", "classpath:/static");	// 정적 리소스 위치
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
//		registry.addInterceptor(new TestInterceptor()) // 인터셉터 등록
//				.addPathPatterns("/**");			   // 인터셉터가 가로챌 url 등록
		
		registry.addInterceptor(new CheckLoginInterceptor())
				.addPathPatterns("/member/myInfo", "/member/edit", "/member/updatePassword", "/member/delete")
				.addPathPatterns("/board/**", "/attm/**")
				.excludePathPatterns("/board/list", "/attm/list", "/board/search", "/board/top");
		
		registry.addInterceptor(new CheckAdminInterceptor())
				.addPathPatterns("/admin/**");
		
		registry.addInterceptor(new LoggerInterceptor())
				.addPathPatterns("/member/signIn");
				
	}
	
}
