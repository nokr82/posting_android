package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.Actions.ReviewAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class DlgReportActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private val _active = true

    var member_id = -1
    var current_school_id = -1
    var posting_id :String?= null
    var dlgtype:String?= null
    var image_uri :String?= null
    var member_type:String?= null
    var contents:String?= null
    var type = 1
    var count = 0
    var save_id:String?= null
    var report_member_id:String?= null
    var chatting_yn = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myposting_dlg)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()
        member_id =intent.getIntExtra("member_id",-1)
        current_school_id =intent.getIntExtra("current_school_id",-1)
        posting_id = intent.getStringExtra("posting_id")
        dlgtype = intent.getStringExtra("dlgtype")
        save_id= intent.getStringExtra("save_id")
        image_uri  = intent.getStringExtra("image_uri")
        contents = intent.getStringExtra("contents")
        member_type = intent.getStringExtra("member_type")
        report_member_id = intent.getStringExtra("report_member_id")
        type = intent.getIntExtra("type",1)
        count = intent.getIntExtra("count",0)
//        this.setFinishOnTouchOutside(true)
        val titleTV = findViewById<TextView>(R.id.titleTV)
        val delTV = findViewById<TextView>(R.id.delTV)
        val modiTV = findViewById<TextView>(R.id.modiTV)
        val recyTV = findViewById<TextView>(R.id.recyTV)


        if (dlgtype.equals("police")) {

            titleTV.text = "이 포스트를 신고하는 이유를 선택하세요"
            delTV.text = "불건전합니다"
            modiTV.text = "부적절합니다"
            recyTV.text = "스팸입니다"

            delTV.setOnClickListener {
                report("1")
                finish()

            }
            modiTV.setOnClickListener {
                report("2")
                finish()
            }
            recyTV.setOnClickListener {
                report("3")
                finish()
            }

        }
        else if (dlgtype.equals("Myposting")){

            chatting_yn = intent.getStringExtra("chatting_yn")

            recyTV.visibility = View.GONE
            delTV.setOnClickListener {
                del_posting()
//                val intent = Intent(context, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)

            }

            modiTV.setOnClickListener {

                val intent = Intent(context, PostWriteActivity::class.java)
                intent.putExtra("posting_id", posting_id)
                intent.putExtra("current_school", current_school_id)
                intent.putExtra("school_id", PrefUtils.getIntPreference(context, "school_id"))
                intent.putExtra("image_uri",image_uri)
                intent.putExtra("member_type",member_type)
                intent.putExtra("contents",contents)
                intent.putExtra("type",type)
                intent.putExtra("count",count)
                intent.putExtra("chatting_yn",chatting_yn)
                startActivity(intent)
                finish()


            }

        }
        else if (dlgtype.equals("Storage")){
            titleTV.text = "My Storage"
            recyTV.visibility = View.GONE
            modiTV.visibility = View.GONE

            delTV.setOnClickListener {
                savedel_posting()
                //이것을 어찌할꼬...
//                val intent = Intent(context, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)

            }
        } else if (dlgtype.equals("police_member")) {

            titleTV.text = "이 사용자를 신고하는 이유를 선택하세요"
            delTV.text = "불건전합니다"
            modiTV.text = "부적절합니다"
            recyTV.text = "스팸입니다"

            delTV.setOnClickListener {
                report("1")
                finish()

            }
            modiTV.setOnClickListener {
                report("2")
                finish()
            }
            recyTV.setOnClickListener {
                report("3")
                finish()
            }

        }

    }

    //안될시 디테일에 뿌려줄것

    fun del_posting(){
        val params = RequestParams()
        params.put("posting_id", posting_id)

        PostingAction.del_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("posting_id", posting_id)
                        intent.putExtra("type", type)
                        intent.action = "DEL_POSTING"
                        sendBroadcast(intent)
                        setResult(Activity.RESULT_OK, intent)


                        //브로드캐스트로 날려주기
                        val intent = Intent()
                        intent.putExtra("tabType",type)
                        intent.action = "SET_VIEW"
                        sendBroadcast(intent)

                        finish()

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

    fun report(type:String){
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("posting_id", posting_id)
        params.put("report_member_id", report_member_id)
        params.put("type", type)

        ReviewAction.report(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        var intent = Intent(context, DlgPoliceActivity::class.java)
                        startActivity(intent)

                    } else if("already" == result) {

                        if("M" == Utils.getString(response, "report_type")) {
                            Toast.makeText(context, "신고한 회원입니다.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "신고한 게시물입니다.", Toast.LENGTH_LONG).show()
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

    fun savedel_posting(){
        val params = RequestParams()
        params.put("posting_id", save_id)

        PostingAction.savedel_posting(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("save_id", save_id)
                        intent.putExtra("type", type)
                        intent.action = "SAVE_DEL_POSTING"
                        sendBroadcast(intent)
                        setResult(RESULT_OK, intent)

                        finish()

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

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
