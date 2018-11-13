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
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class DlgReviewActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var member_id = -1
    var review_id = -1
    var company_member_id = -1
    var dlgtype:String?= null
    var image_uri :String?= null
    var contents:String?= null

    val EIDT_RIVEW = 301;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myposting_dlg)

        this.context = this
        progressDialog = ProgressDialog(context)

        intent = getIntent()

        member_id =intent.getIntExtra("member_id",-1)
        review_id = intent.getIntExtra("review_id", -1)
        company_member_id = intent.getIntExtra("company_member_id", -1)
        dlgtype = intent.getStringExtra("dlgtype")
        image_uri  = intent.getStringExtra("image_uri")
        contents = intent.getStringExtra("contents")

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
        else if (dlgtype.equals("MyReview")){

            recyTV.visibility = View.GONE
            delTV.setOnClickListener {
                del_review()
            }

            modiTV.setOnClickListener {

                val intent = Intent(context, ReviewWriteActivity::class.java)
                intent.putExtra("review_id", review_id)
                intent.putExtra("image_uri",image_uri)
                intent.putExtra("contents",contents)
                intent.putExtra("company_member_id", company_member_id)
                startActivityForResult(intent, EIDT_RIVEW)
                finish()

            }

        }

    }

    //안될시 디테일에 뿌려줄것
    fun del_review(){
        val params = RequestParams()
        params.put("review_id", review_id)

        ReviewAction.del(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        intent = Intent()
                        intent.putExtra("review_id", review_id)
                        intent.action = "DEL_REVIEW"
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
        params.put("review_id", review_id)
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
                        Toast.makeText(context, "신고한 게시물입니다.", Toast.LENGTH_LONG).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == EIDT_RIVEW) {
            if(resultCode == Activity.RESULT_OK) {

                var intent = Intent()
                setResult(Activity.RESULT_OK, intent)

                finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }
}
