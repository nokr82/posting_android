package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_orderjoin.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.AddressAction
import posting.devstories.com.posting_android.Actions.JoinAction
import posting.devstories.com.posting_android.Actions.LoginAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class OrderJoinActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    val SELECT_SCHOOL = 101
    val ADDRESS = 102
    val JOIN_ERROR = 201
    val JOIN_OK = 301

    var school_id = -1
    var sms_code = ""

    var email = ""
    var company_num = ""
    var company_name = ""
    var ori_phone = ""
    var name = ""
    var passwd = ""
    var geterror = ""

    var lng:String = "";
    var lat:String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderjoin)

        this.context = this
        progressDialog = ProgressDialog(context)

        allCB.setOnClickListener {
            var checked = allCB.isChecked
            if (checked) {
                serviceCK.isChecked = true
                soloCK.isChecked = true
                agree3CK.isChecked = true
                agree4CK.isChecked = true
            } else {
                serviceCK.isChecked = false
                soloCK.isChecked = false
                agree3CK.isChecked = false
                agree4CK.isChecked = false
            }
        }

        serviceCK.setOnCheckedChangeListener { compoundButton, b ->

            val check = soloCK.isChecked
            val check_2 = agree3CK.isChecked
            val check_3 = agree4CK.isChecked

            allCB.isChecked = b && check && check_2 && check_3

        }

        soloCK.setOnCheckedChangeListener { compoundButton, b ->

            val check = serviceCK.isChecked
            val check_2 = agree3CK.isChecked
            val check_3 = agree4CK.isChecked

            allCB.isChecked = b && check && check_2 && check_3

        }

        agree3CK.setOnCheckedChangeListener { compoundButton, b ->

            val check = serviceCK.isChecked
            val check_2 = soloCK.isChecked
            val check_3 = agree4CK.isChecked

            allCB.isChecked = b && check && check_2 && check_3

        }

        agree4CK.setOnCheckedChangeListener { compoundButton, b ->

            val check = serviceCK.isChecked
            val check_2 = soloCK.isChecked
            val check_3 = agree3CK.isChecked

            allCB.isChecked = b && check && check_2 && check_3

        }

        sendSMSTV.setOnClickListener {
            val getPhone: String = Utils.getString(phoneET)

            if (getPhone == "" || getPhone == null || getPhone.isEmpty()) {
                Toast.makeText(context, "휴대폰번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {

                sendSMS(getPhone);

            }
        }

        schoolLL.setOnClickListener {
            val intent = Intent(this, SchoolActivity::class.java)
            intent.putExtra("member_type", "3")
            startActivityForResult(intent, SELECT_SCHOOL)
        }

        addressLL.setOnClickListener {
            var intent = Intent(context, AddressActivity::class.java);
            startActivityForResult(intent, ADDRESS)
        }

        PostingStartTX.setOnClickListener {

             email = Utils.getString(OfficeET)
            val getPW: String = Utils.getString(pwET)
            val getPW2: String = Utils.getString(pw2ET)
            company_name = Utils.getString(StoreET)
            name = Utils.getString(ceoET)
            val getPhone: String = Utils.getString(phoneET)
            company_num = Utils.getString(companynumET)
            val address = Utils.getString(addressTV)
            val address_detail = Utils.getString(addressDetailET)

            if(email == "" || email == null || email.isEmpty()) {
                geterror = "이메일을 입력해주세요"

                dlgView( geterror)

            } else if (!Utils.isValidEmail(email)) {
                geterror = "이메일을 확인해주세요"

                dlgView( geterror)
            } else if (company_num == "" || company_num == null || company_num.isEmpty()) {
                geterror = "사업자등록번호를 입력해주세요"

                dlgView(geterror)
            }

           else if (getPW == "" || getPW == null || getPW.isEmpty()) {
                geterror = "비밀번호를 입력해주세요"

                dlgView( geterror)
            }

            else if (getPW != getPW2) {
                geterror = "비밀번호가 일치하지 않습니다"

                dlgView( geterror)
            }

            else if (company_name == "" || company_name == null || company_name.isEmpty()) {
                geterror = "상호명을 입력해주세요"

                dlgView( geterror)
            }

            else if (name == "" || name == null || name.isEmpty()) {
                geterror = "대표자 성명을 입력해주세요"

                dlgView( geterror)
            }
            else if (address == "" || address == null || address.isEmpty()) {
                geterror = "사업장 주소를 입력해주세요"

                dlgView( geterror)
            }
            else if (address_detail == "" || address_detail == null || address_detail.isEmpty()) {
                geterror = "사업장 상세 주소를 입력해주세요"

                dlgView( geterror)
            }

//            else if (getPhone == "" || getPhone == null || getPhone.isEmpty()) {
//                geterror = "휴대폰번호를 입력해주세요"
//
//                dlgView( geterror)
//            }

            else if (allCB.isChecked != true) {
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }

            else if (serviceCK.isChecked != true) {
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }

            else if (soloCK.isChecked != true) {
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }

            else if (agree3CK.isChecked != true) {
                geterror = "이용약관에 동의해주세요"

                dlgView( geterror)
            }

//            else if(getPhone != ori_phone) {
//                geterror = "휴대폰인증을 해주세요"
//
//                dlgView( geterror)
//            }

//            else if(sms_code != Utils.getString(smsCodeET)) {
//                geterror = "인증번호를 다시 확인해주세요"
//
//                dlgView( geterror)
//            }
 else{
                passwd = getPW

                join()

            }



        }

        finishLL.setOnClickListener {
            finish()
        }



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

    fun sendSMS(phone: String) {
        val params = RequestParams()
        params.put("phone", phone)

        JoinAction.send_sms(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        sms_code = Utils.getString(response, "authNumber")
                        ori_phone = phone

                        sendSMSTV.text = "인증번호 재전송"
                        smsCodeLL.visibility = View.VISIBLE

                        Toast.makeText(context, Utils.getString(response, "message"), Toast.LENGTH_LONG).show();

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

            override fun onFailure( statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
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

    fun join() {
        val params = RequestParams()
        params.put("name", name)
        params.put("email",email )
        params.put("company_name", company_name)
        params.put("address", Utils.getString(addressTV))
        params.put("address_detail", Utils.getString(addressDetailET))
        params.put("lat", lat)
        params.put("lng", lng)
        params.put("company_num",company_num)
        params.put("passwd", passwd)
//        params.put("phone", ori_phone)
        params.put("phone", Utils.getString(phoneET))
        params.put("school_id", school_id)
        params.put("member_type", "3")

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

                        var intent = Intent(context, DlgJoinActivity::class.java);
                        intent.putExtra("type", "company_join_ok")
                        startActivityForResult(intent, JOIN_OK)

                    } else {

                        dlgView(Utils.getString(response, "message"))

//                        Toast.makeText(context, response!!.getString("message"), Toast.LENGTH_LONG).show()

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

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseString: String?,
                throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                // System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                SELECT_SCHOOL -> {
                    school_id = data!!.getIntExtra("school_id", -1)
                    schoolTV.text = data!!.getStringExtra("schoolname")
                }

                ADDRESS -> {

                    var address = data!!.getStringExtra("address")

                    addressTV.text = address

                    if (address != null && "" != address && address.length > 0) {
                        find_location(address)
                    }

                }

                JOIN_ERROR -> {

                }

                JOIN_OK -> {
                    login(email, passwd)
                }

            }
        }

    }


    private fun find_location(address: String) {
        val params = RequestParams()
        params.put("address", address)

        AddressAction.search_map(address, 1, 1, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    if (response!!.getJSONObject("meta") != null) {
                        val region = response.getJSONObject("meta")
                        val list = response.getJSONArray("documents")

                        if (list.length() > 0) {
                            val obj = list.get(0) as JSONObject

                            println("obj : " + obj)
                            println("list.get(0)  : " + list.get(0) )

                            val address = obj.getJSONObject("road_address")
                            println("address  : " + address)

                            lng = Utils.getString(address, "x")
                            lat = Utils.getString(address, "y")

                            addressDetailET.requestFocus()

                        }
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
                if (progressDialog != null) {
                    Utils.alert(context, "조회중 장애가 발생하였습니다.")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseString: String?,
                throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                //                System.out.println(responseString);

                throwable.printStackTrace()
                error()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable,
                errorResponse: JSONObject?
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
                throwable.printStackTrace()
                error()
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


    //다이얼로그
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
//
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

                        PrefUtils.setPreference(context, "current_school_id", school_id)
                        PrefUtils.setPreference(context, "current_school_image_uri", school_image_uri)

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

}
