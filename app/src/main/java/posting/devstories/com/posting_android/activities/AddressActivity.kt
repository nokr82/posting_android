package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AbsListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_address_search.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.BackPressCloseHandler
import posting.devstories.com.posting_android.base.RootActivity

/**
 * Created by dev1 on 2018-02-28.
 */


class AddressActivity : RootActivity(), AbsListView.OnScrollListener {

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val backPressCloseHandler: BackPressCloseHandler? = null

    private var userScrolled = false
    private var lastItemVisibleFlag = false
    private var totalItemCountScroll = 0
    private val keyword: String? = null
    private var page = 1
    private val itemCount = 0
    private val totalItemCount = 0
    private val size = 1
    private var lastcount = 0
    private val visibleThreshold = 10

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_search)

        this.context = this
        progressDialog = ProgressDialog(context)

        finishLL.setOnClickListener {
            finish()
        }

        init_webView()
        handler = Handler()
    }

    fun init_webView() {
        // JavaScript 허용
        webViewWV!!.settings.javaScriptEnabled = true
        // JavaScript의 window.open 허용
        webViewWV!!.settings.javaScriptCanOpenWindowsAutomatically = true
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        webViewWV!!.addJavascriptInterface(AndroidBridge(), "AddressData")
        // web client 를 chrome 으로 설정
        webViewWV!!.webChromeClient = WebChromeClient()
        // webview url load
        webViewWV!!.loadUrl("http://13.125.241.200/Zipcode/daum")

        webViewWV!!.webViewClient = object : WebViewClient() {
            // 링크 클릭에 대한 반응
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            // 웹페이지 호출시 오류 발생에 대한 처리
            override fun onReceivedError(view: WebView, errorcode: Int, description: String, fallingUrl: String) {
                Toast.makeText(context, "오류 : $description", Toast.LENGTH_SHORT).show()
            }

            // 페이지 로딩 시작시 호출
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {}

            //페이지 로딩 종료시 호출
            override fun onPageFinished(view: WebView, Url: String) {}
        }

    }

    private inner class AndroidBridge {
        @JavascriptInterface
        fun setAddress(addr: String) {
            handler!!.post {

                println("addr : " + addr)

                var intent = Intent();
                intent.putExtra("address", addr)
                setResult(RESULT_OK, intent)

                // WebView를 초기화 하지않으면 재사용할 수 없음
                init_webView()
                finish()
            }
        }

    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            userScrolled = true
        } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            userScrolled = false
            //화면이 바닥에 닿았을때
            if (totalItemCount > itemCount) {
                page++
                lastcount = totalItemCountScroll
            }
        }

    }

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (userScrolled && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold && itemCount < this.totalItemCount && this.totalItemCount > 0) {
            if (this.totalItemCount > itemCount) {
                //                page++;
                //                adapterData.clear();
                //                searchList();
            }
        }

        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem)
        // + 현재 화면에 보이는 리스트 아이템의갯수(visibleItemCount)가
        // 리스트 전체의 갯수(totalOtemCount)-1 보다 크거나 같을때
        lastItemVisibleFlag = totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount
        totalItemCountScroll = totalItemCount
    }

    private fun back() {
        finish()
    }

    fun onClickBack(view: View) {
        back()
    }

    override fun onDestroy() {

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        super.onDestroy()
    }


}
