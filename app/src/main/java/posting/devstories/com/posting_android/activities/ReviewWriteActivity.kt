package posting.devstories.com.posting_android.activities

import android.app.Activity
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
import com.nostra13.universalimageloader.core.ImageLoader
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_review_write.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ReviewWriteActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()


    val WRITE_RIVEW = 102;
    private val REQUEST_CAMERA = 0
    private val CROP_FROM_CAMERA = 100


    var imageUri: Uri? = null
    var imageUriOutput: Uri? = null
    var postingType = "G"
    var review_id = -1
    var member_type: String? = null
    var imgid: String? = null
    var contents: String? = null
    var company_member_id = -1
    var image_uri: String? = null
    var image: String? = null



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
        setContentView(R.layout.activity_review_write)

        val filter1 = IntentFilter("SET_VIEW")
        registerReceiver(setViewReceiver, filter1)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        intent = getIntent()
        company_member_id = intent.getIntExtra("company_member_id", -1)
        review_id = intent.getIntExtra("review_id", -1)
        member_type = intent.getStringExtra("member_type")
        contents = intent.getStringExtra("contents")
        image_uri = intent.getStringExtra("image_uri")





        if (review_id > 0)  {
            postingType = "M"
            image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, imgIV2, Utils.UILoptionsPosting)
            imgIV2.visibility = View.VISIBLE
        }



        finishLL.setOnClickListener {
            finish()
        }

        textRL.setOnClickListener {
                var intent = Intent(context, ReviewWriteContentsActivity::class.java)
                intent.putExtra("review_id", review_id)
                intent.putExtra("contents", contents)
            intent.putExtra("company_member_id", company_member_id)
                 intent.putExtra("absolutePath", "")
//                    intent.putExtra("getday", getday)
                intent.putExtra("postingType", "T")
            startActivityForResult(intent, WRITE_RIVEW)

//
            }


        nextTX.setOnClickListener{

                if(("G".equals(postingType) || "P".equals(postingType)) && imageUriOutput == null) {
                    Toast.makeText(context, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                var intent = Intent(context, ReviewWriteContentsActivity::class.java)

                if (imageUriOutput != null) {
                    intent.putExtra("imageUri",  imageUriOutput.toString())
                } else {
                    // intent.putExtra("imageUri",  imageUri.toString())
                }
                 intent.putExtra("review_id", review_id)
                intent.putExtra("postingType", postingType)
                 intent.putExtra("company_member_id",company_member_id)
                intent.putExtra("postingType",postingType)
                intent.putExtra("contents", contents)
                intent.putExtra("image_uri", image_uri)

            startActivityForResult(intent, WRITE_RIVEW)


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

                        println("packageName : $packageName")

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


//        checkCategory()

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

            println("gfds")

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
                if(resultCode == Activity.RESULT_CANCELED) {
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

                println("imageUriOutput : " + imageUriOutput)

                val capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUriOutput)
                imgIV2.setImageBitmap(capture)

            }
            WRITE_RIVEW -> {
                if(resultCode == Activity.RESULT_OK) {
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if(result != null) {
                    imageUriOutput = result.uri

                    val capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUriOutput)
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
