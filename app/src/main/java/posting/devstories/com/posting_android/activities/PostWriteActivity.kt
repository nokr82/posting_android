package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
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
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_postwrite.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.*
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
    // var absolutePath: String? = null
    var mee = arrayOf("자유", "정보", "스터디", "동아리", "미팅")
    var most = arrayOf("수량", "1", "3", "5", "10", "20", "무제한")
    var day = arrayOf("기간", "1일", "5일", "7일", "10일", "30일", "60일")
    var getmee: String? = null
    var getmost = ""
    var getday = ""
    var postingType = ""

    var current_school = -1
    var school_id = -1

    var member_type: String? = null
    var imgid: String? = null
    var posting_id: String? = null
    var contents: String? = null
    var image_uri: String? = null
    var image: String? = null
    // var capture: Bitmap? = null
    var tabType = -1
    var type = -1
    var count = -1

    lateinit var adapter: ArrayAdapter<String>
    lateinit var typeAdapter: ArrayAdapter<String>
    lateinit var countAdapter: ArrayAdapter<String>
    var setMee: ArrayList<String> = ArrayList<String>()

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

        intent = getIntent()

        member_type = intent.getStringExtra("member_type")
        current_school = intent.getIntExtra("current_school", -1)
        school_id = intent.getIntExtra("school_id", -1)
        posting_id = intent.getStringExtra("posting_id")
        contents = intent.getStringExtra("contents")
        image_uri = intent.getStringExtra("image_uri")
        tabType = intent.getIntExtra("tabType", -1)
        type = intent.getIntExtra("type", 1)
        count = intent.getIntExtra("count", 1)

        typeAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, mee)
        meetingSP2.adapter = typeAdapter
        typeAdapter.notifyDataSetChanged()

        adapter = ArrayAdapter<String>(this, R.layout.spinner_item, day)
        daySP.adapter = adapter

        countAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, most)
        mostSP.adapter = countAdapter

        if (current_school != school_id) {
            bgRL.background = getDrawable(R.mipmap.write_bg2)
        } else {
            bgRL.background = getDrawable(R.mipmap.wtite_bg)
        }

        if (posting_id != null && !posting_id.equals("") ) {
            postingType = "M"
            image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, imgIV2, Utils.UILoptionsPosting)
            imgIV2.visibility = View.VISIBLE

            var position = 1

            println("type : " + type)

            if(type == 1) {
                position = typeAdapter.getPosition("자유")
            } else if (type == 2) {
                position = typeAdapter.getPosition("정보")
            } else if (type == 3) {
                position = typeAdapter.getPosition("스터디")
            } else if (type == 4) {
                position = typeAdapter.getPosition("동아리")
            } else if (type == 5) {
                position = typeAdapter.getPosition("미팅")
            }

            println("position : $position")

            meetingSP2.setSelection(position)

            position = 1

            if(count == 1) {
                position = countAdapter.getPosition("1")
            } else if (count == 3) {
                position = countAdapter.getPosition("3")
            } else if (count == 5) {
                position = countAdapter.getPosition("5")
            } else if (count == 10) {
                position = countAdapter.getPosition("10")
            } else if (count == 20) {
                position = countAdapter.getPosition("20")
            } else if (count < 1) {
                position = countAdapter.getPosition("무제한")
            } else {
                position = countAdapter.getPosition("수량")
            }

            mostSP.setSelection(position)

        } else {
            if(tabType < 6) {
                meetingSP2.setSelection(tabType - 1)
            }
        }

        if (member_type.equals("3")) {
            meeting2RL.visibility = View.GONE
        } else {
            dayRL.visibility = View.GONE
        }

        finishLL.setOnClickListener {
            finish()
        }

        textRL.setOnClickListener {

            if(member_type == "3") {
                getday = daySP.selectedItem.toString()
            } else {
                getmee = meetingSP2.selectedItem.toString()
            }

            getmost = mostSP.selectedItem.toString()

            if (member_type.equals("3")) {
                var intent = Intent(context, CouponTextActivity::class.java)
                startActivity(intent)
            } else {
//                if (getmost.equals("수량")) {
//                    Toast.makeText(context, "수량을 선택해주세요", Toast.LENGTH_SHORT).show()
//                } else if (getday.equals("기간")) {
//                    Toast.makeText(context, "기간을 선택해주세요", Toast.LENGTH_SHORT).show()
//                } else {
                    var intent = Intent(context, MyPostingWriteActivity::class.java)
                    intent.putExtra("getmee", getmee)
                    intent.putExtra("getmost", getmost)
                    intent.putExtra("posting_id", posting_id)
                    intent.putExtra("contents", contents)
//                    intent.putExtra("getday", getday)
                    intent.putExtra("postingType", "T")
                    startActivity(intent)
//                }
            }
        }

        nextLL.setOnClickListener {

            if(member_type == "3") {
                getday = daySP.selectedItem.toString()
            } else {
                getmee = meetingSP2.selectedItem.toString()
            }

            getmost = mostSP.selectedItem.toString()

            if (getmost.equals("수량")) {
                Toast.makeText(context, "수량을 선택해주세요", Toast.LENGTH_SHORT).show()
//            } else if (getday.equals("기간")) {
//                Toast.makeText(context, "기간을 선택해주세요", Toast.LENGTH_SHORT).show()
            } else {
                var intent = Intent(context, MyPostingWriteActivity::class.java)

                if (imageUriOutput != null) {
                    intent.putExtra("imageUri",  imageUriOutput.toString())
                } else {
                    // intent.putExtra("imageUri",  imageUri.toString())
                }

                intent.putExtra("current_school", current_school)
                intent.putExtra("school_id", school_id)
                // intent.putExtra("imgid", imgid)
                intent.putExtra("postingType", postingType)
                // intent.putExtra("absolutePath", absolutePath)
                intent.putExtra("contents", contents)
                intent.putExtra("posting_id", posting_id)
                intent.putExtra("image_uri", image_uri)
                intent.putExtra("getmee", getmee)
                intent.putExtra("getmost", getmost)
                intent.putExtra("getday", getday)

                startActivity(intent)
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

                    val resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName;
                        context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
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

        if(tabType > 0) {
            meetingSP2.setSelection(tabType - 1)
        }

//        checkCategory()

    }

    fun checkCategory() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"));
        params.put("member_type", member_type);

        PostingAction.today_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        if(member_type == "2") {

                            var study = Utils.getString(response, "study")
                            var classStr = Utils.getString(response, "class")
                            var meeting = Utils.getString(response, "meeting")

                            setMee = ArrayList()
                            setMee.add("자유")
                            setMee.add("정보")

                            if(study == "ok") {
                                setMee.add("스터디")
                            }

                            if(classStr == "ok") {
                                setMee.add("동아리")
                            }

                            if(meeting == "ok") {
                                setMee.add("미팅")
                            }

                            typeAdapter = ArrayAdapter<String>(context, R.layout.spinner_item, setMee)
                            meetingSP2.adapter = typeAdapter
                            typeAdapter.notifyDataSetChanged()

                            if(tabType < 3) {
                                meetingSP2.setSelection(tabType - 1)
                            } else if(tabType == 3 && study == "ok") {
                                meetingSP2.setSelection(2)
                            } else if(tabType == 4) {
                                if(study == "ok" && classStr == "ok") {
                                    meetingSP2.setSelection(3)
                                } else if(study != "ok" && classStr == "ok") {
                                    meetingSP2.setSelection(2)
                                }
                            } else if(tabType == 5) {
                                if(study == "ok" && classStr == "ok" && meeting == "ok") {
                                    meetingSP2.setSelection(4)
                                } else if(study != "ok" && classStr == "ok" && meeting == "ok") {
                                    meetingSP2.setSelection(3)
                                } else if(study != "ok" && classStr != "ok" && meeting == "ok") {
                                    meetingSP2.setSelection(2)
                                }
                            }
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

             // val imgWidth = Utils.getScreenWidth(context) / 4
             // imgIV2.setImageBitmap(Utils.getImage(context.contentResolver, imgid, imgWidth))
             // imgIV2.setImageBitmap(Utils.getImage(context.contentResolver, imgid))
            // capture = null
            // imageUri = null

        }

        imageLoader.setListener(adapter)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CAMERA -> {
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

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            CROP_FROM_CAMERA -> {
                val capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUriOutput)
                imgIV2.setImageBitmap(capture)

                postingType = "P"
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }


    }

    private fun cropImage() {
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

                val resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, imageUriOutput, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriOutput)
                startActivityForResult(intent, CROP_FROM_CAMERA)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        // intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        // startActivityForResult(intent, CROP_FROM_CAMERA)


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
