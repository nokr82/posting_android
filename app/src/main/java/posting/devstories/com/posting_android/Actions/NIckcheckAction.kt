package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object NIckcheckAction {

    //학교목록
    fun Nick(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/join/check_nick_name.json", params, handler)
    }
}