package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object PostingAction {

    // 글쓰기
    fun write(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/write.json", params, handler)
    }
    //글목록보기
    fun view(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/index.json", params, handler)
    }
    //메인목록
    fun mainlist(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/main/index.json", params, handler)
    }

    //상세페이지및댓글보기
    fun detail(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/detail.json", params, handler)
    }

    // 저장하기
    fun save_posting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/save_posting.json", params, handler)
    }

    // 댓글 작성
    fun write_comments(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/write_comments.json", params, handler)
    }
    //삭제
    fun del_posting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/del_posting.json", params, handler)
    }
    //수정
    fun edit_posting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/edit_posting.json", params, handler)
    }
    //쿠폰사용
    fun use_posting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/use_posting.json", params, handler)
    }
    // 저장한 회원 목록 (스터디/동아리/미팅)
    fun save_members(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/save_members.json", params, handler)
    }

    fun savedel_posting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/savedel_posting.json", params, handler)
    }

    fun today_posting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/posting/today_posting.json", params, handler)
    }
}