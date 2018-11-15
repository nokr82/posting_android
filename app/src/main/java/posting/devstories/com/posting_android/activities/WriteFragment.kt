package posting.devstories.com.posting_android.activities


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_postwrite.*
import kotlinx.android.synthetic.main.fra_write.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.ImageAdapter
import posting.devstories.com.posting_android.base.ImageLoader
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils
import java.util.*

open class WriteFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null

    private val photoList = ArrayList<ImageAdapter.PhotoData>()
    private val selected = LinkedList<String>()
    private val REQUEST_CAMERA = 0
    var mee = arrayOf("자유", "정보", "스터디", "동아리", "미팅")
    var most = arrayOf("수량", "1", "3", "5", "10", "20", "∞")

    var day = arrayOf("기간", "1일", "5일", "7일", "10일", "30일", "60일")

    var member_type = ""

    var imgid: String = ""

    var capture: Bitmap? = null

    val text = "1"
    var startd = ""
    var last = ""

    var mount: Int? = null

    lateinit var adpater: ArrayAdapter<String>
    lateinit var mainActivity: MainActivity

    lateinit var imgRL: RelativeLayout
    lateinit var imgIV: ImageView
    lateinit var meetingSP: Spinner
    lateinit var mostSP: Spinner
    lateinit var finishLL: LinearLayout
    lateinit var nextTX: TextView
    lateinit var listGV: GridView
    lateinit var cameraRL: RelativeLayout
    lateinit var textRL: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        progressDialog = ProgressDialog(myContext)

        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fra_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgIV = view.findViewById(R.id.imgIV)
        imgRL = view.findViewById(R.id.imgRL)
        meetingSP = view.findViewById(R.id.meetingSP2)
        mostSP = view.findViewById(R.id.mostSP)
        nextTX = view.findViewById(R.id.nextTX)
        listGV = view.findViewById(R.id.listGV)
        cameraRL = view.findViewById(R.id.cameraRL)
        textRL = view.findViewById(R.id.textRL)


    }


    private lateinit var adapter: ImageAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressDialog = ProgressDialog(myContext)

        member_type = PrefUtils.getStringPreference(myContext, "member_type")

        if (member_type.equals("3")) {

            meetingLL.visibility = View.GONE

            adpater = ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, most)
            day2SP.adapter = adpater

            adpater = ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, day)
            mostSP.adapter = adpater

            meetingSP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    Log.d("yjs", "position : " + position.toString())
                    mount = position
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }


            adpater = ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, day)
            mostSP.adapter = adpater


            var cal = Calendar.getInstance()

//          val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//              cal.set(Calendar.YEAR, year)
//              cal.set(Calendar.MONTH, monthOfYear)
//              cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//              val myFormat = "yy.MM.dd" // mention the format you need
//              val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
//              dateTX.text = sdf.format(cal.time)+"~"
//
//          }
//
//          val dateSetListener2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//              cal.set(Calendar.YEAR, year)
//              cal.set(Calendar.MONTH, monthOfYear)
//              cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//              val myFormat = "yy.MM.dd" // mention the format you need
//              val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
//              limitTX.text = sdf.format(cal.time)
//
//          }
//
//          dateLL.setOnClickListener {
//              DatePickerDialog(myContext, dateSetListener2,
//              cal.get(Calendar.YEAR),
//              cal.get(Calendar.MONTH),
//              cal.get(Calendar.DAY_OF_MONTH)).show()
//              DatePickerDialog(myContext, dateSetListener,
//                      cal.get(Calendar.YEAR),
//                      cal.get(Calendar.MONTH),
//                      cal.get(Calendar.DAY_OF_MONTH)).show()
//          }
//


        } else {
            adpater = ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, mee)
            meetingSP.adapter = adpater

            adpater = ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, most)
            mostSP.adapter = adpater

            adpater = ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_item, day)
            day2SP.adapter = adpater
        }

        textRL.setOnClickListener {
            if (member_type.equals("3")) {
                var intent = Intent(myContext, CouponTextActivity::class.java)
                startActivity(intent)
            } else {
                var intent = Intent(myContext, MyPostingWriteActivity::class.java)
                intent.putExtra("text", text)
                startActivity(intent)
            }
        }

        nextLL.setOnClickListener {

            var intent = Intent(myContext, MyPostingWriteActivity::class.java)
            intent.putExtra("imgid", imgid)
            intent.putExtra("capture", capture)
            intent.putExtra("startd", startd)
            intent.putExtra("last", last)
            intent.putExtra("mount", mount)


            startActivity(intent)
        }

        cameraRL.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (intent.resolveActivity(mainActivity.packageManager) != null) {

                            startActivityForResult(intent, REQUEST_CAMERA)
                        }

                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {
                    }

                }

                TedPermission.with(myContext)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                    .setPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .check();

            } else {

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(mainActivity.packageManager) != null) {

                    startActivityForResult(intent, REQUEST_CAMERA)
                }

            }


        }


        val imageLoader = ImageLoader(mainActivity.contentResolver)

        adapter = ImageAdapter(myContext, photoList, imageLoader, selected)
        listGV.adapter = adapter
        listGV.setOnItemClickListener { parent, view, position, id ->

            val photo = photoList[position]

//            imgRL.background = Drawable.createFromPath(photo.photoPath)
            //이미지가져오기
            imgid = photo.photoPath!!
            imgIV.setImageBitmap(Utils.getImage(mainActivity.contentResolver, imgid))

        }

        imageLoader.setListener(adapter)

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                loadData()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
            }
        }

        TedPermission.with(myContext)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check();
    }

    private fun loadData() {

        var cursor: Cursor? = null
        val resolver = mainActivity.contentResolver

        var cursor1 = cursor
        try {
            val proj = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val idx = IntArray(proj.size)

            cursor1 = MediaStore.Images.Media.query(
                resolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
            )
            if (cursor1 != null && cursor1.moveToFirst()) {
                idx[0] = cursor1.getColumnIndex(proj[0])
                idx[1] = cursor1.getColumnIndex(proj[1])
                idx[2] = cursor1.getColumnIndex(proj[2])
                idx[3] = cursor1.getColumnIndex(proj[3])
                idx[4] = cursor1.getColumnIndex(proj[4])

                var photo = ImageAdapter.PhotoData()

                do {
                    val photoID = cursor1.getInt(idx[0])
                    val photoPath = cursor1.getString(idx[1])
                    val displayName = cursor1.getString(idx[2])
                    val orientation = cursor1.getInt(idx[3])
                    val bucketDisplayName = cursor1.getString(idx[4])
                    if (displayName != null) {
                        photo = ImageAdapter.PhotoData()
                        photo.photoID = photoID
                        photo.photoPath = photoPath
                        photo.orientation = orientation
                        photo.bucketPhotoName = bucketDisplayName
                        photoList.add(photo)
                    }

                } while (cursor1.moveToNext())

                cursor1.close()
            }
        } catch (ex: Exception) {
            // Log the exception's message or whatever you like
        } finally {
            try {
                if (cursor1 != null && !cursor1.isClosed()) {
                    cursor1.close()
                }
            } catch (ex: Exception) {
            }

        }

        adapter.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    capture = data.extras.get("data") as Bitmap
                    imgIV.setImageBitmap(capture)
                    imgid = "";
                }
            }
            else -> {
                Toast.makeText(myContext, "Unrecognized request code", Toast.LENGTH_SHORT)
            }
        }


    }


}


