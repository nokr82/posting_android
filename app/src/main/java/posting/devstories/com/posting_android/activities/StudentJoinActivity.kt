package posting.devstories.com.posting_android.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_studentjoin.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.*
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import android.content.DialogInterface

class StudentJoinActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    var gender = arrayOf("남", "여")
    var years:ArrayList<Int> = ArrayList<Int>()
    lateinit var adpater:ArrayAdapter<Int>
    lateinit var adpater2:ArrayAdapter<String>


    var gendertype =""
    var birthtype = 0
    val membertype = 2
    var schoolid = -1

    var getid = ""
    var getPW=""
    var getPW2 = ""
    var getNick=""
    var getName = ""
    var getBirth=""
    var getGender = ""
    var geterror = ""
    var email = ""
    var passwd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentjoin)

        intent = getIntent()
        schoolid = intent.getIntExtra("school_id",-1)

        this.context = this



        allCK.setOnCheckedChangeListener{
            compoundButton, b ->
            if (b==true){
                serviceCK.isChecked=true
                soloCK.isChecked=true
            }else{
                serviceCK.isChecked=false
                soloCK.isChecked=false
            }
        }
        serviceCK.setOnCheckedChangeListener { compoundButton, b ->

            val check = soloCK.isChecked
            allCK.isChecked = b&&check


        }
        soloCK.setOnCheckedChangeListener { compoundButton, b ->
            val check = serviceCK.isChecked
            allCK.isChecked = b&&check
        }


        PostingStartTX.setOnClickListener {


            getid = Utils.getString(idET)
            getPW = Utils.getString(pwET)
            getPW2= Utils.getString(pw2ET)
            getNick = Utils.getString(nickET)
            getName= Utils.getString(nameET)
            getBirth = birthSP.selectedItem.toString()
            getGender = genderSP.selectedItem.toString()

            if (getGender.equals("남")){
              gendertype = "M"
            }else{
                gendertype = "F"
            }
            if(getid==""||getid==null|| getid.isEmpty()){
               geterror = "이메일을 입력해주세요"

                dlgView( geterror)
            }
            else if(getPW==""||getPW==null|| getPW.isEmpty()){
                geterror = "비밀번호를 입력해주세요"

                dlgView( geterror)
            }
            else if(getPW!=getPW2){
                geterror = "비밀번호가 일치하지 않습니다"

                dlgView( geterror)
            }else if(getNick==""||getNick==null|| getNick.isEmpty()){
                geterror = "닉네임을 입력해주세요"

                dlgView( geterror)
            }else if(getName==""||getName==null|| getName.isEmpty()){
                geterror = "이름을 입력해주세요"

                dlgView( geterror)
            }else if (allCK.isChecked!=true){
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }else if (serviceCK.isChecked!=true){
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }else if (soloCK.isChecked!=true){
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }else {
                Nick(getNick)
            }


//            email:String,passwd:String,nick_name:String,name:String, member_type:String,birth:Int, gender:String,school_id:Int




        }
        finishLL.setOnClickListener {
            finish()
        }


        for (i in 1968..2018) {

            years.add(i)

        }
        adpater = ArrayAdapter<Int>(this,android.R.layout.simple_spinner_item,years)

        birthSP.adapter  = adpater





        adpater2 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender)
        genderSP.adapter = adpater2


    }

    fun schooljoin(){
        val params = RequestParams()
        params.put("email", getid)
        params.put("name", getName)
        params.put("nick_name", getNick)
        params.put("passwd", getPW)
        params.put("member_type", membertype)
        params.put("birth", getBirth)
        params.put("gender", gendertype)
        params.put("school_id",schoolid )


        JoinAction.join(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val member = response.getJSONObject("member")

                        email = Utils.getString(member, "email");
                        passwd = Utils.getString(member, "passwd");

                        joinDlg()

                    } else {
                        geterror = "가입실패"

                        dlgView( geterror)
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
                Utils.alert(context, "가입중 장애가 발생하였습니다.")
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



    fun Nick(nick_name:String){
        val params = RequestParams()
        params.put("nick_name", nick_name)

        JoinAction.check_nick_name(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    val message:String = response!!.getString("message")


                    if ("ok" == result) {

                        schooljoin()

                    } else {
                        geterror =message

                        dlgView( geterror)
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


    fun dlgView(error:String){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.joinerror_dlg, null)
        val errorTX = dialogView.findViewById<TextView>(R.id.errorTX)
        val PostingStartTX = dialogView.findViewById<TextView>(R.id.PostingStartTX)
        errorTX.setText(error)
        mPopupDlg =  builder.setView(dialogView).show()
        PostingStartTX.setOnClickListener {

            mPopupDlg.dismiss()
        }

    }

    fun joinDlg(){
        var mPopupDlg: DialogInterface? = null

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.join_dlg, null)
        val PostingStartTX = dialogView.findViewById<TextView>(R.id.PostingStartTX)
        mPopupDlg =  builder.setView(dialogView).show()
        PostingStartTX.setOnClickListener {


            login(email, passwd);


//            val intent = Intent(context,LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)

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

                        PrefUtils.setPreference(context, "current_school_id", school_id)
                        PrefUtils.setPreference(context, "current_school_image_uri", school_image_uri)

                        PrefUtils.setPreference(context, "loginID", loginID)
                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))
                        PrefUtils.setPreference(context, "email", Utils.getString(data, "email"))
                        PrefUtils.setPreference(context, "passwd", Utils.getString(data, "passwd"))
                        PrefUtils.setPreference(context, "member_type", Utils.getString(data, "member_type"))
                        PrefUtils.setPreference(context, "school_id", Utils.getInt(data, "school_id"))
                        PrefUtils.setPreference(context, "confirm_yn", Utils.getString(data, "confirm_yn"))
                        PrefUtils.setPreference(context, "autoLogin", true)

                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else if("confirm_no" == result) {
                        Toast.makeText(context, "관리자 승인 후 로그인이 가능합니다.", Toast.LENGTH_LONG).show()

                        val intent = Intent(context,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()

                        val intent = Intent(context,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

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
