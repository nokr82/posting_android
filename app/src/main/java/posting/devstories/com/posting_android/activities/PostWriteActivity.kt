package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.GridView
import kotlinx.android.synthetic.main.activity_posttextwrite.*
import kotlinx.android.synthetic.main.activity_postwrite.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.ImageLoader
import posting.devstories.com.posting_android.base.RootActivity
import java.util.*
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class PostWriteActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()

    var mee = arrayOf("Metting")
    var most =  arrayOf("수량")



    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postwrite)

        var cursor: Cursor? = null
        val resolver = contentResolver


        this.context = this
        progressDialog = ProgressDialog(context)


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



        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mee)
        meetingSP2.adapter = adpater



        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,most)
        mostSP2.adapter = adpater







        nextTX.setOnClickListener {
            var intent = Intent(context, MyPostingWriteActivity::class.java)
            startActivity(intent)
        }


        val imageLoader = ImageLoader(resolver)

        println(photoList)

        val adapter = ImageAdapter(this, photoList, imageLoader, selected)
        listGV.adapter = adapter



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
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();


    }

    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
