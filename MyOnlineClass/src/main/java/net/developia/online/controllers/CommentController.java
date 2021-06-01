package net.developia.online.controllers;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.developia.online.dto.CommentDTO;
import net.developia.online.dto.LectureDTO;
import net.developia.online.services.CommentService;
import net.developia.online.services.MemberService;
import net.developia.online.util.DateFormatClass;

@Slf4j
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private MemberService memberService;

	@GetMapping(value = "/classdetail/{no}/{cno}", produces = "application/json; charset=UTF-8")
	public List<CommentDTO> comment_list(@PathVariable("no") long no, @PathVariable("cno") long co,
			@ModelAttribute("comments") CommentDTO comments) throws Exception {
		List<CommentDTO> commentlist = commentService.getCommentList(no);
		System.out.println("★★★" + commentlist.toString());
		return commentlist;
	}

	@PostMapping(value = "/classdetail/{no}/insert", produces = "application/json; charset=UTF-8")
	public @ResponseBody String comment_insert(@PathVariable("no") long no, @RequestBody String strjson,
			HttpServletRequest request, HttpSession session) throws Exception {
		ResponseEntity<String> entity = null;

		JSONObject jObject = new JSONObject(strjson);
		String content = jObject.getString("content_textVal");

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ID", session.getAttribute("id"));
		List<LectureDTO> data = memberService.checkMemberLecture(map);
		if (data.size() == 0) {
			return "False";
		}
		CommentDTO comments = new CommentDTO();
		comments.setContent(content);
		comments.setLecture_id(no);
		comments.setMember_id((String) session.getAttribute("id"));
		comments.setName((String) session.getAttribute("name"));
		comments.setRegdate(DateFormatClass.strDateNow());
		System.out.println(comments.toString());
		try {
			for (LectureDTO dto : data) {
				long lecture_id = dto.getId();
				if (lecture_id == no) { // 수강생만 댓글 가능
					commentService.insertComment(comments);
					return "Success";
				}
			}
			return "False";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@PostMapping(value = "/classdetail/{no}/update", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String comment_update(@ModelAttribute CommentDTO commentDTO, @PathVariable("no") long no,
			@RequestBody String strjson, HttpServletRequest request, HttpSession session) throws Exception {

		JSONObject jObject = new JSONObject(strjson);
		String cno = jObject.getString("cno");
		String comment_user = jObject.getString("comment_user");
		String login_user = (String) session.getAttribute("name");
		String content_fix = jObject.getString("content_fix");

		commentDTO.setNo(Long.parseLong(cno));
		commentDTO.setMember_id((String) session.getAttribute("id"));
		commentDTO.setLecture_id(no);
		commentDTO.setContent(content_fix);

		System.out.println(commentDTO.toString());

		try {
			if (login_user.equals(comment_user) == true) {
				commentService.updateComment(commentDTO); // 작성자인 경우
				return "Success";
			} else
				return "False"; // 작성자가 아닌 경우
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping(value = "/classdetail/{no}/delete", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public String comment_delete(@ModelAttribute CommentDTO commentDTO, @PathVariable("no") long no,
			@RequestBody String strjson, HttpServletRequest request, HttpSession session) throws Exception {

		JSONObject jObject = new JSONObject(strjson);
		String content_no = jObject.getString("cno");
		String content_name = jObject.getString("user_check");
		String me = (String) session.getAttribute("name");

		commentDTO.setNo(Long.parseLong(content_no));
		commentDTO.setMember_id((String) session.getAttribute("id"));

		try {
			if (me.equals(content_name) == true) {
				commentService.deleteComment(commentDTO);
				return "Success";
			} else
				return "False";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}