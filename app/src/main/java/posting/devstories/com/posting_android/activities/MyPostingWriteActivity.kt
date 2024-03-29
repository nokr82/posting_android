package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_posttextwrite.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.io.ByteArrayInputStream


class MyPostingWriteActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    // var imgid:String? = null
    var capture: Bitmap?= null
    var member_type = ""
    var image_uri:String? = null
    var imageUri:Uri? = null
    var str:String? = null
    var posting_id :String?=null
    var member_id = -1
    var type = -1
    var contents = ""
    var contents2:String?=null
    var count:String?=null
    var geterror = ""
    var startd:String?=null
    var last:String?=null
    var mount:Int? = 0
    // var absolutePath:String? =null
    var getmee:String?= null
    var getmost = ""
    var getday:String?= null
    var postingType:String?= null

    var current_school = -1
    var school_id = -1

    var mee = arrayOf("자유", "정보", "스터디", "동아리", "미팅")
    var most = arrayOf("수량", "1", "3", "5", "10", "20", "무제한")

    lateinit var adapter: ArrayAdapter<String>
    lateinit var typeAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posttextwrite)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        member_id =  PrefUtils.getIntPreference(context,"member_id")
        member_type = PrefUtils.getStringPreference(context, "member_type")

        intent = getIntent()
        // 카메라 사진
        // absolutePath = intent.getStringExtra("absolutePath")
        // 포스팅 타입 G-갤러리 P-포토 T-텍스트
        postingType = intent.getStringExtra("postingType")
        getmee = intent.getStringExtra("getmee")
        getmost = intent.getStringExtra("getmost")
        image_uri = intent.getStringExtra("image_uri")

        if("3" == member_type){
            getday = intent.getStringExtra("getday")
        }

        // imgid = intent.getStringExtra("imgid")
        // 수정 contents
        contents2 = intent.getStringExtra("contents")
        startd = intent.getStringExtra("startd")
        current_school = intent.getIntExtra("current_school",-1)
        school_id = intent.getIntExtra("school_id",-1)

        last = intent.getStringExtra("last")
        posting_id = intent.getStringExtra("posting_id")

        val h = intent.getStringExtra("imageUri")
        if(h != null) {
            imageUri = Uri.parse(h)
        }

        mount = intent.getIntExtra("mount",0)

        // 이미지 uri 로드
        if (posting_id != null && !posting_id.equals("") && "M" == postingType) {
            var image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsPosting)
            popupRL.visibility = View.VISIBLE

        } else if ("T" == postingType) {
            popupRL.visibility = View.GONE
            meeting2RL.visibility = View.VISIBLE
            mostRL.visibility = View.VISIBLE

//            checkCategory()

        }

        // 배경 포스트잇
        if (current_school != school_id){
            popupRL.background = getDrawable(R.mipmap.write_bg2)
        }else{
            popupRL.background = getDrawable(R.mipmap.wtite_bg)
        }


        typeAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, mee)
        meetingSP2.adapter = typeAdapter

        adapter = ArrayAdapter<String>(this, R.layout.spinner_item, most)
        mostSP.adapter = adapter

        var typePosition = typeAdapter.getPosition(getmee)
        meetingSP2.setSelection(typePosition)

        var countPosition = adapter.getPosition(getmost)
        mostSP.setSelection(countPosition)

        contentET.setText(contents2)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(str, options)
        options.inJustDecodeBounds = false

        backLL.setOnClickListener {
            finish()
        }

        println("postingType : $postingType, imageUri : $imageUri")

        if (postingType.equals("P") && imageUri != null){
            capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            captureIV.setImageBitmap(capture)
            popupRL.visibility = View.VISIBLE

        }else if (postingType.equals("G") && imageUri != null){
            capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            captureIV.setImageBitmap(capture)
            // ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsUserProfile)
            popupRL.visibility = View.VISIBLE

            // captureIV.setImageBitmap(Utils.getImage(context.contentResolver, imgid))
        }else if (postingType.equals("M") && imageUri != null){
            capture = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            captureIV.setImageBitmap(capture)
            // com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsUserProfile)
            captureIV.visibility = View.VISIBLE
        }




        nextLL2.setOnClickListener {

            contents = Utils.getString(contentET)
//            "Free","Info","Study","Class","Metting","Coupon"


            if (member_type.equals("3")){
                type = 6






//                count = meetingSP3.selectedItem.toString()
//                startd = Utils.getString(date2TX)
//                last = Utils.getString(limit2TX)

                if(contents==""||contents==null|| contents.isEmpty()){
                    geterror = "내용을 입력해주세요"

                    Toast.makeText(context,geterror,Toast.LENGTH_SHORT).show()
                }else{

                    nextLL2.isEnabled = false

                    if (posting_id == null||posting_id == ""){
                        write()
                    }else{

                        edit_posting()
                    }

                }

            }else {
//                type = meetingSP3.selectedItem.toString()
//                count = mostSP3.selectedItem.toString()

//                if(postingType == "T") {
                    getmee = meetingSP2.selectedItem.toString()
                    count = mostSP.selectedItem.toString()
//                }

                if (getmee.equals("자유")) {
                    type = 1
                } else if (getmee.equals("정보")) {
                    type = 2
                } else if (getmee.equals("스터디")) {
                    type = 3
                } else if (getmee.equals("동아리")) {
                    type = 4
                } else if (getmee.equals("미팅")) {
                    type = 5
                } else if (getmee.equals("쿠폰")) {
                    type = 6
                }

                if(count.equals("수량")){

                    Toast.makeText(context,"수량을 선택해주세요",Toast.LENGTH_SHORT).show()
                }

                else if (contents == "" || contents == null || contents.isEmpty()) {
                    geterror = "내용을 입력해주세요"

                    Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
                } else {

                    nextLL2.isEnabled = false

                    if (posting_id == null||posting_id == ""){
                        write()
                    }else{

                        edit_posting()
                    }
                }
            }



        }


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

                            var setMee:ArrayList<String> = ArrayList()
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

                            var typePosition = typeAdapter.getPosition(getmee)
                            meetingSP2.setSelection(typePosition)

                            var countPosition = adapter.getPosition(getmost)
                            mostSP.setSelection(countPosition)

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

    fun write(){


        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", getmost)
        params.put("uses_start_date",startd)
        params.put("uses_end_date",last)
        params.put("current_school_id", PrefUtils.getIntPreference(context, "current_school_id"))
        params.put("days",getday)

        /*
        if (capture==null){

        }else{
            params.put("upload",ByteArrayInputStream(Utils.getByteArray(capture)))


//            params.put("upload",capture)
        }
        */

        // Utils.alert(context, "ii : $imageUri")
        // return

        /*
        if (imageUri != null) {
            val add_file = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            Toast.makeText(context, "f : $add_file", Toast.LENGTH_SHORT).show()

            // val add_file = Utils.getImage(context.contentResolver, imgid)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(add_file)))
        }
        */

        if (capture != null) {
            // val add_file = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // Toast.makeText(context, "f : $add_file", Toast.LENGTH_SHORT).show()

            // val add_file = Utils.getImage(context.contentResolver, imgid)
            params.put("upload", ByteArrayInputStream(Utils.getByteArray(capture)))
        }

        // Toast.makeText(context, "p : $params", Toast.LENGTH_SHORT).show()
        println("params : $params")


        PostingAction.write(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")





                    if ("ok" == result) {

                        Utils.hideKeyboard(context)
//                        val intent = Intent(context,MainActivity::class.java)

                        try {
                            contentResolver.delete(imageUri, null, null);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        //브로드캐스트로 날려주기
                        val intent = Intent()
                        intent.putExtra("tabType",type)
                        intent.action = "SET_VIEW"
                        sendBroadcast(intent)

                        Toast.makeText(context, "글작성이 완료되었습니다", Toast.LENGTH_SHORT).show()

                        finish()


                    } else if ("over" == result) {
                        Toast.makeText(context, "하루 제한량만큼 작성하셨습니다.", Toast.LENGTH_SHORT).show()
                    } else {

                        nextLL2.isEnabled = true

                        geterror = "등록중 장애가 발생하였습니다."

                        Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
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

    fun edit_posting(){

        val params = RequestParams()
        params.put("posting_id", posting_id)
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", count)

        if(postingType == "T") {
            params.put("image", "")
            params.put("image_uri", "")
        } else {

            if (imageUri != null) {
                val add_file = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                // val add_file = Utils.getImage(context.contentResolver, imgid)
                params.put("upload",ByteArrayInputStream(Utils.getByteArray(add_file)))
            }

            /*
            if (capture != null) {
                val add_file = Utils.getImage(context.contentResolver, image_uri)
                // val add_file = Utils.getImage(context.contentResolver, imgid)
                params.put("upload",ByteArrayInputStream(Utils.getByteArray(add_file)))
            }
            */

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
                        val intent = Intent(context,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(context, "수정이 완료되었습니다", Toast.LENGTH_SHORT).show()



                    } else {
                        geterror = "작성실패"

                        Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable, errorResponse: JSONArray?) {
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

    override fun finish() {
        super.finish()

        Utils.hideKeyboard(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
