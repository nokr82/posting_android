package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.LoginAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class LoginActivity : RootActivity() {

    lateinit var context:Context
    private var progressDialog: ProgressDialog? = null
    var autoLogin = false

    var first = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        JoinStudentLL.setOnClickListener {
//            val intent = Intent(this, SchoolActivity::class.java)
//            intent.putExtra("member_type", "2")
//            startActivity(intent)
            val intent = Intent(context, StudentJoinActivity::class.java)
            intent.putExtra("member_type", "2")
            startActivity(intent)
        }


        OrderjoinLL.setOnClickListener {
            val intent = Intent(this, OrderJoinActivity::class.java)
            startActivity(intent)
        }

        StartTV.setOnClickListener {

            var getName = Utils.getString(IDET)
            var getPW = Utils.getString(PWET)

            // 자동 로그인
            autoLogin = autoCK.isChecked

            if (getName == "" || getName == null || getName.isEmpty()) {
                Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getPW == "" || getPW == null || getPW.isEmpty()) {
                Toast.makeText(context, "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                PWET.requestFocus()
                return@setOnClickListener
            }

            if(!Utils.isValidEmail(getName)) {
                Toast.makeText(context, "이메일을 확인해주세요.", Toast.LENGTH_LONG).show();
                IDET.requestFocus()
                return@setOnClickListener
            }

            login(getName, getPW)

        }

        idpwfindTX.setOnClickListener {
            val intent = Intent(this, FindIDAndPasswdActivity::class.java)
            startActivity(intent)
        }

    }

    fun login(email:String, passwd:String){
        val params = RequestParams()
        params.put("email", email)
        params.put("passwd", passwd)

        LoginAction.login(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val loginID = response.getString("loginID")
                        val data = response.getJSONObject("member")
                        val school = response.getJSONObject("school")
                        val school_id = Utils.getInt(school, "id")
                        val school_image_uri = Utils.getString(school, "image_uri")




//                        PrefUtils.setPreference(context, "current_school_id", school_id)
                        PrefUtils.setPreference(context, "current_school_id", -1)
                        PrefUtils.setPreference(context, "current_school_image_uri", "")

                        PrefUtils.setPreference(context, "school_domain", Utils.getString(school, "domain"))

                        PrefUtils.setPreference(context, "loginID", loginID)
                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))
                        PrefUtils.setPreference(context, "email", Utils.getString(data, "email"))
                        PrefUtils.setPreference(context, "passwd", Utils.getString(data, "passwd"))
                        PrefUtils.setPreference(context, "member_type", Utils.getString(data, "member_type"))
                        PrefUtils.setPreference(context, "school_id", Utils.getInt(data, "school_id"))
                        PrefUtils.setPreference(context, "confirm_yn", Utils.getString(data, "confirm_yn"))
                        PrefUtils.setPreference(context, "active_yn", Utils.getString(data, "active_yn"))
                        PrefUtils.setPreference(context, "autoLogin", autoLogin)


                        first =  PrefUtils.getBooleanPreference(context, "first")
                        if (first==false){
                            val intent = Intent(context, GuideActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }else{
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }



                    } else if("confirm_no" == result) {
                        Toast.makeText(context, "관리자 승인 후 로그인이 가능합니다.", Toast.LENGTH_LONG).show()
                    } else if("block" == result) {

                        val intent = Intent(context, DlgCommonActivity::class.java)
                        intent.putExtra("contents", "사용제한되었습니다.\n 고객센터로 문의하세요\n\n 문의 메일\n wepostkorea@gmail.com")
                        startActivity(intent)

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


    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }


    }

}
