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

    // 비밀번호 찾기
    fun find_passwd(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/login/find_passwd.json", params, handler)
    }

}