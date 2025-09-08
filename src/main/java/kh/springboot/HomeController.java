package kh.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // bean(객체) @Controller : 컨트롤러의 역할을 하는 bean을 생성하는 어노테이션
public class HomeController {

	// get방식으로 넘어온 url 요청을 메소드와 맵핑 시켜주는 HandlerMapping @GetMapping("처리할 url")
	@GetMapping("home")
	public String home() {
		return "views/home"; 
		// Controller가 return하는 home이라고 하는 애를 View Resolver가 반환되는 경로 앞에 prefix(classpath:templates/)를 기본적으로 가지고 있고, 반환되는 경로 뒤에 suffix(.html)을 기본적으로 가지고 있음
		// classpath:templates/views/home.html
	}	
		
	@GetMapping("/data") 
	@ResponseBody
	public int test() {
		return 410;
	}
	
}
