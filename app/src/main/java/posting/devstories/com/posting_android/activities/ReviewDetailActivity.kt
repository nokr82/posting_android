package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_review_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.ReviewAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.text.SimpleDateFormat
import java.util.*

class ReviewDetailActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    val EDIT_REIVEW = 101
    var member_id = -1
    var review_id  = -1
    var company_member_id  = -1
    var image_uri  = ""
    var contents  = ""

    internal var delReviewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)


        val filter1 = IntentFilter("DEL_REVIEW")
        registerReceiver(delReviewReceiver, filter1)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        intent = getIntent()
        review_id = intent.getIntExtra("review_id", -1)

        member_id = PrefUtils.getIntPreference(context, "member_id")

        menuIV.setOnClickListener {
            dlgView()
        }

        backLL.setOnClickListener {
            finish()
        }

        policeTV.setOnClickListener {
            policedlgView()
        }

        loadData()

    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("review_id", review_id)

        ReviewAction.detail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val reviewData = response.getJSONObject("review")
                        val review = reviewData.getJSONObject("Review")
                        val member = reviewData.getJSONObject("Member")

                        company_member_id = Utils.getInt(review, "company_member_id")

                        var Image = Utils.getString(review, "Image")

                       image_uri = Utils.getString(review, "image_uri")
//                        var created =   Utils.getString(posting, "created")
                        if (member_id == Utils.getInt(member, "id")){
//                            myLL.visibility = View.VISIBLE
                            menuIV.visibility = View.VISIBLE
                        }

                        contents =   Utils.getString(review, "contents")
                        var nick_name = Utils.getString(member, "nick_name")

                        var profile = Config.url + Utils.getString(member,"image_uri")
                        ImageLoader.getInstance().displayImage(profile, writerIV, Utils.UILoptionsUserProfile)

                        if("3" == Utils.getString(member, "member_type")) {
                            writerIV.setOnClickListener {
                                var intent = Intent(context, OrderPageActivity::class.java)
                                intent.putExtra("company_id", Utils.getInt(member, "id"))
                                startActivity(intent)
                            }
                        }

                        contentTV.text = contents
                        wnameTX.text = nick_name

                        val sdf = SimpleDateFormat("MM월dd일", Locale.KOREA)
                        val created = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utils.getString(review, "created"))
                        val create_date = sdf.format(created)

                        upTX.text = create_date

                        //uri를 이미지로 변환시켜준다
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            ImageLoader.getInstance().displayImage(image, imgIV, Utils.UILoptionsUserProfile)
                            imgIV.visibility = View.VISIBLE
                            contentsTV.visibility = View.GONE
                        } else {
                            contentsTV.text = contents
                            contentsTV.visibility = View.VISIBLE
                            imgIV.visibility = View.GONE
                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            private fun error() {
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
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

    fun policedlgView(){

        var intent = Intent(context, DlgReviewActivity::class.java)
        intent.putExtra("dlgtype", "police")
        intent.putExtra("member_id", member_id)
        intent.putExtra("review_id", review_id)
        startActivity(intent)

    }

    fun dlgView(){

        var intent = Intent(context, DlgReviewActivity::class.java)
        intent.putExtra("review_id", review_id)
        intent.putExtra("dlgtype", "MyReview")
        intent.putExtra("image_uri",image_uri)
        intent.putExtra("contents",contents)
        intent.putExtra("company_member_id",company_member_id)
        startActivityForResult(intent, EDIT_REIVEW)

    }


    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        try {
            if (delReviewReceiver != null) {
                unregisterReceiver(delReviewReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                EDIT_REIVEW -> {
                    loadData()
                }
            }
        }

    }

}
