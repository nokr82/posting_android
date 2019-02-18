package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import android.view.View
import posting.devstories.com.posting_android.adapter.SchoolAdapter
import posting.devstories.com.posting_android.adapter.SpinnerAdapter
import posting.devstories.com.posting_android.base.Config
import java.text.SimpleDateFormat
import java.util.*

class StudentJoinActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    var years:ArrayList<String> = ArrayList<String>()
    var gender:ArrayList<String> = ArrayList<String>()
    lateinit var adpater:ArrayAdapter<String>
    lateinit var adpater2:ArrayAdapter<String>

    lateinit var spinnerAdapter:SpinnerAdapter

    var gendertype =""
    val membertype = 2

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

    val JOIN_OK = 101
    val JOIN_ERROR = 201

    var school_id = -1
    var schoolname = ""

    private var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    private lateinit var adapter: SchoolAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentjoin)

        intent = getIntent()
        school_id = intent.getIntExtra("school_id",-1)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        allCK.setOnClickListener{

            val b = allCK.isChecked

            if (b==true){
                serviceCK.isChecked=true
                soloCK.isChecked=true
                agree3CK.isChecked=true
                agree4CK.isChecked=true
            }else{
                serviceCK.isChecked=false
                soloCK.isChecked=false
                agree3CK.isChecked=false
                agree4CK.isChecked=false
            }
        }
        serviceCK.setOnCheckedChangeListener { compoundButton, b ->

            val check_1 = soloCK.isChecked
            val check_2 = agree3CK.isChecked
            val check_3 = agree4CK.isChecked
            allCK.isChecked = b&&check_1&&check_2&&check_3


        }
        soloCK.setOnCheckedChangeListener { compoundButton, b ->
            val check_1 = serviceCK.isChecked
            val check_2 = agree3CK.isChecked
            val check_3 = agree4CK.isChecked
            allCK.isChecked = b&&check_1&&check_2&&check_3
        }
        agree3CK.setOnCheckedChangeListener { compoundButton, b ->
            val check_1 = serviceCK.isChecked
            val check_2 = soloCK.isChecked
            val check_3 = agree4CK.isChecked
            allCK.isChecked = b&&check_1&&check_2&&check_3
        }
        agree4CK.setOnCheckedChangeListener { compoundButton, b ->
            val check_1 = serviceCK.isChecked
            val check_2 = soloCK.isChecked
            val check_3 = agree3CK.isChecked

            allCK.isChecked = b&&check_1&&check_2&&check_3
        }



        SchoolLV.isExpanded = true

        adapter = SchoolAdapter(this, R.layout.school_item, adapterData)
        SchoolLV.adapter = adapter
        adapter.notifyDataSetChanged()

        SchoolLV.setOnItemClickListener { adapterView, view, i, l ->

            //학교이름을 뺴올라면 데이터에서 포지션값을구해서
            //스쿨인덱스의 학교이름을 찾는다
            var data = adapterData.get(i)
            var school:JSONObject = data.getJSONObject("School")
//            schoolET.text = Editable.Factory.getInstance().newEditable(Utils.getString(data, "school_name"))
            schoolET.setText(Utils.getString(school,"name"))
            schoolname = Utils.getString(school,"name")
            school_id=Utils.getInt(school,"id")

        }

        schoolET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                // you can call or do what you want with your EditText here

                // yourEditText...

                val keyword = Utils.getString(schoolET)

                School(keyword)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

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
            } else if (!Utils.isValidEmail(getid)) {
                geterror = "이메일을 확인해주세요"

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
            } else if(school_id < 1) {
                geterror = "학교를 선택해주세요."

                dlgView( geterror)
            }else if(getName==""||getName==null|| getName.isEmpty()){
                geterror = "이름을 입력해주세요"

                dlgView( geterror)
//            }else if (allCK.isChecked!=true){
//                geterror = "이용약관에 동의해주세요"
//
//                dlgView( geterror)
            }else if (serviceCK.isChecked!=true){
                geterror = "서비스 이용약관에 동의해주세요"

                dlgView( geterror)
            }else if (soloCK.isChecked!=true){
                geterror = "개인정보처리방침에 동의해주세요"

                dlgView( geterror)
            } else if(agree3CK.isChecked != true) {
                geterror = "통합커뮤니티 이용규칙에 동의해주세요"

                dlgView( geterror)
            } else {
                Nick(getNick)
            }


