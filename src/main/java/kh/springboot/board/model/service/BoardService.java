package kh.springboot.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardMapper mapper;
	
	public int getListCount(HashMap<String, String> map) {
		return mapper.getListCount(map);
	}

	public ArrayList<Board> selectBoardList(PageInfo pi, HashMap<String, String> map) {
		int offset = (pi.getCurrentPage()-1)*pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectBoardList(map, rowBounds);
	}

	public int writeBoard(Board b) {
		return mapper.writeBoard(b);
	}

	public Board selectBoard(int bId, Member loginUser) {
		Board b = mapper.selectBoard(bId);
		if(b != null) {
			if(loginUser != null && !b.getBoardWriter().equals(loginUser.getId())) {
				int result = mapper.updateCount(bId);
				if(result > 0) {
					b.setBoardCount(b.getBoardCount() + 1);
				}
			}
		}
		return b;
	}

	public int updateBoard(Board b) {
		return mapper.updateBoard(b);
	}

	public int deleteBoard(int bId) {
		return mapper.deleteBoard(bId);
	}

	public ArrayList<Attachment> selectAttmBoardList(Integer bId) {
		return mapper.selectAttmBoardList(bId);
	}

	public int insertAttm(ArrayList<Attachment> list) {
		return mapper.insertAttm(list);
	}

	public int deleteAttm(ArrayList<String> delRename) {
		return mapper.deleteAttm(delRename);
	}

	public void updateAttmLevel(int boardId) {
		mapper.updateAttmLevel(boardId);
	}

	public ArrayList<Board> selectTop() {
		return mapper.selectTop();
	}

	public ArrayList<Reply> selectReplyList(int bId) {
		return mapper.selectReplyList(bId);
	}

	public int insertReply(Reply r) {
		return mapper.insertReply(r);
	}

	public int deleteReply(int rId) {
		return mapper.deleteReply(rId);
	}

	public int updateReply(Reply r) {
		return mapper.updateReply(r);
	}

	public int updateBoardStatus(HashMap<String, Object> map) {
		return mapper.updateBoardStatus(map);
	}

//	public int statusNAttm(int bId) {
//		return mapper.statusNAttm(bId);
//	}


}
