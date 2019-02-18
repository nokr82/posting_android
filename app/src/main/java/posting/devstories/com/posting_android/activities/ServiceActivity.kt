package posting.devstories.com.posting_android.activities


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_notice.*
import kotlinx.android.synthetic.main.activity_service.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.NoticeAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.NoticeAdapter
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.util.ArrayList

class ServiceActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)


        val url = Config.url + "/agree/agree1"
        serviceWV.loadUrl(url)

        finish4LL.setOnClickListener {
            finish()
        }






    }






    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }


}
