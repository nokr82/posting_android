package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object NoticeAction {

    // 공지
    fun post_notice(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/notice/post_notice.json", params, handler)
    }

}