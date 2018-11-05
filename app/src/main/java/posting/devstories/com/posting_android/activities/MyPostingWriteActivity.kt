package posting.devstories.com.posting_android.activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_posttextwrite.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import android.widget.*
import kotlinx.android.synthetic.main.activity_posttextwrite.view.*
import kotlinx.android.synthetic.main.fra_write.*
import posting.devstories.com.posting_android.R.id.*
import java.io.ByteArrayInputStream
import java.text.SimpleDateFormat
import java.util.*


class MyPostingWriteActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null

    var imgid:String? = null
    var mee = arrayOf("자유","정보","스터디","동아리","미팅")
    var  most =arrayOf("수량","1","3","5","10","20","∞")

    var day = arrayOf("기간","1일","5일","7일","10일","30일","60일")

    var capture: Bitmap?= null
    var member_type = ""
    var image_uri:String? = null
    var image:String? = null
    var str:String? = null
    var posting_id :String?=null
    var text:String? = null
    var member_id = -1
    var type:String?=null
    var contents = ""
    var contents2:String?=null
    var count:String?=null
    var geterror = ""
    var startd:String?=null
    var last:String?=null
    var mount:Int? = 0



    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posttextwrite)


        this.context = this
        progressDialog = ProgressDialog(context)


        member_type = PrefUtils.getStringPreference(context, "member_type")
        intent = getIntent()
        text = intent.getStringExtra("text")
        imgid = intent.getStringExtra("imgid")
        capture = intent.getParcelableExtra("capture")
        contents2 = intent.getStringExtra("contents")
        startd = intent.getStringExtra("startd")
        last = intent.getStringExtra("last")
        posting_id = intent.getStringExtra("posting_id")
        image = intent.getStringExtra("image")
        mount = intent.getIntExtra("mount",0)


        contentET.setText(contents2)

        if (text.equals("1")){
            popupRL.visibility = View.GONE
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(str, options)
        options.inJustDecodeBounds = false




        backLL.setOnClickListener {
            finish()
        }



        member_id =  PrefUtils.getIntPreference(context,"member_id")


        //이미지
        img2RL.background = Drawable.createFromPath(imgid)
        captureIV.setImageBitmap(capture)
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(image, captureIV, Utils.UILoptionsUserProfile)

        if (imgid != null && "" != imgid && imgid!!.length> 1&&capture != null&&image != null){
            popupRL.visibility = View.VISIBLE
        }





        if (member_type.equals("3")){
            meetingSP3.visibility = View.GONE


            adpater = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, day)
            daySP3.adapter = adpater


            adpater = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, most)
            mostSP3.adapter = adpater
//
//            date2TX.text = startd
//            limit2TX.text = last
//
//            if (startd==null||startd.equals("")){
//                date2TX.text = SimpleDateFormat("yy.MM.dd").format(System.currentTimeMillis())+"~"
//            }

            mostSP3.setSelection(mount!!)


            var cal = Calendar.getInstance()

//            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                cal.set(Calendar.YEAR, year)
//                cal.set(Calendar.MONTH, monthOfYear)
//                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//                val myFormat = "yy.MM.dd" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
//                date2TX.text = sdf.format(cal.time)+"~"
//
//            }
//
//            val dateSetListener2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                cal.set(Calendar.YEAR, year)
//                cal.set(Calendar.MONTH, monthOfYear)
//                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//                val myFormat = "yy.MM.dd" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
//                limit2TX.text = sdf.format(cal.time)
//
//            }

//            date2LL.setOnClickListener {
//                DatePickerDialog(context, dateSetListener2,
//                    cal.get(Calendar.YEAR),
//                    cal.get(Calendar.MONTH),
//                    cal.get(Calendar.DAY_OF_MONTH)).show()
//                DatePickerDialog(context, dateSetListener,
//                    cal.get(Calendar.YEAR),
//                    cal.get(Calendar.MONTH),
//                    cal.get(Calendar.DAY_OF_MONTH)).show()
//            }

        }else{
            // dateTX.visibility = View.GONE
            adpater = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, day)
            daySP3.adapter = adpater

            adpater = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mee)
            meetingSP3.adapter = adpater

            adpater = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, most)
            mostSP3.adapter = adpater

        }







        nextLL2.setOnClickListener {

            contents = Utils.getString(contentET)


//            "Free","Info","Study","Class","Metting","Coupon"


            if (member_type.equals("3")){
                type = "6"
                count = meetingSP3.selectedItem.toString()
//                startd = Utils.getString(date2TX)
//                last = Utils.getString(limit2TX)

                if(contents==""||contents==null|| contents.isEmpty()){
                    geterror = "내용을 입력해주세요"

                    Toast.makeText(context,geterror,Toast.LENGTH_SHORT).show()
                }
                if(count.equals("수량")){

                    Toast.makeText(context,"수량을 선택해주세요",Toast.LENGTH_SHORT).show()
                }
                else{
                    if (posting_id == null||posting_id == ""){
                        write()
                    }else{

                        edit_posting()
                    }

                }

            }else {
                type = meetingSP3.selectedItem.toString()
                count = mostSP3.selectedItem.toString()
                if (type.equals("자유")) {
                    type = "1"
                } else if (type.equals("정보")) {
                    type = "2"
                } else if (type.equals("스터디")) {
                    type = "3"
                } else if (type.equals("동아리")) {
                    type = "4"
                } else if (type.equals("미팅")) {
                    type = "5"
                } else if (type.equals("쿠폰")) {
                    type = "6"
                }
                if(count.equals("수량")){

                    Toast.makeText(context,"수량을 선택해주세요",Toast.LENGTH_SHORT).show()
                }

                else if (contents == "" || contents == null || contents.isEmpty()) {
                    geterror = "내용을 입력해주세요"

                    Toast.makeText(context, geterror, Toast.LENGTH_SHORT).show()
                } else {
                    if (posting_id == null||posting_id == ""){
                        write()
                    }else{

                        edit_posting()
                    }
                }
            }



        }


    }




    fun write(){


        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", count)
        params.put("uses_start_date",startd)
        params.put("uses_end_date",last)


        if (capture==null){

        }else{
            params.put("upload",ByteArrayInputStream(Utils.getByteArray(capture)))


//            params.put("upload",capture)
        }



        if (imgid.equals("")||imgid==null){

        }else{


            val add_file = Utils.getImage(context.contentResolver, imgid)
            params.put("upload",ByteArrayInputStream(Utils.getByteArray(add_file)))

        }




        PostingAction.write(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")





                    if ("ok" == result) {


                        val intent = Intent(context,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(context, "글작성이 완료되었습니다", Toast.LENGTH_SHORT).show()



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





    fun edit_posting(){


        val params = RequestParams()

        params.put("posting_id", posting_id)
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", count)


        if (capture==null){

        }else{
            params.put("upload",ByteArrayInputStream(Utils.getByteArray(capture)))


//            params.put("upload",capture)
        }
//        if (image.equals("")||image==null){
//
//        }else{
//
//
//            val add_file = Utils.getImage(context.contentResolver, image_uri)
//            params.put("upload",ByteArrayInputStream(Utils.getByteArray(add_file)))
//
//        }

        if (imgid.equals("")||imgid==null){

        }else{


            val add_file = Utils.getImage(context.contentResolver, imgid)
            params.put("upload",ByteArrayInputStream(Utils.getByteArray(add_file)))

        }




        PostingAction.edit_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {


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








    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
