package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

/**
 * Created by hooni
 */
object SchoolAction {

    //학교목록
    fun School(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/school/index.json", params, handler)
    }
}