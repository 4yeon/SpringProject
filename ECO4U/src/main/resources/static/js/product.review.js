$(function(){
	let currentPage;
	let count;
	let rowCount;
	
	//리뷰 목록
	function selectList(pageNum){
		currentPage = pageNum;
		
		$.ajax({
			url:'listReview.do',
			type:'post',
			data:{pageNum:pageNum,p_num:$('#p_num').val()},
			dataType:'json',
			cache:false,
			timeout:30000,
			success:function(param){
				count = param.count;
				rowCount = param.rowCount;
				
				if(pageNum == 1){
					//처음 호출시는 해당 ID의 div의 내부
					//내용물을 제거
					$('#output').empty();
				}
				
				//리뷰 목록 작업
				$(param.list).each(function(index, item){
					let output = '<div class="item">';
					output += '<h4>';
					output += item.id + '</h4>';
					output += '<div class="sub-item">';
					output += '<h4>' + item.r_title.replace(/\r\n/g,'<br>') + '</h4>';
					
					if(item.r_photoname==null){
						
					}else{
						output += '<img class="review-img" src="${pageContext.request.contextPath}/images/product/${item.r_photoname}">';
					}
					output += '<p>' + item.r_content.replace(/\r\n/g,'<br>') + '</p>';
					
					if(item.modify_date){
						output += '<span class="modify-date">' + item.modify_date + ' 수정</span>';
					}else{
						output += '<span class="modify-date">' + item.reg_date + '</span>';
					}
					
					if(param.user_num==item.mem_num){
						//로그인한 회원번호와 댓글 작성자 회원번호가 일치
						output += ' <input type="button" data-num="'+ item.re_num +'" value="수정" class="modify-btn">';
						output += ' <input type="button" data-num="'+ item.re_num +'" value="삭제" class="delete-btn">';
					}
					output += '<hr size="1" noshade>';
					output += '</div>';
					output += '</div>'; 
					
					//문서 객체에 추가
					$('#output').append(output);
				});
				
				//paging button 처리
				if(currentPage>=Math.ceil(count/rowCount)){
					//다음 페이지가 없음
					$('.paging-button').hide();
				}else{
					//다음 페이지가 존재
					$('.paging-button').show();
				}
			},
			error:function(){
				 alert('리뷰 목록에서 네트워크 오류 발생');
			}
		});
		
	}
	
	//다음 리뷰 보기 버튼 클릭시 데이터 추가
	$('.paging-button input').click(function(){
		selectList(currentPage + 1);
	});
	

	//리뷰 수정 버튼 클릭시 수정 폼 노출
	$(document).on('click','.modify-btn',function(){
		//리뷰 번호
		let r_num = $(this).attr('data-num');
		//리뷰 내용
		let content = $(this).parent().find('p').html().replace(/<br>/g,'\r\n');
		
		//리뷰 수정폼 UI
		let modifyUI = '<form id="mr_form">';
		modifyUI += '<input type="hidden" name="r_num" id="mr_num" value="'+ r_num +'">';
		modifyUI += '<textarea rows="3" cols="50" name="r_content" id="mr_content" class="rep-content">'+ content +'</textarea>';
		modifyUI += '<div id="mr_first"><span class="letter-count">300/300</span></div>';
		modifyUI += '<div id="mr_second" class="align-right">';
		modifyUI += '<input type="submit" value="수정">';
		modifyUI += ' <input type="button" value="취소" class="re-reset">';
		modifyUI += '</div>';
		modifyUI += '<hr size="1" noshade width="96%">';
		modifyUI += '</form>';
		
		//이전에 이미 수정하는 리뷰가 있을 경우 수정 버튼을
		//클릭하면 숨김 sub-item을 환원시키고 수정폼을 초기화
		initModifyForm();
		
		//지금 클릭해서 수정하고자 하는 데이터는 감추기
		$(this).parent().hide();
		
		//수정 폼을 수정하고자 하는 데이터가 있는 div에 노출
		$(this).parents('.item').append(modifyUI);
		
		//입력한 글자수 셋팅
		let inputLength = $('#mr_content').val().length;
		let remain = 300 - inputLength;
		remain += '/300';
		
		//문서 객체에 반영
		$('#mr_first .letter-count').text(remain);
		
	});
	//수정 폼에서 취소 버튼 클릭시 수정 폼 초기화
	$(document).on('click','.re-reset',function(){
		initModifyForm();
	});
	//수정 폼 초기화
	function initModifyForm(){
		$('.sub-item').show();
		$('#mr_form').remove();
	}
	//리뷰 수정
	$(document).on('submit','#mr_form',function(event){
		if($('#mr_content').val().trim()==''){
			alert('내용을 입력하세요!');
			$('#mr_content').val('').focus();
			return false;
		}
		
		//폼에 입력한 데이터 반환
		let form_data = $(this).serialize();
		
		//수정
		$.ajax({
			url:'updateReview.do',
			type:'post',
			data:form_data,
			dataType:'json',
			cache:false,
			timeout:30000,
			success:function(param){
				if(param.result=='logout'){
					alert('로그인해야 수정할 수 있습니다.');
				}else if(param.result=='success'){
					$('#mr_form').parent()
					              .find('p')
                                  .html($('#mr_content').val()
                                         .replace(/</g,'&lt;')
                                         .replace(/>/g,'&gt;')
                                         .replace(/\r\n/g,'<br>')
                                         .replace(/\r/g,'<br>')
                                         .replace(/\n/g,'<br>'));
                    //최근 수정일 처리
                    $('#mr_form').parent()
                                  .find('.modify-date')
                                  .text('최근 수정일: 5초 미만');
					//수정폼 초기화
					initModifyForm();
				}else if(param.result=='wrongAccess'){
					alert('타인의 리뷰는 수정할 수 없습니다.');
				}else{
					alert('수정 시 오류 발생');
				}
			},
			error:function(){
				alert('리뷰 수정에서 네트워크 오류 발생');
			}
		});
		//기본 이벤트 제거
		event.preventDefault();
	});
	//리뷰 삭제
	$(document).on('click','.delete-btn',function(){
		//리뷰 번호
		let r_num = $(this).attr('data-num');
		
		$.ajax({
			url:'deleteReview.do',
			type:'post',
			data:{r_num:r_num},
			dataType:'json',
			cache:false,
			timeout:30000,
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 삭제할 수 있습니다.');
				}else if(param.result == 'success'){
					alert('삭제 완료!');
					selectList(1);
				}else if(param.result == 'wrongAccess'){
					alert('타인의 리뷰를 삭제할 수 없습니다.');
				}else{
					alert('리뷰 삭제 시 오류 발생');
				}
			},
			error:function(){
				alert('리뷰 삭제에서 네트워크 오류 발생');
			}
		});
		
	});

	//초기 데이터(목록) 호출
	selectList(1);
});