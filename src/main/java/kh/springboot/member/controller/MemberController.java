package kh.springboot.member.controller;


import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Controller // Controller 역할을 하는 bean 생성
@RequiredArgsConstructor // 생성자 주입, final이 붙은 상수나 @NotNull이 붙은 변수만 가지고 생성자 생성
@SessionAttributes("loginUser")
@RequestMapping("/member/") // MemberController로 오면 URL을 /member/로 시작 (공용 URL)
@Slf4j
//@RequestMapping("/member/") + @XXXMapping("signIn"), @RequestMapping("/member/" + @XXXMapping("signIn"), @RequestMapping("/member") + @XXXMapping("signIn")
// 셋다 상관없음
public class MemberController {
	
	// 의존성 주입 중 필드 주입 방법 : import org.springframework.beans.factory.annotation.Autowired;
	//@Autowired 
	//private MemberService mService; // MemberService 객체를 mService 변수에 주입
	
	// 생성자 주입 : import lombok.RequiredArgsConstructor
	// 권장하는 이유 : final이 붙기 때문 -> 주입이 되자마자 객체에 대한 불변성이 보장됨
	private final MemberService mService;
	// public MemberController(MemberService mService){
	//		this.mService = mService;
	// }
	
	private final BCryptPasswordEncoder bcrypt;
	
	
	
//	private Logger log = org.slf4j.LoggerFactory.getLogger(MemberController.class);
	
	// 같은 url을 처리하는 메소드가 2개 이상이면 원래 error가 나야함
	// 여기서는 보내는 방식이 달라서 상관없음
	@GetMapping("/signIn")
	public String singIn() {
		return "login";
	}
	
	// 파라미터를 전송받는 방법
	// 1. HttpServletRequest 이용 (Servlet 방식)
//	@PostMapping("member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println("id1 : " + id);
//		System.out.println("pwd1 : " + pwd);
//	}
	
	// 2. @RequestParam 이용
	// value : view에서 받아오는 파라미터 이름이 들어가는 곳(@RequestParam에 들어갈 속성이 value 하나 뿐이라면 생략 가능)
	// defaultValue : 값이 없거나 null일 때 기본적으로 들어갈 데이터를 지정하는 속성
	//				  값이 들어가 있을 때는 defaultValue에 설정된 데이터가 들어가지 않음
	// required : 지정 파라미터가 필수 파라미터인지 설정하는 속성
	// required=false : 해당 파라미터가 없으면 없는대로 있으면 있는대로 ㅇㅇ.. -> 없을때는 null값이 넘어옴
//	@PostMapping("member/signIn")
//	public void login(@RequestParam(value="id", defaultValue="hello") String id, @RequestParam(value="pwd") String pwd, @RequestParam(value="test", required=false) String test) {
//		System.out.println("id2 : " + id);
//		System.out.println("pwd2 : " + pwd);
//		System.out.println("test : " + test);
//	}
//	
	// 3. @RequestParam 생략 : 파라미터 명과 변수 명을 일치시켜 자동 맵핑되게 함 -> 권장사항이 아님
	// public void login(String id, String pwd){}
	
	// 4. @ModelAttribute 이용
	// ModelAttribute에 적혀있는 클래스에 기본 생성자랑 setter를 이용해서 데이터를 주입 -> 둘 중 하나라도 없으면 에러가 남
	// 보내는 파라미터 명과 setter에 있는 이름이 일치해야 함
//	@PostMapping("member/signIn")
//	public void login(@ModelAttribute Member m) {
//		System.out.println("id4 : " + m.getId());
//		System.out.println("pwd4 : " + m.getPwd());
//	}
	
	// 5. @ModelAttribute 생략
//	@PostMapping("/member/signIn")
//	public String login(Member m, HttpSession session) {
//		// 결합도가 높은 상황 : 메소드가 있어야 코드가 실행이 되는 상황
//		Member loginUser = mService.login(m);
//		if(loginUser != null) {
//			session.setAttribute("loginUser", loginUser);
//			//return "views/home"; // == forward -> URL이 유지되기 때문
//			return "redirect:/home"; // == sendRedirect
//		} else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}
//	}
//	
//	@GetMapping("/member/logout")
//	public String logout(HttpSession session) {
//		session.invalidate(); // 세션 무효화
//		// session.setAttribute("loginUser", null);
//		return "redirect:/home";
//	}
	
