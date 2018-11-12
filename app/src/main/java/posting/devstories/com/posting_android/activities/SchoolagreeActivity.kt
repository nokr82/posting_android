package posting.devstories.com.posting_android.activities


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_schoolagree.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.io.ByteArrayInputStream
import java.io.IOException

class SchoolagreeActivity : RootActivity() {

    private val GALLERY = 1
    private val CAMERA = 2

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private var has_branch_yn = "N"
    private var school_email_confirmed = "N"
    private var school_confirmed = "N"
    private var school_domain = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schoolagree)

        this.context = this

        has_branch_yn = intent.getStringExtra("has_branch_yn")
        school_email_confirmed = intent.getStringExtra("school_email_confirmed")
        school_confirmed = intent.getStringExtra("school_confirmed")
        school_domain = intent.getStringExtra("school_domain") as Nothing?

        if(school_domain != null) {
            schoolDomainTV.text = "@$school_domain"
        }

        if("Y" == has_branch_yn) {
            // studentIdLL.visibility = View.VISIBLE
        } else {
            studentIdLL.visibility = View.GONE
        }

        if (school_email_confirmed == "N") {
            schoolEmailConfirmedTV.visibility= View.GONE
            schoolEmailLL.visibility= View.VISIBLE
        } else {
            schoolEmailConfirmedTV.visibility= View.VISIBLE
            schoolEmailLL.visibility= View.GONE
        }

        sendSchoolConfirmBtn.setOnClickListener {
            sendSchoolConfirm()
        }

        selectStudentIdBtn.setOnClickListener {
            showPictureDialog()
        }

        agreenAndRequestLL.setOnClickListener {
            uploadStudentId()
        }

        finishLL.setOnClickListener {

            Utils.hideKeyboard(this)

            finish()
        }

    }

    private fun uploadStudentId() {
        val member_id = PrefUtils.getIntPreference(context, "member_id")

        if (mySchoolIdImg == null){
            Toast.makeText(this,"학생증을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("upload", ByteArrayInputStream(Utils.getByteArray(mySchoolIdImg)))

        MemberAction.uploadSchoolId(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        Toast.makeText(context, "학생증을 발송하였습니다.", Toast.LENGTH_LONG).show()
                    } else {

                        val message = response!!.getString("message")

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(errorResponse);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(errorResponse);

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

    private fun sendSchoolConfirm() {
        val member_id = PrefUtils.getIntPreference(context, "member_id")
        val school_email:String = Utils.getString(schoolEmailET)

        if (school_email == ""){
            Toast.makeText(this,"이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        sendSchoolConfirmBtn.isEnabled = false

        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("school_email", school_email)

        MemberAction.cirtySchoolEmail(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        Toast.makeText(context, "인증 메일을 발송하였습니다.", Toast.LENGTH_LONG).show()
                    } else {

                        sendSchoolConfirmBtn.isEnabled = true

                        val message = response!!.getString("message")

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(errorResponse);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(errorResponse);

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


    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("프로필 이미지 변경")
        val pictureDialogItems = arrayOf("갤러리에서 가져오기", "카메라로 사진찍기")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)

    }

    private var mySchoolIdImg: Bitmap? = null

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    mySchoolIdImg = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    studemtImgIV.setImageBitmap(mySchoolIdImg)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "바꾸기실패", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            mySchoolIdImg = data!!.extras!!.get("data") as Bitmap
            studemtImgIV.setImageBitmap(mySchoolIdImg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        Utils.hideKeyboard(this)

    }


}