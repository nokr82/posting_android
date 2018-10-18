package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
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
import posting.devstories.com.posting_android.Actions.JoinAction
import posting.devstories.com.posting_android.Actions.LoginAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class OrderJoinActivity : RootActivity() {

    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    val SELECT_SCHOOL = 101
    val ADDRESS = 102

    var school_id = -1
    var sms_code = ""

    var company_num = ""
    var company_name = ""
    var ori_phone = ""
    var name = ""
    var passwd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderjoin)

        this.context = this
        progressDialog = ProgressDialog(context)

        allCB.setOnClickListener {
            var checked = allCB.isChecked
            if (checked) {
                serviceCB.isChecked = true
                soloCB.isChecked = true
            } else {
                serviceCB.isChecked = false
                soloCB.isChecked = false
            }
        }

        serviceCB.setOnCheckedChangeListener { compoundButton, b ->

            val check = soloCB.isChecked

            allCB.isChecked = b && check

        }

        soloCB.setOnCheckedChangeListener { compoundButton, b ->

            val check = serviceCB.isChecked

            allCB.isChecked = b && check

        }

        sendSMSTV.setOnClickListener {
            val getPhone: String = Utils.getString(phoneET)

            if (getPhone == "" || getPhone == null || getPhone.isEmpty()) {
                Toast.makeText(context, "휴대폰번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {

                sendSMS(getPhone);

            }
        }

        schoolTV.setOnClickListener {
            val intent = Intent(this, SchoolActivity::class.java)
            intent.putExtra("member_type", "3")
            startActivityForResult(intent, SELECT_SCHOOL)
        }

        addressTV.setOnClickListener {
            var intent = Intent(context, AddressActivity::class.java);
            startActivityForResult(intent, ADDRESS)
        }

        PostingStartTX.setOnClickListener {

            company_num = Utils.getString(OfficeET)
            val getPW: String = Utils.getString(pwET)
            val getPW2: String = Utils.getString(pw2ET)
            company_name = Utils.getString(StoreET)
            name = Utils.getString(ceoET)
            val getPhone: String = Utils.getString(phoneET)
            val addressDetail: String = Utils.getString(addressDetailET)

            if (company_num == "" || company_num == null || company_num.isEmpty()) {
                Toast.makeText(context, "사업자등록번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getPW == "" || getPW == null || getPW.isEmpty()) {
                Toast.makeText(context, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getPW != getPW2) {
                Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (company_name == "" || company_name == null || company_name.isEmpty()) {
                Toast.makeText(context, "상호명을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name == "" || name == null || name.isEmpty()) {
                Toast.makeText(context, "대표자 성명을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (getPhone == "" || getPhone == null || getPhone.isEmpty()) {
                Toast.makeText(context, "휴대폰번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (allCB.isChecked != true) {
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (serviceCB.isChecked != true) {
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (soloCB.isChecked != true) {
                Toast.makeText(context, "이용약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(getPhone != ori_phone) {
                Toast.makeText(context, "휴대폰 인증을 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(sms_code != Utils.getString(smsCodeET)) {
                Toast.makeText(context, "인증번호를 다시 확인해주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            passwd = getPW

            join()

        }

        finishLL.setOnClickListener {
            finish()
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
        params.put("company_num", company_num)
        params.put("company_name", company_name)
        params.put("address", Utils.getString(addressTV))
        params.put("address_detail", Utils.getString(addressDetailET))
        params.put("passwd", passwd)
        params.put("phone", ori_phone)
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

                        Toast.makeText(context, "회원가입이 완료되었습니다. 로그인 후 이용하세요", Toast.LENGTH_LONG).show()

                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {

                        Toast.makeText(context, response!!.getString("message"), Toast.LENGTH_LONG).show()

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

                }

            }
        }

    }

}
