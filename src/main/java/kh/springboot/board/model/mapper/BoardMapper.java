package kh.springboot.board.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;

@Mapper
public interface BoardMapper {

	int getListCount(HashMap<String, String> map);

	ArrayList<Board> selectBoardList(HashMap<String, String> map, RowBounds rowBounds);
	// 위 순서 제대로 지켜야함 첫번째 인자 : 보낼 데이터, 두번째 인자 : 페이징처리에 관한 RowBounds

	int writeBoard(Board b);

	Board selectBoard(int bId);

	int updateCount(int bId);

	int updateBoard(Board b);

	int deleteBoard(int bId);

	ArrayList<Attachment> selectAttmBoardList(Integer bId);

	int insertAttm(ArrayList<Attachment> list);

	int deleteAttm(ArrayList<String> delRename);

	void updateAttmLevel(int boardId);

	ArrayList<Board> selectTop();

	ArrayList<Reply> selectReplyList(int bId);

	int insertReply(Reply r);

	int deleteReply(int rId);

	int updateReply(Reply r);

	int updateBoardStatus(HashMap<String, Object> map);

//	int statusNAttm(int bId);



}