	@GetMapping("enroll")
	public String enroll() {
//		log.warn("회원가입페이지");
		//log4j의 로그레벨 피라미터: debug < info < warn < error < fatal
		//설정한 level 속성 이상만이 화면에 보임
		//fatal 아주 심각한 에러
		//error 어떤  요청 처리 중 문제 발생
		//warn 프로그램 실행에는 문제 없지만, 향후 시스템 에러의 원인이 될 수 있는 경고성 메세지
		//info 상태 변경과 같은 정보성 메세지
		//debug 개발 시 디버그 용도로 사용하는 메세지
		//trace 디버그 레벨이 너무 광범위한 것을 해결하기 위해 좀 더 상세한 이벤트(ex: 경로추적)를 나타냄
		return "enroll";
	}
	
	@PostMapping("enroll")
	public String insertMember(@ModelAttribute Member m, @RequestParam(value="emailId") String emailId, @RequestParam(value="emailDomain") String emailDomain) {
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		
		String encPwd = bcrypt.encode(m.getPwd()); // 비밀번호 암호화
		m.setPwd(encPwd);
		System.out.println(m);
		
		int result = mService.insertMember(m);
		// 회원가입 성공 시 home으로 가기
		if(result > 0) {
			return "redirect:/home";
		} else {
			// 회원가입 실패 시 500에러 발생 : 회원가입을 실패하였습니다.
			throw new MemberException("회원가입을 실패하였습니다.");
		}

	}
	
	// 암호화 후 로그인
//	@PostMapping("/member/signIn")
//	public String login(Member m, HttpSession session) {
//		Member loginUser = mService.login(m);
//		
//		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			session.setAttribute("loginUser", loginUser);
//			//return "views/home"; // == forward -> URL이 유지되기 때문
//			return "redirect:/home"; // == sendRedirect
//		} else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}
//
//	}
	
	// 데이터를 view로 전달하는 방법
	// 두 가지 방법 중 아무거나 쓰면 됨
	// 1. Model 객체 이용 : request영역에 담기는 Map형식(key:value)의 객체
//	@GetMapping("/member/myInfo")
//	public String myInfo(HttpSession session, Model model) {
//		Member loginUser = (Member)session.getAttribute("loginUser"); 
//		if(loginUser != null) {
//			String id = loginUser.getId();
//			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
//			model.addAttribute("list", list); // request영역에 데이터 담기
//		}
//		return "views/member/myInfo";
//	}
	
