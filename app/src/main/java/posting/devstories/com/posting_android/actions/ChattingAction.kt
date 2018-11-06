package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient

object ChattingAction {

    // 채팅방 체크
    fun chattingCheck(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/chatting/chattingCheck.json", params, handler)
    }
    // 채팅
    fun chatting(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/chatting/chatting.json", params, handler)
    }

    // 채팅방 만들기
    fun chattingAdd(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/chatting/chattingAdd.json", params, handler)
    }

    // 메세지 보내기
    fun sendMessage(params: RequestParams, handler: JsonHttpResponseHandler) {
        HttpClient.post("/chatting/sendMessage.json", params, handler)
    }

}