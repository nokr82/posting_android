package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_match_info.*
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.BackPressCloseHandler
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config

/**
 * Created by dev1 on 2018-02-28.
 */

class MatchInfoActivity : RootActivity() {

    private var context: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val backPressCloseHandler: BackPressCloseHandler? = null

    var posting_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_info)

        this.context = this
        progressDialog = ProgressDialog(context)

        posting_id = intent.getStringExtra("posting_id")

        finishLL.setOnClickListener {
            finish()
        }

        loadData()

    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("posting_id", posting_id)

        PostingAction.save_members(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val postingSaves = response.getJSONArray("postingSaves")

                        val posting = response.getJSONObject("posting")
                        val member = response.getJSONObject("member")

                        var profile_uri = Config.url + Utils.getString(member,"image_uri")
                        ImageLoader.getInstance().displayImage(profile_uri, myCV, Utils.UILoptionsProfile)

                        var posting_uri = Config.url + Utils.getString(posting,"image_uri")
                        ImageLoader.getInstance().displayImage(posting_uri, imageIV, Utils.UILoptionsPosting)

                        postingCntTV.text = postingSaves.length().toString() + "/" + Utils.getString(posting, "count")
                        matchCntTV.text = "0"

                        alarmCntTV.visibility = View.GONE
                        alarmCntTV.text = "0"

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }


            override fun onStart() {
                // show dialog
                if (progressDialog != null) {

                    progressDialog!!.show()
                }
            }

            override fun onFinish() {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }


    private fun back() {
        finish()
    }

    fun onClickBack(view: View) {
        back()
    }

    override fun onDestroy() {

        progressDialog = null

        super.onDestroy()
    }


}
