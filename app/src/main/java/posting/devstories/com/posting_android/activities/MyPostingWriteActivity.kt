package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import java.io.ByteArrayInputStream


class MyPostingWriteActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null

    var imgid:String? = null
    var mee2 = arrayOf("자유","정보","스터디","동아리","미팅","쿠폰")
    var mount2=arrayOf("1","2","3","4","5","6","7","8","9","10")
    var capture: Bitmap?= null


    var str:String? = null

    var text:String? = null
    var member_id = -1
    var type=""
    var contents = ""
    var count=""
    var geterror = ""





    lateinit var adpater: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posttextwrite)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        text = intent.getStringExtra("text")
        imgid = intent.getStringExtra("imgid")
        capture = intent.getParcelableExtra("capture")



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


        if (imgid != null && "" != imgid && imgid!!.length> 1&&capture != null){
            popupRL.visibility = View.VISIBLE
        }


        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mee2)
        meetingSP.adapter = adpater


        adpater = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mount2)
        mostSP.adapter = adpater







        registTX.setOnClickListener {

            contents = Utils.getString(contentET)

            type = meetingSP.selectedItem.toString()
            count = mostSP.selectedItem.toString()
//            "Free","Info","Study","Class","Metting","Coupon"

            if (type.equals("자유")){
                type = "1"
            }else if (type.equals("정보")){
                type = "2"
            }else if (type.equals("스터디")){
                type = "3"
            }else if (type.equals("동아리")){
                type = "4"
            }else if (type.equals("미팅")){
                type = "5"
            }else if (type.equals("쿠폰")){
                type = "6"
            }

            if(contents==""||contents==null|| contents.isEmpty()){
                geterror = "내용을 입력해주세요"

               Toast.makeText(context,geterror,Toast.LENGTH_SHORT).show()
            }else{
                write()

            }


        }


    }




    fun write(){


        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", count)


        if (capture==null){

        }else{
            params.put("upload",ByteArrayInputStream(Utils.getByteArray(capture)))


//            params.put("upload",capture)
        }



        if (imgid.equals("")){

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
//                        val data = response.getJSONObject("contents")

//                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))
//                        PrefUtils.setPreference(context, "email", Utils.getString(data, "email"))
//                        PrefUtils.setPreference(context, "name", Utils.getString(data, "name"))
//
//                        PrefUtils.setPreference(context, "nick_name", Utils.getString(data, "nick_name"))
//                        PrefUtils.setPreference(context, "passwd", Utils.getString(data, "passwd"))
//                        PrefUtils.setPreference(context, "member_type", Utils.getString(data, "member_type"))
//
//                        PrefUtils.setPreference(context, "birth", Utils.getString(data, "birth"))
//                        PrefUtils.setPreference(context, "gender", Utils.getString(data, "gender"))
//                        PrefUtils.setPreference(context, "school_id", Utils.getString(data, "school_id"))
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












    override fun onDestroy() {
        super.onDestroy()

        progressDialog = null

    }

}
