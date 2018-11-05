package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

object AlarmAction {

    // 알림 목록
    fun alarm_list(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/alarm/alarm_list.json", params, handler)
    }

}