	// 2. ModelAndView 객체 이용 : Model + View
	//		Model에 데이터 저장하고 View에 forward할 뷰 정보를 담음
	@GetMapping("myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser"); 
		if(loginUser != null) {
			String id = loginUser.getId();
			ArrayList<HashMap<String, Object>> list = mService.selectMyList(id);
			
			// todolist
			ArrayList<TodoList> todolist = mService.getTodolist(id);
			
			mv.addObject("todolist", todolist);
			mv.addObject("list", list);
			mv.setViewName("myInfo");
		}
		return mv;
	}
	
	// 3. Session에 저장할 때 @SessionAttributes 이용
	//		Model에 attribute가 추가될 때 자동으로 key값을 찾아 세션에 등록
	// 암호화 후 로그인 + @SessionAttributes
	@PostMapping("signIn")
	public String login(Member m, Model model, @RequestParam("beforeURL") String beforeURL) {
		Member loginUser = mService.login(m);
		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) { // 암호화랑 평문화 비교, bcrypt.matches(비교대상(평문), 비교조건(암호))
			model.addAttribute("loginUser", loginUser);
			if(loginUser.getIsAdmin().equals("N")) {
//				log.debug("일반회원 로그인 : " + m.getId());
				return "redirect:" + beforeURL; 
			} else {
//				log.debug("관리자 로그인 : " + m.getId());
				return "redirect:/admin/home"; 
			}
		} else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	}
	
	// @SessionAttributes 추가 후 로그아웃
	@GetMapping("/logout")
	public String logout(SessionStatus status) {
		status.setComplete(); // SessionStatus의 세션 무효화 같은 뭐 ㅇㅇ...
		// session.setAttribute("loginUser", null);
		return "redirect:/home";
	}
	
	@GetMapping("/edit")
	public String edit() {
		return "edit";
	}
	
	@PostMapping("/edit")
	public String updateMember(Member m, @RequestParam(value="emailId") String emailId, @RequestParam(value="emailDomain") String emailDomain, Model model) {
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		int result = mService.updateMember(m);
		
		if(result > 0) {
			model.addAttribute("loginUser", mService.login(m));
			return "redirect:/member/myInfo";
		} else {
			throw new MemberException("회원정보 수정을 실패하였습니다.");
		}
	}
	
	@PostMapping("updatePassword")
	public String updatePassword(@RequestParam(value="currentPwd") String pwd, @RequestParam(value="newPwd") String newPwd, Model model, SessionStatus status) {
		// Member m = (Member)session.getAttribute("loginUser"); // HttpSession 이용 (매개변수에 HttpSession session 추가)
		Member m = (Member)model.getAttribute("loginUser");
		
		// 현재 비밀번호가 같은지 확인
		if(bcrypt.matches(pwd, m.getPwd())) {
			// 평문화 되어있는 새로운 비밀번호를 암호화하여 DB에 넘김
			String encPwd = bcrypt.encode(newPwd);
			m.setPwd(encPwd);
			int result = mService.updatePassword(m);
			if(result > 0) {
				// model.addAttribute("loginUser", m); 해서 세션에 비밀번호 변경된 것을 적용시켜줘도 됨
				status.setComplete();
				return "redirect:/home";
			} else {
				// 에러 메세지는 비밀번호 수정 실패
				throw new MemberException("비밀번호 수정을 실패하였습니다.");
			}
		} else {
			throw new MemberException("비밀번호 수정을 실패하였습니다.");
		}
	}
	
	@GetMapping("delete")
	public String deleteMember(Model model) {
		Member m = (Member)model.getAttribute("loginUser");
		int result = mService.deleteMember(m);
		if(result > 0) {
			return "redirect:/member/logout";
		} else {
			throw new MemberException("회원탈퇴를 실패하였습니다.");
		}
	}
	
//	@GetMapping("checkId")
//	public void checkId(@RequestParam("id") String id, PrintWriter out) {
//		int result = mService.checkId(id);
//		out.println(result);
//		
//	}
//	
//	@GetMapping("checkNickName")
//	@ResponseBody // return에 view를 찾을게 아니라 return 자체를 응답하는 데이터로 보냄
//	public String checkNickName(@RequestParam("nickName") String nickName) {
//		int result = mService.checkNickName(nickName);
//		return result == 0 ? "usable" : "unusable";
//	}
	
	
	@GetMapping("findIDPW")
	public String findIDPW() {
		return "findIDPW";
	}
	
//	@PostMapping("fid")
//	public String findId(@ModelAttribute Member m, Model model) {
//		String id = mService.findId(m);
//		if(id != null) {
//			model.addAttribute("id", id);
//			return "findId";
//		} else {
//			throw new MemberException("존재하지 않는 회원입니다.");
//		}
//	}
//	
//	@PostMapping("fpw")
//	public String findPw(@ModelAttribute Member m, Model model) {
//		Member member = mService.findPw(m);
//		if(member != null) {
//			model.addAttribute("id", m.getId());
//			return "resetPw";
//		} else {
//			throw new MemberException("존재하지 않는 회원입니다.");
//		}
//	}
	
	@PostMapping("fInfo")
	public String findInfo(@ModelAttribute Member m, Model model) {
		Member member = mService.findInfo(m);
		if(member != null) {
			model.addAttribute("id", member.getId());
			return m.getName() == null ? "resetPw" : "findId";
		} else {
			throw new MemberException("존재하지 않는 회원입니다.");
		}
	}
	
	// RedirectAttribute => 스프링부트에서 제공, Redirect시 필요한 데이터가 있으면 보내줌
	@PostMapping("fpwUpdate")
	public String updatePwd(@ModelAttribute Member m, Model model) {
		m.setPwd(bcrypt.encode(m.getPwd()));
		int result = mService.updatePassword(m);
		if(result > 0) {
			model.addAttribute("msg", "비밀번호 수정이 완료되었습니다.");
			model.addAttribute("url", "/home");
			return "views/common/sendRedirect";
		} else {
			throw new MemberException("비밀번호 수정을 실패하였습니다.");
		}
	}
	
	
}



