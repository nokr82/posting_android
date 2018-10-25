package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.LoginAction
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.Actions.PostingAction.detail
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.io.ByteArrayInputStream

class DetailActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false
    var member_id = -1
    var id  = ""
    var image_uri = ""
    var contents = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()

        id = intent.getStringExtra("id")

        member_id = PrefUtils.getIntPreference(context, "member_id")

        detaildata(member_id,id)

    }

    fun detaildata(member_id:Int ,posting_id: String) {
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
                        val member  = data.getJSONObject("Member")
                        var id = Utils.getString(posting, "id")
                        var member_id =   Utils.getString(posting, "member_id")
                        var Image = Utils.getString(posting, "Image")
                        var image_uri = Utils.getString(posting, "image_uri")
                        var created =   Utils.getString(posting, "created")
                        var contents =   Utils.getString(posting, "contents")
                        var nick_name = Utils.getString(member, "nick_name")

                        contentTV.text = contents
                        wnameTX.text = nick_name
                        upTX.text = created

                        //uri를 이미지로 변환시켜준다
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            ImageLoader.getInstance().displayImage(image, imgIV, Utils.UILoptionsUserProfile)
                        } else {

                        }



                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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
