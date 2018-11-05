package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object ReviewAction {

    // 글쓰기
    fun write(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/review/write.json", params, handler)
    }

    // 상세
    fun detail(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/review/detail.json", params, handler)
    }

    // 삭제
    fun del(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/review/del.json", params, handler)
    }

    // 신고
    fun report(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/review/report.json", params, handler)
    }

}