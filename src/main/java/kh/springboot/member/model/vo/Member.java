package kh.springboot.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor // 기본 생성자 lombok 어노테이션
@AllArgsConstructor // 매개변수 있는 생성자 lombok 어노테이션
@Getter // getter lombok 어노테이션
@Setter // setter lombok 어노테이션
@ToString // toString lombok 어노테이션
public class Member {
	private String id;
	private String pwd;
	private String name;
	private String nickName;
	private String email;
	private String gender;
	private int age;
	private String phone;
	private String address;
	private Date enrollDate;
	private Date updateDate;
	private String memberStatus;
	private String isAdmin;
	private String profile;
}
