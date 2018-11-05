package posting.devstories.com.posting_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
    var member_id = -1
    var review_id  = -1
    var image_uri  = ""
    var contents  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        this.context = this
        progressDialog = ProgressDialog(context)

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

                        var Image = Utils.getString(review, "Image")

                       image_uri = Utils.getString(review, "image_uri")
//                        var created =   Utils.getString(posting, "created")
                        if (member_id == Utils.getInt(member, "id")){
                            myLL.visibility = View.VISIBLE
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
                        } else {
                            contentsTV.text = contents
                            contentsTV.visibility = View.VISIBLE
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
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
        val titleTV = dialogView.findViewById<TextView>(R.id.titleTV)
        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)
        titleTV.text = "이 포스트를 신고하는 이유를 선택하세요"
        delTV.text = "불건전합니다"
        modiTV.text = "부적절합니다"
        recyTV.text = "스팸입니다"

        mPopupDlg =  builder.setView(dialogView).show()


        delTV.setOnClickListener {
            report("1")
            mPopupDlg.dismiss()
        }
        modiTV.setOnClickListener {
            report("2")
            mPopupDlg.dismiss()
        }
        recyTV.setOnClickListener {
            report("3")
            mPopupDlg.dismiss()
        }
    }

    fun dlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.myposting_dlg, null)
        val delTV = dialogView.findViewById<TextView>(R.id.delTV)
        val modiTV = dialogView.findViewById<TextView>(R.id.modiTV)
        val recyTV = dialogView.findViewById<TextView>(R.id.recyTV)

        delTV.setOnClickListener {
            del_posting()
            mPopupDlg!!.cancel()
        }

        modiTV.setOnClickListener {

            val intent = Intent(context, ReviewWriteActivity::class.java)
            intent.putExtra("review_id", review_id)
            intent.putExtra("image_uri",image_uri)
            intent.putExtra("contents",contents)

            context.startActivity(intent)
            finish()

            mPopupDlg!!.cancel()

        }

        recyTV.setOnClickListener {

        }


        mPopupDlg =  builder.setView(dialogView).show()

    }


    fun del_posting(){
        val params = RequestParams()
        params.put("review_id", review_id)

        ReviewAction.del(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("review_id", review_id)
                        intent.action = "DEL_REVIEW"
                        sendBroadcast(intent)

                        finish()

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

    fun report(type:String){
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("review_id", review_id)
        params.put("type", type)

        ReviewAction.report(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        var intent = Intent(context, DlgPoliceActivity::class.java)
                        startActivity(intent)

                    } else if("already" == result) {
                        Toast.makeText(context, "신고한 게시물입니다.", Toast.LENGTH_LONG).show()
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

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
