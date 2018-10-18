package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object StudentJoinAction {

    //학교목록
    fun StudentJoin(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/join/join.json", params, handler)
    }
}