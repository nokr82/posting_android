package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Toast
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.ImageLoader
import java.util.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

open class WriteFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()
    private val REQUEST_CAMERA = 0
    var mee = arrayOf("Free","Info","Study","Class","Metting","Coupon")
    var  most =arrayOf("1","2","3","4","5","6","7","8","9","10")

    var imgid: String = ""

    var capture: Bitmap? = null

    val text = "1"


    lateinit var adpater: ArrayAdapter<String>
    lateinit var mainActivity:MainActivity

    lateinit var imgRL: RelativeLayout
    lateinit var imgIV: ImageView
    lateinit var meetingSP2: Spinner
    lateinit var mostSP2: Spinner
    lateinit var finishLL: LinearLayout
    lateinit var nextTX: TextView
    lateinit var listGV: GridView
    lateinit var cameraRL: RelativeLayout
    lateinit var textRL: RelativeLayout






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context
        if (null != ctx) {
            doSomethingWithContext(ctx)
        }

        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fra_write, container, false)
    }
    fun doSomethingWithContext(context: Context) {
        // TODO: Actually do something with the context
        this.ctx = context
        progressDialog = ProgressDialog(ctx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgIV = view.findViewById(R.id.imgIV)
        imgRL = view.findViewById(R.id.imgRL)
        meetingSP2 = view.findViewById(R.id.meetingSP2)
        mostSP2 = view.findViewById(R.id.mostSP2)
        finishLL = view.findViewById(R.id.finishLL)
        nextTX = view.findViewById(R.id.nextTX)
        listGV = view.findViewById(R.id.listGV)
        cameraRL = view.findViewById(R.id.cameraRL)
        textRL = view.findViewById(R.id.textRL)




    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var cursor: Cursor? = null
        val resolver = mainActivity.contentResolver


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

        adpater = ArrayAdapter<String>(mainActivity.context, android.R.layout.simple_spinner_item, mee)
        meetingSP2.adapter = adpater

        adpater = ArrayAdapter<String>(mainActivity.context, android.R.layout.simple_spinner_item, most)
        mostSP2.adapter = adpater




        finishLL.setOnClickListener {

        }


        textRL.setOnClickListener {
            var intent = Intent(context, MyPostingWriteActivity::class.java)
            intent.putExtra("text", text)
            startActivity(intent)

        }

        nextTX.setOnClickListener {

            var intent = Intent(context, MyPostingWriteActivity::class.java)
            intent.putExtra("imgid", imgid)
            intent.putExtra("capture", capture)

            startActivity(intent)
        }
        cameraRL.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(mainActivity.packageManager)!=null){

                startActivityForResult(intent,REQUEST_CAMERA)
            }


        }



        val imageLoader = ImageLoader(resolver)

        println(photoList)

        val adapter = ImageAdapter(mainActivity.context, photoList, imageLoader, selected)
        listGV.adapter = adapter
        listGV.setOnItemClickListener { parent, view, position, id ->

            val photo = photoList[position]

            imgRL.background = Drawable.createFromPath(photo.photoPath)
            //이미지가져오기
            imgid = photo.photoPath!!


        }



        imageLoader.setListener(adapter)


        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
            }


        }

        TedPermission.with(mainActivity.context)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_CAMERA ->{
                if(resultCode== Activity.RESULT_OK && data !=null){
                    capture = data.extras.get("data") as Bitmap
                    imgIV.setImageBitmap(capture)





                }
            }
            else -> {
                Toast.makeText(mainActivity.context,"Unrecognized request code",Toast.LENGTH_SHORT)
            }
        }



    }


    }


