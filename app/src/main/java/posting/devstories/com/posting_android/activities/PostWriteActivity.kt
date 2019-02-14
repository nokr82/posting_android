package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_postwrite.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PostWriteActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()
    private val REQUEST_CAMERA = 0
    private val CROP_FROM_CAMERA = 100
    var imageUri: Uri? = null
    var imageUriOutput: Uri? = null
    var postingType = "G"

    var current_school = -1
    var school_id = -1

    var member_type: String? = null
    var imgid: String? = null
    var posting_id: String? = null
    var contents: String? = null
    var image_uri: String? = null
    var image: String? = null
    var tabType = -1
    var type = -1
    var count = -1
    var chatting_yn = "N"

    var member_id = -1

    lateinit var capture: Bitmap

    lateinit var adapter: ArrayAdapter<String>

    //mypostwrite에서 브로드캐스트로 인텐트를 받는다
    internal var setViewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postwrite)

        val filter1 = IntentFilter("SET_VIEW")
        registerReceiver(setViewReceiver, filter1)

        this.context = this
        progressDialog = ProgressDialog(context)

        member_id = PrefUtils.getIntPreference(context, "member_id")

        intent = getIntent()

        member_type = intent.getStringExtra("member_type")
        current_school = intent.getIntExtra("current_school", -1)
        school_id = intent.getIntExtra("school_id", -1)
        posting_id = intent.getStringExtra("posting_id")
        contents = intent.getStringExtra("contents")
        image_uri = intent.getStringExtra("image_uri")
        tabType = intent.getIntExtra("tabType", -1)
        type = intent.getIntExtra("type", -1)
        count = intent.getIntExtra("count", -1)

        if (count > -1) {
            countET.setText(count.toString())
        }

        if (posting_id != null && !posting_id.equals("")) {
            postingType = "M"
            image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, imgIV2, Utils.UILoptionsPosting)
            imgIV2.visibility = View.VISIBLE

            chatting_yn = intent.getStringExtra("chatting_yn")
        }

        if ("Y" == chatting_yn) {
            chattingCB.isChecked = true
        }

        finishLL.setOnClickListener {
            finish()
        }

        nextLL.setOnClickListener {

            count = Utils.getInt(countET)

            if (count < 1 || count > 10) {
                countdlgView()
//                Toast.makeText(context, "1개 ~ 10개 한정\n수량을 기입해주세요", Toast.LENGTH_SHORT).show()
            } else {

                if (chattingCB.isChecked) {
                    chatting_yn = "Y"
                } else {
                    chatting_yn = "N"
                }

                if (("G".equals(postingType) || "P".equals(postingType)) && imageUriOutput == null) {
                    Toast.makeText(context, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if ("" != posting_id && null != posting_id) {
                    edit_posting()
                } else {
                    write()
                }

            }

        }

        cameraRL.setOnClickListener {
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

                        context.grantUriPermission(
                            packageName,
                            imageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivityForResult(intent, REQUEST_CAMERA)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }


        }

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                loadPhoto()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
            }

        }

        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()

        loadPhoto()

    }

    fun countdlgView() {

        var intent = Intent(context, DlgCommonActivity::class.java)
        intent.putExtra("contents", "1개 ~ 10개 한정\n수량을 기입해주세요.")
        startActivity(intent)

    }



    fun loadPhoto() {
        var cursor: Cursor? = null
        val resolver = contentResolver

        try {
            val proj = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val idx = IntArray(proj.size)

            cursor = MediaStore.Images.Media.query(
                resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
            )
            if (cursor != null && cursor.moveToFirst()) {
                idx[0] = cursor.getColumnIndex(proj[0])
                idx[1] = cursor.getColumnIndex(proj[1])
                idx[2] = cursor.getColumnIndex(proj[2])
                idx[3] = cursor.getColumnIndex(proj[3])
                idx[4] = cursor.getColumnIndex(proj[4])

                var photo = ImageAdapter.PhotoData()

                do {
                    val photoID = cursor.getInt(idx[0])
                    val photoPath = cursor.getString(idx[1])
                    val displayName = cursor.getString(idx[2])
                    val orientation = cursor.getInt(idx[3])
                    val bucketDisplayName = cursor.getString(idx[4])
                    if (displayName != null) {
                        photo = ImageAdapter.PhotoData()
                        photo.photoID = photoID
                        photo.photoPath = photoPath
                        photo.orientation = orientation
                        photo.bucketPhotoName = bucketDisplayName
                        photoList.add(photo)
                    }

                } while (cursor.moveToNext())

                cursor.close()
            }
        } catch (ex: Exception) {
            // Log the exception's message or whatever you like
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close()
                }
            } catch (ex: Exception) {
            }

        }


        val imageLoader = ImageLoader(resolver)

        val adapter = ImageAdapter(context, photoList, imageLoader, selected)
        listGV.adapter = adapter
        listGV.setOnItemClickListener { parent, view, position, id ->

            postingType = "G"
            image = ""

            val photo = photoList[position]

            //이미지가져오기
            imgid = photo.photoPath!!

            // imageUri = Uri.fromFile(File(imgid))

            if (intent.resolveActivity(packageManager) != null) {
                try {
                    imageUri = FileProvider.getUriForFile(context, packageName + ".provider", File(imgid))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            cropImage()

        }

        imageLoader.setListener(adapter)


    }

    fun write() {

        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", Utils.getInt(countET))
        params.put("current_school_id", PrefUtils.getIntPreference(context, "current_school_id"))
        params.put("chatting_yn", chatting_yn)

        if (capture != null) {
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(capture)))
        }

        PostingAction.write(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Utils.hideKeyboard(context)

                        try {
                            contentResolver.delete(imageUri, null, null);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        //브로드캐스트로 날려주기
                        val intent = Intent()
                        intent.action = "WRITE_POST"
                        sendBroadcast(intent)

                        Toast.makeText(context, "글작성이 완료되었습니다", Toast.LENGTH_SHORT).show()

                        finish()

                    } else if ("over" == result) {
                        Toast.makeText(context, "하루 제한량만큼 작성하셨습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "등록중 장애가 발생하였습니다.", Toast.LENGTH_SHORT).show()
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

    fun edit_posting() {

        val params = RequestParams()
        params.put("posting_id", posting_id)
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", count)
        params.put("chatting_yn", chatting_yn)

        if (capture != null) {
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(capture)))
        }

        PostingAction.edit_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        Utils.hideKeyboard(context)
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(context, "수정이 완료되었습니다", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, "작성실패", Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    return
                }

                imgid = null

                val realPathFromURI = imageUri!!.path
                context.sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://$realPathFromURI")
                    )
                )
                try {

                    cropImage()

                    postingType = "P"

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            CROP_FROM_CAMERA -> {
                capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUriOutput)
                imgIV2.setImageBitmap(capture)

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (result != null) {
                    imageUriOutput = result.uri

                    capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUriOutput)
                    imgIV2.setImageBitmap(capture)
                }

            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }


    }

    private fun cropImage() {

        val intent = CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .getIntent(this);

        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    private fun cropImageOld() {

        context.grantUriPermission(
            "com.android.camera", imageUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(imageUri, "image/*")

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 500)
        intent.putExtra("outputY", 500)
        intent.putExtra("return-data", true)

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
                imageUriOutput = FileProvider.getUriForFile(context, packageName + ".provider", photo)

                val resInfoList =
                    context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(
                        packageName,
                        imageUriOutput,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriOutput)
                startActivityForResult(intent, CROP_FROM_CAMERA)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }


        try {
            if (setViewReceiver != null) {
                context.unregisterReceiver(setViewReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }
    }

}
