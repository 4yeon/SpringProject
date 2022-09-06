package kr.spring.faq.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.spring.faq.service.FAQService;
import kr.spring.faq.vo.FAQVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;

@Controller
public class FAQController {
	private static final Logger logger = LoggerFactory.getLogger(FAQController.class);
	
	private int rowCount = 30;
	private int pageCount = 30;
	
	@Autowired
	private FAQService FAQService;
	
	//자바빈(VO) 초기화
	@ModelAttribute
	public FAQVO initCommand() {
		return new FAQVO();
	}
	
	//===========게시판 글 등록============//
	//등록 폼
	@GetMapping("/faq/faqwrite.do")
	public String form() {
		return "faqWrite";
	}
	//등록 폼에서 전송된 데이터 처리
	@PostMapping("/faq/faqwrite.do")
	public String submit(@Valid FAQVO FAQVO,
			BindingResult result,
			HttpServletRequest request,
			HttpSession session,
			Model model) {

		logger.debug("<<게시판 글 저장>> : " + FAQVO);

		//유효성 검사 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form();
		}

		MemberVO user = (MemberVO)session.getAttribute("user");
		
		//회원번호 셋팅
		FAQVO.setMem_num(user.getMem_num());

		//글쓰기
		FAQService.insertBoard(FAQVO);

		//View에 표시할 메시지
		model.addAttribute(
				"message", "글 등록이 완료되었습니다.");
		model.addAttribute(
				"url", request.getContextPath()+"/faq/faqlist.do");

		return "common/resultView";
	}


	/*
	 * //===========게시판 글 목록============//
	 * 
	 * @GetMapping("/faq/faqlist.do") public String formFaq() { return "faqList"; }
	 */
	
	@RequestMapping("/faq/faqlist.do")
	public ModelAndView process(
//			@RequestParam int f_num,
			@RequestParam(value="pageNum",defaultValue="1") int currentPage,
			@RequestParam(value="keyfield",defaultValue="") String keyfield,
			@RequestParam(value="category",defaultValue="") String category
			) {
		
//		logger.debug("<<f_num>> : " + f_num);
		
//		logger.debug("<<process>> : 에 들어옴");
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyfield", keyfield);
		map.put("category", category);
		
//		FAQService.selectBoard(f_num);
		
		//글의 총개수(검색된 글의 개수)
		int count = FAQService.selectRowCount(map);
		
		logger.debug("<<count>> : " + count);
		
		//페이지 처리
		PagingUtil page = 
				new PagingUtil(keyfield,category,currentPage,count,rowCount,pageCount,"faqlist.do");
				
		List<FAQVO> list = null;
		if(count > 0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = FAQService.selectList(map);
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("faqList");
		mav.addObject("count", count);
		mav.addObject("list", list);
		mav.addObject("page", page.getPage());
		
		
		return mav;
	}
	
	//글상세
//	@RequestMapping("/faq/faqdetail.do")
//	public ModelAndView detail(
//			@RequestParam int f_num
//			) {
//		
//		logger.debug("<<f_num>> : " + f_num);
//		
//		FAQVO faq = FAQService.selectBoard(f_num);
//		
//		                          //뷰 이름    속성명   속성값
//		return new ModelAndView("faqView","faq",faq);
//	}
	
	//===========게시판 글수정===========//
	//수정 폼
	@GetMapping("/faq/faqupdate.do")
	public String formUpdate(
			Model model,@RequestParam int f_num) {
		FAQVO faqVO = FAQService.selectBoard(f_num);
		
		model.addAttribute("fAQVO", faqVO);
		
		return "faqModify";
	}
	//수정 폼에서 전송된 데이터 처리
	@PostMapping("/faq/faqupdate.do")
	public String submitUpdate(@Valid FAQVO faqVO,
			            BindingResult result,
			            HttpServletRequest request,
			            Model model) {
		
		logger.debug("<<글수정>> : " + faqVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			//title 또는 content가 입력되지 않아 유효성 체크에
			//걸리면 파일 정보를 잃어버리기 때문에 품을
			//호출할 때 다시 셋팅해주어야 함.
			FAQService.selectBoard(faqVO.getF_num());
			
			return "faqModify";
		}
		
		//글수정
		FAQService.updateBoard(faqVO);
		
		//View에 표시할 메시지
		model.addAttribute("message", "글수정 완료!!");
		model.addAttribute("url", request.getContextPath()+"/faq/faqlist.do");	

		return "common/resultView";
	}
	
	//==========게시판 글삭제==========//
	@RequestMapping("/faq/delete.do")
	public String submitDelete(
			       @RequestParam int f_num,
			       Model model,
			       HttpServletRequest request) {
		
		logger.debug("<<글삭제>> : " + f_num);
		
		//글삭제
		FAQService.deleteBoard(f_num);
		
		//View에 표시할 메시지
		model.addAttribute("message", "글삭제 완료!!");
		model.addAttribute("url", request.getContextPath()+"/faq/faqlist.do");
		
		return "common/resultView";
	}
	
}




