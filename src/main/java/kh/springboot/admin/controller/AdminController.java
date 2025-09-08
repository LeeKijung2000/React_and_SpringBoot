package kh.springboot.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminController {

	@GetMapping("home")
	public String moveToMainAdmin() {
		return "redirect:http://localhost:5173";
	}
	
}
