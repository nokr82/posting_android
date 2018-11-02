package posting.devstories.com.posting_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_mypage.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils
import java.io.*

class MyPageActivity : FragmentActivity() {


    var thumbnail:Bitmap? = null
    var gallery:Bitmap? = null
    var nick = ""
    var name  = ""
    var birth = ""
    private val GALLERY = 1
    private val CAMERA = 2
    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        this.context = this
        progressDialog = ProgressDialog(context)
        loadInfo()



        schoolTV.setOnClickListener {

        }


        profileTV.setOnClickListener {
            showPictureDialog()

        }
        nickTV.setOnClickListener {
            val intent = Intent(this, nickchangeActivity::class.java)
            intent.putExtra("nick",nick)
            startActivity(intent)
        }
        outTV.setOnClickListener {
            dlgView()
            }


        joinoutTV.setOnClickListener {
            joinoutdlgView()
        }

        myproIV.setOnClickListener {
            showPictureDialog()
        }

        alramTV.setOnClickListener {
            alramSW.isChecked

        }

        appTV.setOnClickListener {

        }
        questTV.setOnClickListener {

        }
        warringTV.setOnClickListener {
            val intent = Intent(this, NoticeActivity::class.java)
            startActivity(intent)
        }
        postingTV.setOnClickListener {

        }







        finish3LL.setOnClickListener {
            finish()
        }


    }
    fun dlgView(){
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
        builder.setTitle("회원탈퇴")
        builder.setMessage("정말 탈퇴하시겠습니까?")

        builder.setPositiveButton("확인") { _, _ ->
            PrefUtils.setPreference(context, "autoLogin", autoLogin)
            redout()
        }
        builder.setNegativeButton("취소") { _, _ ->



        }

        builder.show()
    }

    fun joinoutdlgView(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.coupon_dlg, null)
        val couponnoTX = dialogView.findViewById<TextView>(R.id.couponnoTX)
        val couponyTX = dialogView.findViewById<TextView>(R.id.couponyTX)
        val couponTV :TextView = dialogView.findViewById<TextView>(R.id.couponTV)
        couponTV.text = "로그아웃 하시겠습니까?"
        couponnoTX.text = "NO"
        couponyTX.text = "YES"

        couponnoTX.setOnClickListener {
            mPopupDlg!!.cancel()
        }

        couponyTX.setOnClickListener {

            PrefUtils.setPreference(context, "autoLogin", autoLogin)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            mPopupDlg!!.cancel()

        }

        mPopupDlg =  builder.setView(dialogView).show()

    }
    //회원탈퇴
    fun redout() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

        MemberAction.secession(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        Toast.makeText(context, "탈퇴가 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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


    fun edit_profile(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        if (gallery==null){
        }else{
            print("===캡쳐"+gallery)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(gallery)))
        }
        if (thumbnail==null){
        }else{
            print(thumbnail)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(thumbnail)))
        }

        MemberAction.edit_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")




                    if ("ok" == result) {

                        Toast.makeText(context, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show()


                    } else {

                        Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
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
    //회원정보
    fun loadInfo() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))

        MemberAction.my_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var member = response.getJSONObject("member")
                        nick =  Utils.getString(member, "nick_name")
                        name = Utils.getString(member,"name")
                        birth =  Utils.getString(member,"birth")
                        var image_uri = Utils.getString(member, "image_uri")
                        if (!image_uri.isEmpty() && image_uri != "") {
                            var image = Config.url + image_uri
                            ImageLoader.getInstance().displayImage(image,myproIV, Utils.UILoptionsPosting)
                        }
                        infonameTV.text = name+"/"+birth
                        nameTV.text =nick

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)

    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    gallery = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    edit_profile()
                    myproIV!!.setImageBitmap(gallery)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "바꾸기실패", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            thumbnail = data!!.extras!!.get("data") as Bitmap
            edit_profile()
            myproIV!!.setImageBitmap(thumbnail)
        }
    }



    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
