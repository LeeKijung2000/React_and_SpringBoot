package kh.springboot.member.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;

@Mapper // 인터페이스 구현을 xml로 하겠다는 의미
// 일반 클래스에 Mapper를 넣으면 작동하지 않음
// interface에 Mapper 어노테이션을 넣어야 작동
public interface MemberMapper {

	Member login(Member m);

	int insertMember(Member m);

	ArrayList<HashMap<String, Object>> selectMyList(String id);

	int updateMember(Member m);

	int updatePassword(Member m);

	int deleteMember(Member m);

//	int checkId(String id);
//
//	int checkNickName(String nickName);

	int checkValue(HashMap<String, String> map);

//	String findId(Member m);
//
//	Member findPw(Member m);

	Member findInfo(Member m);

	ArrayList<TodoList> getTodolist(String id);

	int insertTodo(TodoList todo);

	int updateTodo(TodoList todo);

	int ldeleteTodo(int num);

	int updateProfile(Member m);
	
}
