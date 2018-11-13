package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_review_write.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.ImageLoader
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.io.File
import java.io.IOException
import java.util.*


class ReviewWriteActivity : RootActivity() {

    val WRITE_RIVEW = 102;

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()

    private val REQUEST_CAMERA = 0
    private val CROP_FROM_CAMERA = 100

    var imgid: String = ""
    var contents = ""
    var image_uri = ""
    var image = ""
    var text = ""
    var postingType = ""
    var company_member_id = -1
    var review_id = -1

    var imageUri: Uri? = null
    var absolutePath: String? = ""

    var capture: Bitmap? = null

    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_write)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        company_member_id = intent.getIntExtra("company_member_id", -1)
        review_id = intent.getIntExtra("review_id", -1)

        if (review_id > 0) {

            postingType = "M"

            image_uri = intent.getStringExtra("image_uri")
            contents = intent.getStringExtra("contents")

            image = Config.url + image_uri
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(image, imgIV2, Utils.UILoptionsPosting)
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
            intent.putExtra("postingType", "T")
            intent.putExtra("absolutePath", "")
            startActivityForResult(intent, WRITE_RIVEW)
        }

        nextTX.setOnClickListener {
            var intent = Intent(context, ReviewWriteContentsActivity::class.java)
            intent.putExtra("review_id", review_id)
            intent.putExtra("image", image)
            intent.putExtra("imgid", imgid)
            intent.putExtra("contents", contents)
            // intent.putExtra("capture", capture)
            intent.putExtra("image_uri",image_uri)
            intent.putExtra("company_member_id",company_member_id)
            intent.putExtra("postingType",postingType)
            intent.putExtra("absolutePath", absolutePath)
            startActivityForResult(intent, WRITE_RIVEW)
        }

        cameraRL.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (intent.resolveActivity(packageManager)!=null){

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

                    override fun onPermissionDenied(deniedPermissions: List<String>) {
                    }

                }

                TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                    .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();

            } else {

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager)!=null){


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
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        if(review_id > 1) {

        }

        loadPhoto()

    }

    fun loadPhoto(){

        var cursor: Cursor? = null
        val resolver = contentResolver

        try {
            val proj = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idx = IntArray(proj.size)

            cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC")
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
            image = ""
            postingType="G"

            val photo = photoList[position]

            imgid = photo.photoPath!!
            imgIV2.setImageBitmap(Utils.getImage(context.contentResolver, imgid))

        }

        imageLoader.setListener(adapter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

            when(requestCode){
                REQUEST_CAMERA ->{
                    imgid = ""

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
//                    capture = Utils.getImage(context.contentResolver, absolutePath)
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    capture = bitmap
                    postingType = "P"
                    imgIV2.setImageBitmap(capture)

                }

                WRITE_RIVEW -> {
                    if(resultCode== Activity.RESULT_OK) {
                        var intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }

                else -> {
                    Toast.makeText(this,"Unrecognized request code",Toast.LENGTH_SHORT)
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CROP_FROM_CAMERA)

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
