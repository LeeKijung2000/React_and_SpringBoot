package kh.springboot.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attm")
@RequiredArgsConstructor
public class AttachmentController {

	private final BoardService bService;
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("i", "2");
		
		int listCount = bService.getListCount(map);
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9);
		
		// 첨부파일 게시판 = 게시글 내용 + 첨부파일
		ArrayList<Board> bList = bService.selectBoardList(pi, map);
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null);
		
		if(bList != null) {
			model.addAttribute("bList", bList).addAttribute("aList", aList).addAttribute("pi", pi).addAttribute("loc", request.getRequestURI());
			return "views/attm/list";
		} else {
			throw new BoardException("첨부파일 게시글 조회를 실패하였습니다.");
		}
		
	}
	
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	@PostMapping("insert")
	public String insertAttmBoard(@ModelAttribute Board b, @RequestParam("file") ArrayList<MultipartFile> files, HttpSession session) {
		// MultipartFile로 받을 때 파일을 넣지 않아도 파일을 넣을 수 있는 요소만 있으면 객체가 생성됨
		String id = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardWriter(id);
		
		// MultipartFile로 받아온거를 Attachment로 바꿔주는 작업
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i < files.size(); i++) {
			MultipartFile upload = files.get(i);
			//System.out.println(upload.getOriginalFilename()); // 업로드 파일의 원본 이름 리턴 : 파일을 넣은 경우 파일 이름 리턴, 파일을 넣지 않은 경우 비워져있는 "" 리턴
			//if(upload != null && !upload.isEmpty()) { // upload.isEmpty() : 파일의 size(크기)를 보고 비어있는지 확인
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload); // 파일 리네임 및 파일 저장소에 파일 저장
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					
					list.add(a);
				}
			}
		}
		// attmLevel 정하는 과정 0: 썸네일
		for(int i = 0; i < list.size(); i++) {
			Attachment a = list.get(i);
			if(i == 0) {
				a.setAttmLevel(0);
			} else {
				a.setAttmLevel(1);
			}
		}
		
		int result1 = 0;
		int result2 = 0;
		if(list.isEmpty()) {
			b.setBoardType(1);
			result1 = bService.writeBoard(b);
		} else {
			b.setBoardType(2);
			result1 = bService.writeBoard(b);
			
			// System.out.println(b);
			// selectkey 도입 전 b
			// Board(boardId=0, boardTitle=잘가라, boardWriter=user03, nickName=null, boardContent=잘가~, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=2)
			
			// selectkey 도입 후 b
			// Board(boardId=23, boardTitle=잘가라, boardWriter=user03, nickName=null, boardContent=잘가~, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=2)
			
			for(Attachment a : list) {
				a.setRefBoardId(b.getBoardId());
			}
			
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1) { // 성공했을때
			if(result2 == 0) {
				return "redirect:/board/list";
			} else {
				return "redirect:/attm/list";
			}
		} else {
			for(Attachment a : list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 작성을 실패하였습니다.");
		}
		
	}
	
	public String[] saveFile(MultipartFile upload) {
		// 파일 저장소 지정
		// SpringBoot에서 파일 저장소는 외부에 만드는 것을 권장
		// devtools가 서버 내에 변동사항이 있으면 서버가 꺼졌다 켜지기 때문에도 있음
		String savePath = "c:\\uploadFiles";
		
		File folder = new File(savePath);
		// 만약 폴더가 존재하지 않으면
		if(!folder.exists()) {
			// 폴더를 만들어줌
			folder.mkdirs();
		}
		
		// file이름을 rename 해주는 과정
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100000);
		String originFileName = upload.getOriginalFilename();
		String renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
		
		// file 저장
		String renamePath = folder + "\\" + renameFileName;
		try {
			upload.transferTo(new File(renamePath));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 호출한 메소드에서 사용하기 위해 String[]에 담아 리턴
		String[] returnArr = new String[2];
		returnArr[0] = savePath;
		returnArr[1] = renameFileName;
		
		return returnArr;
		
	}
	
	public void deleteFile(String renameName) {
		String savePath = "c:\\uploadFiles";
		
		File f = new File(savePath + "\\" + renameName);
		if(f.exists()) {
			f.delete();
		}
	}
	
	// url mapping annotation
	@GetMapping("/{id}/{page}")
	// method 상단부
	public String selectAttm(@PathVariable("id") int bId, @PathVariable("page") int page, HttpSession session, Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		Board b = bService.selectBoard(bId, loginUser);
		// 첨부파일 게시글 상세보기를 하기 위한 첨부파일 select query
		// : select * from attachment where attm_status = 'Y' and ref_board_id = #{bId}
		// 동적 쿼리 사용을 위해 selectAttmBoardList() => selectAttmBoardList(bId)로 변경
		// 썸네일을 위한 첨부파일을 가져올 때는 데이터가 필요 없기 때문에 null로 보냄
		// 근데? int에는 null을 담을 수 없으니 Service에서 int가 아닌 Integer로 받게끔 바꿔줌
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		
		if(b != null) {
			model.addAttribute("b", b).addAttribute("page", page).addAttribute("list", list);
			return "views/attm/detail";
		} else {
			throw new BoardException("첨부파일 게시글 상세보기를 실패하였습니다.");
		}
	}
	
	// 첨부파일 게시글 수정 화면
	@PostMapping("updForm")
	public String updateAttm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		model.addAttribute("b", b).addAttribute("list", list).addAttribute("page", page);
		
		return "views/attm/edit";
	}
	
	// 첨부파일 게시글 수정 후 데이터 넘기기
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("file") ArrayList<MultipartFile> files, @RequestParam("deleteAttm") String[] deleteAttm, @RequestParam("page") int page) {
//		System.out.println(b);
//		System.out.println(deleteAttm.length + "개, " + Arrays.toString(deleteAttm));
//		for(MultipartFile mf : files) {
//			System.out.println("fileName : " + mf.getOriginalFilename());
//		}
		/* 기존 첨부파일 삭제 X, 새로운 첨부파일 첨부 X
		// Board(boardId=24, boardTitle=식사 하셨어요?, boardWriter=null, nickName=null, boardContent=에?, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=0)
		// 기존 첨부파일 : 2개, [, ] 
		// fileName : 
		
		// 기존 첨부파일 1개 삭제, 새로운 첨부파일 X
		// Board(boardId=24, boardTitle=식사 하셨어요?, boardWriter=null, nickName=null, boardContent=에?, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=0)
		// 기존 첨부파일 : 2개, [, 2025071516154548499991.xlsx/1]
		// fileName : 
		
		// 기존 첨부파일 모두 삭제, 새로운 첨부파일 X
		// Board(boardId=24, boardTitle=식사 하셨어요?, boardWriter=null, nickName=null, boardContent=에?, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=0)
		// 기존 첨부파일 : 2개, [20250715161545483725.png/0, 2025071516154548499991.xlsx/1]
		// fileName :
		
		// 기존 첨부파일 삭제 X, 새로운 첨부파일 O
		// Board(boardId=24, boardTitle=식사 하셨어요?, boardWriter=null, nickName=null, boardContent=에?, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=0)
		// 기존 첨부파일 : 2개, [, ]
		// fileName : 세미프로젝트_DB(07.02 수정).sql
		
		// 기존 첨부파일 모두 삭제, 새로운 첨부파일 2개
		// Board(boardId=24, boardTitle=식사 하셨어요?, boardWriter=null, nickName=null, boardContent=에?, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=0)
		// 기존 첨부파일 : 2개, [20250715161545483725.png/0, 2025071516154548499991.xlsx/1]
		// fileName : 세미프로젝트_DB(07.02 수정).sql
		// fileName : 게시글 관련 테이블 정의서.xlsx
		
		// 기존 첨부파일 3개, 새로운 첨부파일 X
		// Board(boardId=22, boardTitle=안녕하세요, boardWriter=null, nickName=null, boardContent=반갑습니다~, boardCount=0, boardCreateDate=null, boardModifyDate=null, status=null, boardType=0)
		// 3개, [, 2025071515233483915997.JPG/1, ]
		// fileName : 
		
		// 중요! 기존 첨부파일이 1개 이면서, 첨부파일을 삭제 안한다 했을 때 deleteAttm.length가 0인 이유
		// 중요! <input>태그가 1개만 있기 때문에 아예 넘어오지를 않음, 여러개 일 때는 <input>태그가 여러개 만들어져 있기 때문에 value가 없어도 다 넘어옴 */
		
		
		/*
		  	1. 새 파일 O
		  		(1) 기존 파일 모두 삭제 : 기존 파일 모두 삭제 && 새 파일 저장
		  								-> 새 파일 중에서 level 0, 1 지정
		  		(2) 기존 파일 일부 삭제 : 기존 파일 일부 삭제 && 새 파일 저장
		  								-> 삭제할 파일의 level 검사 후, level 0 파일이 삭제되면 기존 파일 중 다른 파일의 레벨 0으로 지정
		  								   새 파일의 레벨은 모두 1로 지정
		  		(3) 기존 파일 모두 유지 : 새 파일 저장
		  								-> 새 파일의 레벨은 모두 1로 지정
		  	2. 새 파일 X
		  		(1) 기존 파일 모두 삭제 : 일반 게시판으로 이동 -> board_type = 1로 지정 (mapper에 updateBoard 수정)
		  		(2) 기존 파일 일부 삭제 : 삭제할 파일의 level 검사 후, level 0 파일이 삭제되면 기존 파일 중 다른 파일의 레벨 0으로 지정
		  		(3) 기존 파일 모두 유지 : board만 수정
		 */
		b.setBoardType(2);
		
		// 새로 넣는 파일이 있다면 ArayList<Attachment> list에 옮겨담기
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i < files.size(); i++) {
			MultipartFile upload = files.get(i);
			//System.out.println(upload.getOriginalFilename()); // 업로드 파일의 원본 이름 리턴 : 파일을 넣은 경우 파일 이름 리턴, 파일을 넣지 않은 경우 비워져있는 "" 리턴
			//if(upload != null && !upload.isEmpty()) { // upload.isEmpty() : 파일의 size(크기)를 보고 비어있는지 확인
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload); // 파일 리네임 및 파일 저장소에 파일 저장
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					a.setRefBoardId(b.getBoardId());
					
					list.add(a);
				}
			}
		}
		
		// 삭제한다는 파일이 있다면 삭제할 파일의 이름과 레벨을 각각 delRename과 delLevel에 옮겨 담기
		ArrayList<String> delRename = new ArrayList<String>();
		ArrayList<Integer> delLevel = new ArrayList<Integer>();
		for(String rename : deleteAttm) {
			if(!rename.equals("")) {
				String[] split = rename.split("/");
				delRename.add(split[0]);
				delLevel.add(Integer.parseInt(split[1]));
			}
		}
		
		// 삭제할 파일이 있을 때
		int deleteAttmResult = 0;
		boolean existBeforeAttm = true; // 기존 파일이 있는지
		if(!delRename.isEmpty()) {
			// 파일 삭제
			deleteAttmResult = bService.deleteAttm(delRename);
			if(deleteAttmResult > 0) {
				for(String rename : delRename) {
					deleteFile(rename);
				}
			}
			
			if(deleteAttm.length == deleteAttmResult) { // 기존 파일 전부 삭제
				existBeforeAttm = false; // 기존 파일을 전부 삭제 했으니 false로
				if(list.isEmpty()) { // 새 파일이 없을 때
					b.setBoardType(1);
				}
			} else { // 기존 파일 일부 삭제
				for(int level : delLevel) {
					if(level == 0) { // 기존 파일 중 level이 0이 있을 때
						bService.updateAttmLevel(b.getBoardId());
						break;
					}
				}
			}
		}
		
		// 새 파일이 있을 때 새 파일들에 대한 level 설정
		for(int i = 0; i < list.size(); i++) {
			Attachment a = list.get(i);
			
			if(existBeforeAttm) { // 기존 파일이 존재
				a.setAttmLevel(1); // 새로운 파일의 level = 1
			} else { // 기존 파일이 존재 X
				if(i == 0) { // 새로운 파일 중 가장 첫 번째
					a.setAttmLevel(0); // 파일의 level = 0
				} else { // 그 외 나머지
					a.setAttmLevel(1); // 파일의 level = 1
				}
			}
		}
		
		int updateBoardResult = bService.updateBoard(b);
		
		int updateAttmResult = 0;
		if(!list.isEmpty()) {
			updateAttmResult = bService.insertAttm(list);
		}
		
		if(updateBoardResult + updateAttmResult == list.size() + 1) {
			if(deleteAttm.length != 0 && delRename.size() == deleteAttm.length && updateAttmResult == 0) {
				return "redirect:/board/list";
			} else {
				return String.format("redirect:/attm/%d/%d", b.getBoardId(), page);
			}
		} else {
			throw new BoardException("첨부파일 게시글 수정을 실패하였습니다.");
		}
	}
	
//	@PostMapping("delete")
//	public String deleteAttm(@RequestParam("boardId") int bId) {
//		int result1 = bService.deleteBoard(bId);
//		int result2 = bService.statusNAttm(bId);
//		if(result1 > 0 && result2 > 0) {
//			return "redirect:/attm/list";
//		} else {
//			throw new BoardException("첨부파일 게시글 삭제를 실패하였습니다.");
//		}
//	}
	
}
