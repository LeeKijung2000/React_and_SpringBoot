package kh.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // 이 클래스가 main클래스임을 알려주는 어노테이션, (exclude = {SecurityAutoConfiguration.class}) Security의 자동 실행을 제외시킴
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		// 이 메인 메소드를 통해서 SpringBoot가 실행됨
	}

}
