package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.view.View
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
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException

class MyPageActivity : FragmentActivity() {

    var thumbnail: Bitmap? = null
    // var gallery:Bitmap? = null
    var nick = ""
    var name = ""
    var push_yn = ""
    var birth = ""
    private val GALLERY = 1
    private val CAMERA = 2
    private val VERSION_UPDATE = 3
    private  val EDIT_PROFILE = 4
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false

    private var has_branch_yn = "N"
    private var school_domain = ""

    val SECESSION = 301
    val LOGOUT = 401

    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        this.context = this
        progressDialog = ProgressDialog(context)

        loadInfo()

        versionCheck()

        schoolTV.setOnClickListener {
            val intent = Intent(this, SchoolagreeActivity::class.java)
            intent.putExtra("has_branch_yn", has_branch_yn)
            intent.putExtra("school_email_confirmed", school_email_confirmed)
            intent.putExtra("school_confirmed", school_confirmed)
            intent.putExtra("school_domain", school_domain)
            startActivity(intent)

        }


        profileTV.setOnClickListener {
            showPictureDialog()
        }
        nickTV.setOnClickListener {
            val intent = Intent(this, nickchangeActivity::class.java)
            intent.putExtra("nick", nick)
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

            if (push_yn.equals("Y")) {
                push_yn = "N"
                edit_profile()
            } else {
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
                        var android = Utils.getString(version, "android_version")
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

    fun dlgView() {

        var intent = Intent(context, DlgYesOrNoCommonActivity::class.java)
        intent.putExtra("contents", "정말 탈퇴하시겠습니까?")
        startActivityForResult(intent, SECESSION)

//        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
//        builder.setTitle("회원탈퇴")
//        builder.setMessage("정말 탈퇴하시겠습니까?")
//
//        builder.setPositiveButton("확인") { _, _ ->
//            PrefUtils.setPreference(context, "autoLogin", autoLogin)
//            redout()
//        }
//        builder.setNegativeButton("취소") { _, _ ->
//
//
//
//        }
//
//        builder.show()
    }

    fun joinoutdlgView() {

        var intent = Intent(context, DlgYesOrNoCommonActivity::class.java)
        intent.putExtra("contents", "로그아웃 하시겠습니까?")
        startActivityForResult(intent, LOGOUT)


//        var mPopupDlg: DialogInterface? = null
//
//        val builder = AlertDialog.Builder(this)
//        val dialogView = layoutInflater.inflate(R.layout.coupon_dlg, null)
//        val couponnoTX = dialogView.findViewById<TextView>(R.id.couponnoTX)
//        val couponyTX = dialogView.findViewById<TextView>(R.id.couponyTX)
//        val couponTV :TextView = dialogView.findViewById<TextView>(R.id.couponTV)
//        couponTV.text = "로그아웃 하시겠습니까?"
//        couponnoTX.text = "NO"
//        couponyTX.text = "YES"
//
//        couponnoTX.setOnClickListener {
//            mPopupDlg!!.cancel()
//        }
//
//        couponyTX.setOnClickListener {
//
//            PrefUtils.setPreference(context, "autoLogin", autoLogin)
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            mPopupDlg!!.cancel()
//
//        }
//
//        mPopupDlg =  builder.setView(dialogView).show()

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
        intent.putExtra(Intent.EXTRA_SUBJECT, "Posting 문의하기")
        startActivity(intent)
    }

    fun edit_profile() {

        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
        params.put("push_yn", push_yn)

        if (thumbnail != null) {
            val byteArrayInputStream = ByteArrayInputStream(Utils.getByteArray(thumbnail))
            params.put("upload", byteArrayInputStream)
        }

        MemberAction.edit_info(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Toast.makeText(context, "변경되었습니다.", Toast.LENGTH_SHORT).show()

//                        var member = response.getJSONObject("member");

                        var intent = Intent()
//                        intent.putExtra("profile_uri", Utils.getString(member, "image_uri"))
                        intent.action = "EDIT_PROFILE"
                        sendBroadcast(intent)

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

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONArray?
            ) {
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

    private var school_email_confirmed = "N"
    private var school_confirmed = "N"
    private var school_email = ""

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

//                    print("result : $response")

                    if ("ok" == result) {

                        var member = response.getJSONObject("member")
                        nick = Utils.getString(member, "nick_name")
                        name = Utils.getString(member, "name")
                        birth = Utils.getString(member, "birth")
                        push_yn = Utils.getString(member, "push_yn")

                        if (push_yn.equals("Y")) {
                            alramIV.setImageResource(R.mipmap.alrambar)
                        } else {
                            alramIV.setImageResource(R.mipmap.alramoff)
                        }

                        var image_uri = Utils.getString(member, "image_uri")
                        var image = Config.url + image_uri
                        ImageLoader.getInstance().displayImage(image, myproIV, Utils.UILoptionsUserProfile)

                        nameTV.text = nick

                        // school
                        val school = response.getJSONObject("school")

                        infonameTV.text = name + "/" + Utils.getString(school, "name")

                        has_branch_yn = Utils.getString(school, "has_branch_yn")
                        school_domain = Utils.getString(school, "domain")

                        school_email = Utils.getString(member, "school_email")
                        school_email_confirmed = Utils.getString(member, "school_email_confirmed")
                        school_confirmed = Utils.getString(member, "school_confirmed")

//                        if(school_confirmed == "N" && Utils.getString(member, "member_type") == "2") {
//                            schoolTV.visibility = View.VISIBLE
//                        } else {
//                            schoolTV.visibility = View.GONE
//                        }

                        val writeStudyCnt = Utils.getInt(response, "writeStudyCnt")
                        writeStudyTV.text = writeStudyCnt.toString()

                        val writeClassCnt = Utils.getInt(response, "writeClassCnt")
                        writeClassTV.text = writeClassCnt.toString()

                        val writeMeetingCnt = Utils.getInt(response, "writeMeetingCnt")
                        writeMeetingTV.text = writeMeetingCnt.toString()

                        val saveStudyCnt = Utils.getInt(response, "saveStudyCnt")
                        saveStudyTV.text = saveStudyCnt.toString()

                        val saveClassCnt = Utils.getInt(response, "saveClassCnt")
                        saveClassTV.text = saveClassCnt.toString()

                        val saveMeetingCnt = Utils.getInt(response, "saveMeetingCnt")
                        saveMeetingTV.text = saveMeetingCnt.toString()

                        val today = Utils.getString(response, "today")
                        todayTV.text = today


                        // 닉네임 변경
                        if (Utils.getString(member, "member_type") == "3") {
                            nickTV.visibility = View.GONE
                        }

                    } else {
//                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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
//        val pictureDialog = AlertDialog.Builder(this)
//        pictureDialog.setTitle("프로필 이미지 변경")
//        val pictureDialogItems = arrayOf("갤러리에서 가져오기", "카메라로 사진찍기")
//        pictureDialog.setItems(
//            pictureDialogItems
//        ) { dialog, which ->
//            when (which) {
//                0 -> choosePhotoFromGallary()
//                1 -> takePhotoFromCamera()
//            }
//        }
//        pictureDialog.show()

        var intent = Intent(context, DlgEditProfileActivity::class.java)
        startActivityForResult(intent, EDIT_PROFILE)

    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)

    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {

            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            try {
                val photo = File.createTempFile(
                    System.currentTimeMillis().toString(), /* prefix */
                    ".jpg", /* suffix */
                    storageDir      /* directory */
                )

                // absolutePath = photo.absolutePath
                //imageUri = Uri.fromFile(photo);
                imageUri = FileProvider.getUriForFile(context, packageName + ".provider", photo)

                val resInfoList =
                    context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName;

                    println("packageName : $packageName")

                    context.grantUriPermission(
                        packageName,
                        imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, CAMERA)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }


        // startActivityForResult(intent, CAMERA)

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    thumbnail = Utils.rotate(contentResolver, thumbnail, contentURI)
                    edit_profile()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "바꾸기실패", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            thumbnail = Utils.rotate(contentResolver, thumbnail, imageUri)
            edit_profile()
        } else if (requestCode == VERSION_UPDATE) {

        } else if (requestCode == SECESSION) {
            if (resultCode == Activity.RESULT_OK) {
                redout()
            }
        } else if (requestCode == LOGOUT) {
            if (resultCode == Activity.RESULT_OK) {
                PrefUtils.setPreference(context, "autoLogin", autoLogin)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        } else if (requestCode == EDIT_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                loadInfo()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
