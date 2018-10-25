package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction.detail
import posting.devstories.com.posting_android.Actions.PostingAction.save_posting
import posting.devstories.com.posting_android.Actions.PostingAction.write_comments
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false
    var member_id = -1
    var posting_id  = ""
    var image_uri = ""
    var contents = ""
    var p_comments_id = -1
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()

        posting_id = intent.getStringExtra("id")

        member_id = PrefUtils.getIntPreference(context, "member_id")

        commentsET.setOnEditorActionListener { textView, i, keyEvent ->

            when (i) {
                EditorInfo.IME_ACTION_DONE -> {

                    var comments = Utils.getString(commentsET)

                    if(!comments.isEmpty() && comments != "") {
                        writeComments(comments)
                    }

                }
            }
            return@setOnEditorActionListener true

        }

        postingLL.setOnClickListener {

            if(count < 1) {

                Toast.makeText(context, "남은 포스팅 갯수가 없습니다.", Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }

            savePosting();

        }

        detaildata()

    }

    fun writeComments(comments:String) {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)
        params.put("comments", comments)
        params.put("p_comments_id", p_comments_id)

        write_comments(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        detaildata()

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

    fun savePosting() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)

        save_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var intent = Intent(context, DlgStorageActivity::class.java)
                        startActivity(intent)


                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.action = "SAVE_POSTING"
                        sendBroadcast(intent)

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

    fun detaildata() {
        val params = RequestParams()
        params.put("member_id",member_id)
        params.put("posting_id", posting_id)

        detail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("posting")

                        val posting = data.getJSONObject("Posting")

                        var id = Utils.getString(posting, "id")
                        var member_id =   Utils.getString(posting, "member_id")
                        var Image = Utils.getString(posting, "Image")
                        var image_uri = Utils.getString(posting, "image_uri")
                        count = Utils.getInt(posting, "leftCount")
//                        var created =   Utils.getString(posting, "created")

                        val member  = data.getJSONObject("Member")

                        var contents =   Utils.getString(posting, "contents")
                        var nick_name = Utils.getString(member, "nick_name")

                        contentTV.text = contents
                        wnameTX.text = nick_name

                        val sdf = SimpleDateFormat("MM월dd일", Locale.KOREA)
                        val created = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utils.getString(posting, "created"))
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

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
