package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateResolverConfig {
	
	// EL처럼 작동
	
	@Bean
	public ClassLoaderTemplateResolver memberResolver() {
		ClassLoaderTemplateResolver mResolver = new ClassLoaderTemplateResolver();
		mResolver.setPrefix("templates/views/member/"); // URL뒤에 /가 빠져있으면 Controller에서 return에 계속 앞에다가 /를 붙여줘야함, ViewResolver가 하나 더 생성된 것 -> 기본 ViewResolver가 사라지는 것이 아님
		mResolver.setSuffix(".html");
		mResolver.setTemplateMode(TemplateMode.HTML);
		mResolver.setCharacterEncoding("UTF-8");
		mResolver.setCacheable(false); // 서버가 재실행되지 않아도 바로 수정된 데이터를 화면에 보내줌
		mResolver.setCheckExistence(true); // Resolver가 연쇄적으로 작동 할 수 있도록 해주는 메소드, false면 제일 최상단 Resolver메소드만 실행되고 그 밑에는 아예 실행이 안됨, 기본값이 false이기 때문에 연쇄적으로 하려면 true로 바꿔주는 설정이 필요함
		
		return mResolver;
	}
	
	@Bean
	public ClassLoaderTemplateResolver boardResolver() {
		ClassLoaderTemplateResolver bResolver = new ClassLoaderTemplateResolver();
		bResolver.setPrefix("templates/views/board/"); // URL뒤에 /가 빠져있으면 Controller에서 return에 계속 앞에다가 /를 붙여줘야함, ViewResolver가 하나 더 생성된 것 -> 기본 ViewResolver가 사라지는 것이 아님
		bResolver.setSuffix(".html");
		bResolver.setTemplateMode(TemplateMode.HTML);
		bResolver.setCharacterEncoding("UTF-8");
		bResolver.setCacheable(false); // 서버가 재실행되지 않아도 바로 수정된 데이터를 화면에 보내줌
		bResolver.setCheckExistence(true); // Resolver가 연쇄적으로 작동 할 수 있도록 해주는 메소드, false면 제일 최상단 Resolver메소드만 실행되고 그 밑에는 아예 실행이 안됨, 기본값이 false이기 때문에 연쇄적으로 하려면 true로 바꿔주는 설정이 필요함
		
		return bResolver;
	}
	
	@Bean
	public ClassLoaderTemplateResolver adminResolver() {
		ClassLoaderTemplateResolver aResolver = new ClassLoaderTemplateResolver();
		aResolver.setPrefix("templates/views/admin/"); // URL뒤에 /가 빠져있으면 Controller에서 return에 계속 앞에다가 /를 붙여줘야함, ViewResolver가 하나 더 생성된 것 -> 기본 ViewResolver가 사라지는 것이 아님
		aResolver.setSuffix(".html");
		aResolver.setTemplateMode(TemplateMode.HTML);
		aResolver.setCharacterEncoding("UTF-8");
		aResolver.setCacheable(false); // 서버가 재실행되지 않아도 바로 수정된 데이터를 화면에 보내줌
		aResolver.setCheckExistence(true); // Resolver가 연쇄적으로 작동 할 수 있도록 해주는 메소드, false면 제일 최상단 Resolver메소드만 실행되고 그 밑에는 아예 실행이 안됨, 기본값이 false이기 때문에 연쇄적으로 하려면 true로 바꿔주는 설정이 필요함
		
		return aResolver;
	}
}
