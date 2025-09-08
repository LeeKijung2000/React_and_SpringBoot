package kh.springboot.board.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board") 
@RequiredArgsConstructor
public class BoardController {

	private final BoardService bService;
	
	
	
	// URL만 다르고 같은 내용으로 처리할 때 Mapping안에 ({"123", "456"}) 형식으로 받아줌
	@GetMapping({"/list", "search"})
	public ModelAndView selectList(@RequestParam(value="page", defaultValue="1") int currentPage, ModelAndView mv, HttpServletRequest request, @RequestParam HashMap<String, String> map) {
		// @RequestParam의 반환 타입은 개발자 마음대로
		// page라는 parameter가 없는데 @RequestParam으로 무조건 가져오겠다 하면 400에러가 남
		
		// System.out.println(map);
		// /board/list										-> {}
		// /board/list?page=3								-> {page=3}
		// /board/search?value=?&condition=title			-> {value=?, condition=title}
		// /board/search?value=?&condition=title&page=1		-> {value=?, condition=title, page=1}
		
		map.put("i", "1");
		
		int listCount = bService.getListCount(map);

		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		
		ArrayList<Board> list = bService.selectBoardList(pi, map);
		
		mv.addObject("list", list).addObject("pi", pi).addObject("loc", request.getRequestURI()).setViewName("list");
		mv.addObject("map", map);
		// View에서 현재 URL을 사용하기 위해 request에 있는 getRequestURI를 이용하여 contextPath/현재URL을 가져옴
		
		return mv;
	}
	
	@GetMapping("/write")
	public String write() {
		return "write";
	}
	
	@PostMapping("/insert")
	public String writeBoard(@ModelAttribute Board b, HttpSession session) {
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		//System.out.println(loginUser);
		
		b.setBoardWriter(loginUser.getId());
		b.setBoardType(1);
		
		int result = bService.writeBoard(b);
		if(result > 0) {
			return "redirect:/board/list";
		} else {
			throw new BoardException("게시글 작성을 실패하였습니다.");
		}
	}
	
	/*
	 	url을 통해 전달된 값을 파라미터로 받아오기
		 	http://localhost:8080/board?id=10&page=1 -> 쿼리스트링 이용하여 여러 개 값 전달 : @RequestParam
		 	http://localhost:8080/board/10/1		 -> 폴더이동 혹은 데이터 전달         : @PathVariable
	 */
	
	// 게시글 상세 조회 + 댓글
	@GetMapping("/{id}/{page}")
	public String selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page, HttpSession session, Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		Board b = bService.selectBoard(bId, loginUser);
		ArrayList<Reply> list = bService.selectReplyList(bId);
		if(b != null) {
			model.addAttribute("b", b).addAttribute("page", page);
			model.addAttribute("list", list); // 댓글
			
			return "detail";
		} else {
			throw new BoardException("게시글 상세보기를 실패하였습니다.");
		}
		
	}
	
	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		model.addAttribute("b", b).addAttribute("page", page);
		return "views/board/edit";
	}
	
//	@PostMapping("update")
//	public String updateBoard(@RequestParam("boardTitle") String Title, @RequestParam("boardContent") String content, @RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
//		
//		Board b = new Board();
//		
//		b.setBoardContent(content);
//		b.setBoardId(bId);
//		b.setBoardTitle(Title);
//		model.addAttribute("id", bId).addAttribute("page", page);
//		
//		int result = bService.updateBoard(b);
//		if(result > 0) {
//			return "redirect:/{Id}/{page}";
//		} else {
//			throw new BoardException("게시글 수정을 실패하였습니다.");
//		}
//	}
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page) {
		b.setBoardType(1);
		int result = bService.updateBoard(b);
		if(result > 0) {
			// return "redirect:/board/" + b.getBoardId() + "/" + page;
			return String.format("redirect:/board/%d/%d", b.getBoardId(), page);
		} else {
			throw new BoardException("게시글 수정을 실패하였습니다.");
		}
	}
	
	@PostMapping("delete")
	public String deleteBoard(@RequestParam("boardId") int bId, HttpServletRequest request) {
		int result = bService.deleteBoard(bId);
		if(result > 0) {
			return "redirect:/" + (request.getHeader("referer").contains("board") ? "board" : "attm") + "/list"; // request.getHeader("referer") 이전 URL
		} else {
			throw new BoardException("게시글 삭제를 실패하였습니다.");
		}
	}
	

	
//	@GetMapping(value="rinsert")
//	@ResponseBody
//	public String insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
//		int result = bService.insertReply(r);
//		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
		
		// json 버전
//		JSONArray array = new JSONArray();
//		for(Reply reply : list) {
//			JSONObject json = new JSONObject();
//			json.put("replyContent", reply.getReplyContent());
//			json.put("nickName", reply.getNickName());
//			json.put("replyModifyDate", reply.getReplyModifyDate());
//			array.put(json);
//		}
//		return array.toString();
		
		// gson 버전
//		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//		response.setContentType("application/json; charset=UTF-8");
//		return gson.toJson(list);
//	}
	

	
	// 검색 관련
//	@GetMapping("search")
//	// Get방식 쿼리스트링으로 데이터를 받아올 때 처음부터 @RequestParam HashMap으로 해도 됨
//	public String searchBoard(@RequestParam(value="page", defaultValue="1") int currentPage, @RequestParam HashMap<String, String> map, Model model, HttpServletRequest request) {
//		// System.out.println(map);
//		map.put("i", "1");
//		int listCount = bService.getListCount(map);
//		
//		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
//		ArrayList<Board> list = bService.selectBoardList(pi, map);
//		
//		model.addAttribute("list", list).addAttribute("pi", pi).addAttribute("loc", request.getRequestURI());
//		
//		return "list";
//	}
//	
	
	
}
