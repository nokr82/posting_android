package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object LoginAction {

    // 로그인
    fun login(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/login/email_login.json", params, handler)
    }
}