package posting.devstories.com.posting_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import posting.devstories.com.posting_android.Actions.VersionAction
import posting.devstories.com.posting_android.BuildConfig
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
    var push_yn  = ""
    var birth = ""
    private val GALLERY = 1
    private val CAMERA = 2
    private val VERSION_UPDATE = 3
    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        this.context = this
        progressDialog = ProgressDialog(context)
        loadInfo()

        versionCheck()

        schoolTV.setOnClickListener {
            val intent = Intent(this, SchoolagreeActivity::class.java)
            startActivity(intent)

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
        //알람스위치
        alramIV.setOnClickListener {

            if (push_yn.equals("Y")){
                push_yn = "N"
                edit_profile()
            }else{
                push_yn = "Y"
                edit_profile()
            }




        }

        questTV.setOnClickListener {
            sendEmail("1")
        }
        warringTV.setOnClickListener {
            val intent = Intent(this, NoticeActivity::class.java)
            startActivity(intent)
        }
        postingTV.setOnClickListener {

            val intent = Intent(this, ServiceActivity::class.java)
            startActivity(intent)
        }

        privacyTV.setOnClickListener {
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }


        finish3LL.setOnClickListener {
            finish()
        }


    }

   fun versionCheck() {

        val my_version = BuildConfig.VERSION_NAME
        val params = RequestParams()
        params.put("my_version", my_version)
        params.put("device", "A")
       versionTV.text = my_version

        VersionAction.versionCheck(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        upTV.visibility = View.GONE

                    } else if ("fail" == result) {

                        val version = response.getJSONObject("version")
                        var android =   Utils.getString(version, "android_version")
                         upTV.visibility = View.VISIBLE
                        upTV.setOnClickListener {
//                            val intent = Intent(Intent.ACTION_VIEW)
//                            intent.data = Uri.parse("market://details?id=$packageName")
//                            startActivity(intent)
//
//                            finish()
                        }

                    } else {
                        Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show()
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

    //이메일문의
    fun sendEmail(type: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:contact.wepostkorea@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("contact.wepostkorea@gmail.com"))
        if ("1" == type) {
            intent.putExtra(Intent.EXTRA_SUBJECT, "Posting 문의하기")
        } else if ("2" == type) {
            intent.putExtra(Intent.EXTRA_SUBJECT, "Groomee 제휴신청하기")
        }
        startActivity(intent)
    }

    fun edit_profile(){
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        if (gallery==null){
        }else{
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(gallery)))
        }
        if (thumbnail==null){
        }else{
            print(thumbnail)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(thumbnail)))
        }
        params.put("push_yn", push_yn)
        print("=============push_yn"+push_yn)
        MemberAction.edit_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")




                    if ("ok" == result) {

                        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()
                        loadInfo()

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
                        push_yn = Utils.getString(member,"push_yn")

                        if (push_yn.equals("Y")){
                            alramIV.setImageResource(R.mipmap.alrambar)
                        }else{
                            alramIV.setImageResource(R.mipmap.alramoff)
                        }


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
        else if (requestCode == VERSION_UPDATE)
        {

        }
    }



    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
