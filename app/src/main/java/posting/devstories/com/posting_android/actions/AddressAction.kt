package posting.devstories.com.posting_android.Actions

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import posting.devstories.com.posting_android.base.HttpClient
import java.util.HashMap

/**
 * Created by hooni
 */
object AddressAction {

    // 지도 검색 (정확도)
    fun search_map(keyword: String, page: Int, size: Int, handler: JsonHttpResponseHandler) {
        val headers = HashMap<String, String>()
        //        headers.put("Authorization", "KakaoAK 9928b24dd82518aeab9ebc55fa5989b3");
        headers["Authorization"] = "KakaoAK ec90c0431431237eb237304909cca0fb"
        val params = RequestParams()
        params.put("keyword", keyword)
        params.put("page", page)
        params.put("size", size)

        //        HttpClient.get("http://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword + "&page=" + page + "&size=" + size, headers, params, handler);
        HttpClient.get(
            "http://dapi.kakao.com/v2/local/search/address.json?query=$keyword&page=$page&size=$size",
            headers,
            params,
            handler
        )
    }

}