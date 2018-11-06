package posting.devstories.com.posting_android.activities


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_notice.*
import kotlinx.android.synthetic.main.activity_privacy.*
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

class CouponTextActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coupon_activity)
        this.context = this
        progressDialog = ProgressDialog(context)








    }






    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }


}
