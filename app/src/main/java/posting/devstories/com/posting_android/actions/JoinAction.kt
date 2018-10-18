package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object JoinAction {

    // 핸드폰 인증
    fun send_sms(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/join/sms_code.json", params, handler)
    }

    // 핸드폰 인증
    fun join(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/join/join.json", params, handler)
    }

}