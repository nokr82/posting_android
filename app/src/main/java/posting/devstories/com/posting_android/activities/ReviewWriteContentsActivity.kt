package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_reivew_write_contents.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.ReviewAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.io.ByteArrayInputStream


class ReviewWriteContentsActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null

    var capture: Bitmap?= null
    var member_type = ""
    var image_uri:String? = null
    var imageUri:Uri? = null
    var str:String? = null
    var member_id = -1
    var type = -1
    var contents = ""
    var contents2:String?=null
    var count:String?=null
    var geterror = ""


    var postingType:String?= null
    var review_id = -1
    var company_member_id = -1



    lateinit var adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reivew_write_contents)

        this.context = this
        progressDialog = ProgressDialog(context)

        member_id =  PrefUtils.getIntPreference(context,"member_id")
        member_type = PrefUtils.getStringPreference(context, "member_type")

        intent = getIntent()

        // 리뷰 타입 G-갤러리 P-포토 T-텍스트
        review_id = intent.getIntExtra("review_id", -1)
        postingType = intent.getStringExtra("postingType")
        image_uri = intent.getStringExtra("image_uri")
        company_member_id = intent.getIntExtra("company_member_id", -1)
        contents2 = intent.getStringExtra("contents")


        val h = intent.getStringExtra("imageUri")
        if(h != null) {
            imageUri = Uri.parse(h)
        }

        // 이미지 uri 로드
        if (review_id > 0 && "M" == postingType) {
            var image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsPosting)
            popupRL.visibility = View.VISIBLE

        } else if ("T" == postingType) {
            popupRL.visibility = View.GONE

        }




        contentET.setText(contents2)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(str, options)
        options.inJustDecodeBounds = false

        backLL.setOnClickListener {
            finish()
        }

        println("postingType : $postingType, imageUri : $imageUri")

        if (postingType.equals("P") && imageUri != null){
            capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            captureIV.setImageBitmap(capture)
            popupRL.visibility = View.VISIBLE

        }else if (postingType.equals("G") && imageUri != null){
            capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            captureIV.setImageBitmap(capture)
            // ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsUserProfile)
            popupRL.visibility = View.VISIBLE

            // captureIV.setImageBitmap(Utils.getImage(context.contentResolver, imgid))
        }else if (postingType.equals("M") && imageUri != null){
            capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            captureIV.setImageBitmap(capture)
            // com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsUserProfile)
            captureIV.visibility = View.VISIBLE
        }




        nextLL2.setOnClickListener {

            contents = Utils.getString(contentET)

            if (contents == "" || contents == null || contents.isEmpty()) {
                    geterror = "내용을 입력해주세요"

                    Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
                } else {

                    nextLL2.isEnabled = false

                if (review_id < 1){
                        write()
                    }else{

                        edit_posting()
                    }
                }




        }


    }



    fun write(){


        val params = RequestParams()
        params.put("company_member_id", company_member_id)
        params.put("member_id", member_id)
        params.put("contents", contents)

        if (capture != null) {

            params.put("upload", ByteArrayInputStream(Utils.getByteArray(capture)))
        }

        println("params : $params")


        ReviewAction.write(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Utils.hideKeyboard(context)
//                        val intent = Intent(context,MainActivity::class.java)

                        try {
                            contentResolver.delete(imageUri, null, null);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        Toast.makeText(context, "글작성이 완료되었습니다", Toast.LENGTH_SHORT).show()

                        val intent = Intent();
                        setResult(Activity.RESULT_OK, intent)


                        finish()


                    } else {
                        nextLL2.isEnabled = true
                        geterror = "등록중 장애가 발생하였습니다."
                        Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
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
                Utils.alert(context, "등록중 장애가 발생하였습니다.")
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

    fun edit_posting(){

        val params = RequestParams()
        params.put("company_member_id", company_member_id)
        params.put("member_id", member_id)
        params.put("contents", contents)
        params.put("review_id", review_id)


        if(postingType == "T") {
            params.put("image", "")
            params.put("image_uri", "")
        } else {

            if (imageUri != null) {
                val add_file = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                // val add_file = Utils.getImage(context.contentResolver, imgid)
                params.put("upload",ByteArrayInputStream(Utils.getByteArray(add_file)))
            }


        }

        ReviewAction.edit_review(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Utils.hideKeyboard(context)

                        var intent = Intent();
                        intent.putExtra("review_id", review_id)
                        intent.action = "EDIT_REVIEW"
                        sendBroadcast(intent)

                        intent = Intent();
                        setResult(Activity.RESULT_OK, intent)
                        finish()



                    } else {
                        geterror = "작성실패"

                        Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
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

    override fun finish() {
        super.finish()

        Utils.hideKeyboard(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
