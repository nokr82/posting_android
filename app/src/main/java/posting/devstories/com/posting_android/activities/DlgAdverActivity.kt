package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
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
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)


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

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            if (progressDialog != null) {
                progressDialog!!.show()
            }

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }

        }

    }

}
