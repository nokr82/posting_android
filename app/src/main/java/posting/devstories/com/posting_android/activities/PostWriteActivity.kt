package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

class PostWriteActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()
    private val REQUEST_CAMERA = 0
    private val CROP_FROM_CAMERA = 100
    var imageUri: Uri? = null
    var imageUriOutput: Uri? = null
    var absolutePath: String? = null
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
    var capture: Bitmap? = null

    lateinit var adapter: ArrayAdapter<String>
    lateinit var typeAdapter: ArrayAdapter<String>

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

        if (current_school != school_id) {
            bgRL.background = getDrawable(R.mipmap.write_bg2)
        } else {
            bgRL.background = getDrawable(R.mipmap.wtite_bg)
        }

        if (!posting_id.equals("")) {
            postingType = "M"
            image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, imgIV2, Utils.UILoptionsPosting)
            imgIV2.visibility = View.VISIBLE
        }

        if (member_type.equals("3")) {
            meeting2LL.visibility = View.GONE
        } else {
            dayLL.visibility = View.GONE
        }

        typeAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, mee)
        meetingSP2.adapter = typeAdapter

        adapter = ArrayAdapter<String>(this, R.layout.spinner_item, day)
        daySP.adapter = adapter

        adapter = ArrayAdapter<String>(this, R.layout.spinner_item, most)
        mostSP.adapter = adapter

        finishLL.setOnClickListener {
            finish()
        }

        textRL.setOnClickListener {

            getmee = meetingSP2.selectedItem.toString()
            getmost = mostSP.selectedItem.toString()
            getday = daySP.selectedItem.toString()

            if (member_type.equals("3")) {
                var intent = Intent(context, CouponTextActivity::class.java)
                startActivity(intent)
            } else {
                if (getmost.equals("수량")) {
                    Toast.makeText(context, "수량을 선택해주세요", Toast.LENGTH_SHORT).show()
//                } else if (getday.equals("기간")) {
//                    Toast.makeText(context, "기간을 선택해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    var intent = Intent(context, MyPostingWriteActivity::class.java)
                    intent.putExtra("getmee", getmee)
                    intent.putExtra("getmost", getmost)
//                    intent.putExtra("getday", getday)
                    intent.putExtra("postingType", "T")
                    startActivity(intent)
                }
            }
        }

        nextLL.setOnClickListener {

            getmee = meetingSP2.selectedItem.toString()
            getmost = mostSP.selectedItem.toString()
            getday = daySP.selectedItem.toString()


            if (getmost.equals("수량")) {
                Toast.makeText(context, "수량을 선택해주세요", Toast.LENGTH_SHORT).show()
//            } else if (getday.equals("기간")) {
//                Toast.makeText(context, "기간을 선택해주세요", Toast.LENGTH_SHORT).show()
            } else {
                var intent = Intent(context, MyPostingWriteActivity::class.java)
                intent.putExtra("image", image)
                intent.putExtra("current_school", current_school)
                intent.putExtra("school_id", school_id)
                intent.putExtra("imgid", imgid)
                intent.putExtra("postingType", postingType)
                intent.putExtra("absolutePath", absolutePath)
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

                val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

                try {
                    val photo = File.createTempFile(
                        System.currentTimeMillis().toString(), /* prefix */
                        ".jpg", /* suffix */
                        storageDir      /* directory */
                    )

                    absolutePath = photo.absolutePath
                    //imageUri = Uri.fromFile(photo);
                    imageUri = FileProvider.getUriForFile(context, packageName + ".provider", photo)
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

        checkCategory()

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
                            var meeting = Utils.getString(response, "study")

                            println("study : " + study)
                            println("classStr : " + classStr)
                            println("meeting : " + meeting)

                            if(meeting == "fail") {
                                mee.drop(4)
                            }

                            if(classStr == "fail") {
                                mee.drop(3)
                            }

                            if(study == "fail") {
                                mee.drop(2)
                            }

                            typeAdapter.notifyDataSetChanged()

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

            imageUri = Uri.fromFile(File(imgid))

            cropImage()

            // val imgWidth = Utils.getScreenWidth(context) / 4
            // imgIV2.setImageBitmap(Utils.getImage(context.contentResolver, imgid, 800))
            // imgIV2.setImageBitmap(Utils.getImage(context.contentResolver, imgid))
            capture = null
            imageUri = null

        }

        imageLoader.setListener(adapter)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CAMERA -> {
                imgid = null

                val realPathFromURI = imageUri!!.getPath()
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

                capture = Utils.getImage(context.contentResolver, imageUriOutput!!.path)
                // val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUriOutput)
                // capture = bitmap
                postingType = "P"
                imgIV2.setImageBitmap(capture)

            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }


    }

    fun cropImage() {
        context.grantUriPermission(
            "com.android.camera", imageUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(imageUri, "image/*")

        //you must setup two line below
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 200)
        intent.putExtra("outputY", 200)
        intent.putExtra("return-data", true)

        grantUriPermission(
            packageName, imageUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        //you must setup this

        if (intent.resolveActivity(packageManager) != null) {

            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            try {
                val photo = File.createTempFile(
                    System.currentTimeMillis().toString(), /* prefix */
                    ".jpg", /* suffix */
                    storageDir      /* directory */
                )

                absolutePath = photo.absolutePath
                //imageUri = Uri.fromFile(photo);
                imageUriOutput = FileProvider.getUriForFile(context, packageName + ".provider", photo)

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
