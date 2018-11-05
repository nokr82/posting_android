package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object VersionAction {

    // 공지
    fun versionCheck(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/version/index.json", params, handler)
    }

}