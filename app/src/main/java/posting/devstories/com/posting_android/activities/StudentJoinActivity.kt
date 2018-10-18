package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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
                Toast.makeText(context, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(getPW==""||getPW==null|| getPW.isEmpty()){
                Toast.makeText(context, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(getPW!=getPW2){
                Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(getNick==""||getNick==null|| getNick.isEmpty()){
                Toast.makeText(context, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            if(getName==""||getName==null|| getName.isEmpty()){
                Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (allCK.isChecked!=true){
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (serviceCK.isChecked!=true){
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (soloCK.isChecked!=true){
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Nick(getNick)

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

    fun schooljoin(email:String, passwd:String, nick_name:String, name:String, member_type:Int, birth: String, gender:String, school_id:Int){
        val params = RequestParams()
        params.put("email", email)
        params.put("name", name)

        params.put("nick_name", nick_name)
        params.put("passwd", passwd)
        params.put("member_type", member_type)
        params.put("birth", birth)
        params.put("gender", gender)
        params.put("school_id", school_id)


        JoinAction.join(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONObject("member")

                        PrefUtils.setPreference(context, "member_id", Utils.getInt(data, "id"))
                        PrefUtils.setPreference(context, "email", Utils.getString(data, "email"))
                        PrefUtils.setPreference(context, "name", Utils.getString(data, "name"))

                        PrefUtils.setPreference(context, "nick_name", Utils.getString(data, "nick_name"))
                        PrefUtils.setPreference(context, "passwd", Utils.getString(data, "passwd"))
                        PrefUtils.setPreference(context, "member_type", Utils.getString(data, "member_type"))

                        PrefUtils.setPreference(context, "birth", Utils.getString(data, "birth"))
                        PrefUtils.setPreference(context, "gender", Utils.getString(data, "gender"))
                        PrefUtils.setPreference(context, "school_id", Utils.getString(data, "school_id"))

                        val intent = Intent(context,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(context, "가입실패", Toast.LENGTH_LONG).show()
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

                        schooljoin(getid,getPW,getNick,getName,membertype,getBirth,gendertype,schoolid)

                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

        progressDialog = null

    }






}
