package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_reivew_write_contents.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.ReviewAction
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils
import java.io.ByteArrayInputStream

class ReviewWriteContentsActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var imgid:String? = null
    var capture: Bitmap?= null
    var member_type = ""
    var image_uri:String? = null
    var image:String? = null
    var text:String? = null
    var member_id = -1
    var type:String?=null
    var contents = ""
    var count:String?=null
    var geterror = ""
    var company_member_id = -1
    var review_id = -1

    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reivew_write_contents)

        this.context = this
        progressDialog = ProgressDialog(context)

        member_id =  PrefUtils.getIntPreference(context,"member_id")

        intent = getIntent()
        review_id = intent.getIntExtra("review_id", -1)
        company_member_id = intent.getIntExtra("company_member_id", -1)
        contents = intent.getStringExtra("contents")
        imgid = intent.getStringExtra("imgid")
        capture = intent.getParcelableExtra("capture")
        image = intent.getStringExtra("image")

        if(review_id > 0) {
            contentET.setText(contents)
        }

        backLL.setOnClickListener {
            finish()
        }

        //이미지
        img2RL.background = Drawable.createFromPath(imgid)
        captureIV.setImageBitmap(capture)
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsUserProfile)

        if (imgid != null && "" != imgid && imgid!!.length> 1&&capture != null&&image != null){
            popupRL.visibility = View.VISIBLE
        }

        nextLL2.setOnClickListener {

            contents = Utils.getString(contentET)

            if(contents==""||contents==null|| contents.isEmpty()){
                geterror = "내용을 입력해주세요"

                Toast.makeText(context,geterror,Toast.LENGTH_SHORT).show()
            } else {
                if (review_id < 1){
                    write()
                }else{
                    edit_review()
                }
            }

        }

    }

    fun write(){

        val params = RequestParams()
        params.put("company_member_id", company_member_id)
        params.put("member_id", member_id)
        params.put("contents", contents)

        if (capture==null){

        }else{
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(capture)))
        }

        if (imgid.equals("")||imgid==null){

        }else{
            val add_file = Utils.getImage(context.contentResolver, imgid)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(add_file)))

        }

        ReviewAction.write(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        val intent = Intent();
                        setResult(Activity.RESULT_OK, intent)
                        finish()

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "올리는중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
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

    fun edit_review() {

        val params = RequestParams()
        params.put("company_member_id", company_member_id)
        params.put("member_id", member_id)
        params.put("contents", contents)

        if (capture==null){

        }else{
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(capture)))
        }

        if (imgid.equals("")||imgid==null){

        }else{
            val add_file = Utils.getImage(context.contentResolver, imgid)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(add_file)))

        }

        ReviewAction.edit_review(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        val intent = Intent();
                        setResult(Activity.RESULT_OK, intent)
                        finish()

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONArray?) {
                super.onSuccess(statusCode, headers, response)
            }

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(context, "올리는중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
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

}
