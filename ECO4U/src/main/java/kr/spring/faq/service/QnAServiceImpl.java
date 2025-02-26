package kr.spring.faq.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.faq.dao.QnAMapper;
import kr.spring.faq.vo.QnAVO;
import kr.spring.faq.vo.QnAcommentVO;



@Service
@Transactional
public class QnAServiceImpl implements QnAService{
	
	@Autowired
	private QnAMapper qnaMapper;
	
	@Override
	public void insertQnA(QnAVO qna) {
		qnaMapper.insertQnA(qna);
	}

	@Override
	public List<QnAVO> selectQnAList(Map<String, Object> map) {
		return qnaMapper.selectQnAList(map);
	}

	@Override
	public int selectRowCount(Map<String, Object> map) {
		return qnaMapper.selectRowCount(map);
	}

	@Override
	public QnAVO selectQnA(Integer q_num) {
		return qnaMapper.selectQnA(q_num);
	}

	@Override
	public void updateQnA(QnAVO qna) {
		qnaMapper.updateQnA(qna);
	}

	@Override
	public void deleteQnA(Integer q_num) {
		qnaMapper.deleteComment(q_num);
		qnaMapper.deleteCommentByQNum(q_num);
		qnaMapper.deleteQnA(q_num);
	}

	@Override
	public List<QnAcommentVO> selectListComment(Map<String, Object> map) {
		return qnaMapper.selectListComment(map);
	}

	@Override
	public int selectRowCountComment(Map<String, Object> map) {
		return qnaMapper.selectRowCountComment(map);
	}

	@Override
	public QnAcommentVO selectComment(Integer qc_num) {
		return qnaMapper.selectComment(qc_num);
	}

	@Override
	public void insertComment(QnAcommentVO qnacomment) {
		qnaMapper.insertComment(qnacomment);
	}

	@Override
	public void updateComment(QnAcommentVO qnacomment) {
		qnaMapper.updateComment(qnacomment);
	}

	@Override
	public void deleteComment(Integer qc_num) {
		qnaMapper.deleteComment(qc_num);
	}

	@Override
	public void deleteQnAChecked(String del_qna) {
		String[] ajaxMsg = del_qna.split(",");
		
		for(int i=0; i<ajaxMsg.length; i++) {
			qnaMapper.deleteComment(Integer.parseInt(ajaxMsg[i]));
			qnaMapper.deleteCommentByQNum(Integer.parseInt(ajaxMsg[i]));
			qnaMapper.deleteQnA(Integer.parseInt(ajaxMsg[i]));
		}
	}

	@Override
	public void updateComCnt(Integer q_num) {
		qnaMapper.updateComCnt(q_num);
	}
	
	//관리자
	@Override
	public List<QnAVO> selectmnQnAList(Map<String, Object> map) {
		return qnaMapper.selectmnQnAList(map);
	}

	@Override
	public int selectmnRowCount(Map<String, Object> map) {
		return qnaMapper.selectmnRowCount(map);
	}

	@Override
	public void updatemnQnA(QnAVO qna) {
		qnaMapper.updatemnQnA(qna);
	}
}
