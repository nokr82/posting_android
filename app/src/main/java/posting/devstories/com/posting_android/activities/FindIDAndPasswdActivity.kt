package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.find_id_and_passwd_activity.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.LoginAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class FindIDAndPasswdActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    var tab = "id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_id_and_passwd_activity)

        this.context = this
        progressDialog = ProgressDialog(context)


        finishLL.setOnClickListener {
            finish()
        }

        findPWRL.setOnClickListener {

            tab = "pw"

            setView()
            pwfindV.visibility=View.VISIBLE
            findTV.text = "패스워드 찾기"
        }

        findIDRL.setOnClickListener {

            tab = "id"

            setView()
            idfindV.visibility = View.VISIBLE
            findTV.text = "아이디 찾기"

        }

        findTV.setOnClickListener {
            val email:String = emailET.text.toString()
            val getName:String = nameET.text.toString()

            if (email==""||email==null || email.isEmpty()){
                Toast.makeText(this,"이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getName==""||getName == null){
                Toast.makeText(this,"이름를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            Toast.makeText(this,"찾는중...", Toast.LENGTH_SHORT).show()
            if("id" == tab) {

            } else {
                findPW(email, getName)
            }

        }

    }

    fun setView(){

        emailET.setText("")
        nameET.setText("")

        pwfindV.visibility = View.INVISIBLE
        idfindV.visibility = View.INVISIBLE
    }


    fun findPW(email:String, name:String){
        val params = RequestParams()
        params.put("name", name)
        params.put("email", email)

        LoginAction.find_passwd(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("member")

                        Toast.makeText(context, "패스워드 : " + Utils.getString(data, "passwd"), Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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
                Utils.alert(context, "조회중 장애가 발생하였습니다.")
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


}