//            email:String,passwd:String,nick_name:String,name:String, member_type:String,birth:Int, gender:String,school_id:Int




        }
        finishLL.setOnClickListener {
            finish()
        }

        years.add("태어난 연도");

        // 현재 연도
        val date = Date(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy")

        val years_str = sdf.format(date)
        var year = years_str.toInt()

        // 시작 연도
//        var start_year = year - 20
//        start_year = start_year - (start_year % 10)
        var start_year = 1990

        for (i in start_year..year) {

            years.add(i.toString())

        }

        spinnerAdapter = SpinnerAdapter(context, R.layout.item_join, years)
        birthSP.adapter  = spinnerAdapter
        spinnerAdapter.setDropDownViewResource(R.layout.item_join);
//        birthSP.setOnItemClickListener { parent, view, position, id ->
//
//            var years :String = years.get(position);
//
//        }

        gender.add("성별")
        gender.add("남")
        gender.add("여")

        spinnerAdapter = SpinnerAdapter(context, R.layout.item_join, gender)
        genderSP.adapter = spinnerAdapter
        spinnerAdapter.setDropDownViewResource(R.layout.item_join);

        agree1WV.loadUrl(Config.url + "/agree/agree1");
        agree2WV.loadUrl(Config.url + "/agree/agree2");
        agree3WV.loadUrl(Config.url + "/agree/agree3");
        agree4WV.loadUrl(Config.url + "/agree/agree4");

        agree1BtnLL.setOnClickListener {
            if(agree1LL.visibility == View.VISIBLE) {
                agree1LL.visibility = View.GONE
            } else {
                agree1LL.visibility = View.VISIBLE
            }
        }

        agree2BtnLL.setOnClickListener {
            if(agree2LL.visibility == View.VISIBLE) {
                agree2LL.visibility = View.GONE
            } else {
                agree2LL.visibility = View.VISIBLE
            }
        }

        agree3BtnLL.setOnClickListener {
            if(agree3LL.visibility == View.VISIBLE) {
                agree3LL.visibility = View.GONE
            } else {
                agree3LL.visibility = View.VISIBLE
            }
        }

        agree4BtnLL.setOnClickListener {
            if(agree4LL.visibility == View.VISIBLE) {
                agree4LL.visibility = View.GONE
            } else {
                agree4LL.visibility = View.VISIBLE
            }
        }

    }

    fun School(searchKeyword: String) {

        val params = RequestParams()
        params.put("searchKeyword", searchKeyword)

        SchoolAction.School(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    adapterData.clear()
                    adapter.notifyDataSetChanged()

                    val result = response!!.getString("result")
                    val dbSearchKeyword = response!!.getString("searchKeyword")
                    val list = response!!.getJSONArray("list")

                    if("ok" == result && dbSearchKeyword == searchKeyword) {

                        for (i in 0..(list.length() - 1)) {

                            var data  = list.get(i) as JSONObject
                            checkSchoolData(data)
//                            adapterData.add(data)

                        }

                        adapter.notifyDataSetChanged()

                    } else {

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

    fun checkSchoolData(data:JSONObject){

        var add = true

        val addData = data.getJSONObject("School")

        for (i in 0.. (adapterData.size - 1)) {
            val json = adapterData.get(i)
            val school = json.getJSONObject("School")

            if(Utils.getString(school, "id") == Utils.getString(addData, "id")) {
                add = false
            }

        }

        if(add) {
            adapterData.add(data)
        }
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
        params.put("school_id",school_id)

        var agree_yn = "N"
        if(agree4CK.isChecked) {
            agree_yn = "Y"
        }

        params.put("agree_yn", agree_yn)


        JoinAction.join(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    geterror = Utils.getString(response, "message")

                    if ("ok" == result) {
                        val member = response.getJSONObject("member")

                        email = Utils.getString(member, "email");
                        passwd = Utils.getString(member, "passwd");

                        var intent = Intent(context, DlgJoinActivity::class.java);
                        intent.putExtra("type", "join_ok")
                        startActivityForResult(intent, JOIN_OK)

                    } else {
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
//        var mPopupDlg: DialogInterface? = null
//
//        val builder = AlertDialog.Builder(this)
//        val dialogView = layoutInflater.inflate(R.layout.joinerror_dlg, null)
//        val errorTX = dialogView.findViewById<TextView>(R.id.errorTX)
//        val PostingStartTX = dialogView.findViewById<TextView>(R.id.PostingStartTX)
//        errorTX.setText(error)
//        mPopupDlg =  builder.setView(dialogView).show()
//        PostingStartTX.setOnClickListener {
//            mPopupDlg.dismiss()
//        }

        var intent = Intent(context, DlgJoinActivity::class.java);
        intent.putExtra("type", "join_error")
        intent.putExtra("message", error)
        startActivityForResult(intent, JOIN_ERROR)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            JOIN_OK -> {

                if(resultCode == Activity.RESULT_OK) {
                    login(email, passwd)
                }

            }
            JOIN_ERROR -> {

            }
        }

    }

}
