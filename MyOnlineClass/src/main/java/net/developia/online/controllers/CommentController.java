package net.developia.online.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.developia.online.dto.CommentDTO;
import net.developia.online.services.CommentService;
import net.developia.online.services.InstructorServiceImpl;
import net.developia.online.util.DateFormatClass;

@Slf4j
@RestController
public class CommentController {

	@Autowired
	private CommentService commentService;

	@GetMapping(value = "/classdetail/{no}/{cno}", produces = "application/json; charset=UTF-8")
	public List<CommentDTO> comment_list(@PathVariable("no") long no, @PathVariable("cno") long co,
			@ModelAttribute("comments") CommentDTO comments) throws Exception {
		List<CommentDTO> commentlist = commentService.getCommentList(no);
		System.out.println("★★★" + commentlist.toString());
		return commentlist;
	}

	@PostMapping(value = "/classdetail/{no}/insert", produces = "application/json; charset=UTF-8")
	public @ResponseBody String comment_insert(@PathVariable("no") long no, @RequestBody String content_textVal,
			HttpServletRequest request, HttpSession session) throws Exception {
		ResponseEntity<String> entity = null;
		
		JSONObject jObject = new JSONObject(content_textVal);
	    String content = jObject.getString("content_textVal");
	    
		CommentDTO comments = new CommentDTO();
		comments.setContent(content);
		comments.setLecture_id(no);
		comments.setMember_id((String) session.getAttribute("id"));
		comments.setName((String) session.getAttribute("name"));
		comments.setRegdate(DateFormatClass.strDateNow());
		System.out.println(comments.toString());
		try {
			commentService.insertComment(comments);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@PostMapping(value = "/classdetail/{no}/{cno}/update", produces = "application/json; charset=UTF-8")
	@ResponseBody 
	public String comment_update(@ModelAttribute CommentDTO commentDTO, @PathVariable("no") long no, @PathVariable("cno") long cno, @RequestBody String content_textVal, HttpServletRequest request, HttpSession session) throws Exception { 
		
		commentDTO.setNo(cno);
		commentDTO.setMember_id((String)session.getAttribute("id"));
		commentDTO.setLecture_id(no);
		commentDTO.setContent(content_textVal);

		try {
			commentService.updateComment(commentDTO);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	  @PostMapping(value = "/classdetail/{no}/delete", produces ="application/json; charset=UTF-8")
	  @ResponseBody 
	  public String comment_delete(@ModelAttribute CommentDTO commentDTO, @PathVariable("no") long no, @RequestBody String strjson, HttpServletRequest request, HttpSession session) throws Exception {
		  
		JSONObject jObject = new JSONObject(strjson);
		String content_no = jObject.getString("cno");
		String content_name = jObject.getString("user_check");
		String me = (String)session.getAttribute("name");

		System.out.println("댓글쓴사람 : "+ content_name+ "나" + (String)session.getAttribute("name"));
		commentDTO.setNo(Long.parseLong(content_no));
		commentDTO.setMember_id((String)session.getAttribute("id"));
	  
		try {
			if(me.equals(content_name) == true) {
				commentService.deleteComment(commentDTO); 
				return "Success";
			}
			else
				return "False";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	  }
	 
	// 수강중인 회원만 댓글 가능하도록 수정해야함 (비회원, 수강생X는 댓글 불가 처리)

}