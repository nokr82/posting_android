package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.ImageLoader
import posting.devstories.com.posting_android.base.RootActivity
import java.util.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import kotlinx.android.synthetic.main.activity_review_write.*
import posting.devstories.com.posting_android.base.Utils


class ReviewWriteActivity : RootActivity() {

    val WRITE_RIVEW = 102;

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()
    private val REQUEST_CAMERA = 0

    var imgid: String = ""
    var posting_id = ""
    var contents = ""
    var image_uri = ""
    var image = ""
    var text = ""
    var capture: Bitmap? = null
    var company_member_id = -1

    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_write)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        company_member_id = intent.getIntExtra("company_member_id", -1)

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

        finishLL.setOnClickListener {
            finish()
        }

        textRL.setOnClickListener {
            var intent = Intent(context, ReviewWriteContentsActivity::class.java)
//            intent.putExtra("review_id", review_id)
            intent.putExtra("text", text)
            intent.putExtra("company_member_id", company_member_id)
            startActivityForResult(intent, WRITE_RIVEW)
        }

        nextTX.setOnClickListener {

            var intent = Intent(context, ReviewWriteContentsActivity::class.java)
            intent.putExtra("image", image)
            intent.putExtra("imgid", imgid)
            intent.putExtra("capture", capture)
            intent.putExtra("image_uri",image_uri)
            intent.putExtra("company_member_id",company_member_id)
            startActivityForResult(intent, WRITE_RIVEW)
        }

        cameraRL.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (intent.resolveActivity(packageManager)!=null){

                            startActivityForResult(intent,REQUEST_CAMERA)
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

                    startActivityForResult(intent,REQUEST_CAMERA)
                }

            }

        }

        val imageLoader = ImageLoader(resolver)

        val adapter = ImageAdapter(context, photoList, imageLoader, selected)
        listGV.adapter = adapter
        listGV.setOnItemClickListener { parent, view, position, id ->
            image = ""

            val photo = photoList[position]

            imgid = photo.photoPath!!
            imgIV2.setImageBitmap(Utils.getImage(context.contentResolver, imgid))

        }

        imageLoader.setListener(adapter)

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
            }

        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK) {
            when(requestCode){
                REQUEST_CAMERA ->{
                    if(data !=null){
                        imgid = ""
                        image = ""

                        capture = data.extras.get("data") as Bitmap
                        imgIV2.setImageBitmap(capture)

                    }
                }

                WRITE_RIVEW -> {
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                else -> {
                    Toast.makeText(this,"Unrecognized request code",Toast.LENGTH_SHORT)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
