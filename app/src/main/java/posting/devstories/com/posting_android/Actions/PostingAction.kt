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
    fun mainlist(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/main/index.json", params, handler)
    }
}