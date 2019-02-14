package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.dlg_adver.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class DlgAdverActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dlg_adver)

        this.context = this
        progressDialog = ProgressDialog(context)

        val link = intent.getStringExtra("link")

        backLL.setOnClickListener {
            finish()
        }

        webWV.setWebViewClient(WebViewClientClass())
        webWV.setVerticalScrollBarEnabled(true)
        webWV.setBackgroundColor(Color.parseColor("#00000000"))
        webWV.loadUrl(link)

    }

    private inner class WebViewClientClass : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

}
