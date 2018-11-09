package posting.devstories.com.posting_android.activities


import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.coupon_activity.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.R.id.*
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils
import java.text.SimpleDateFormat
import java.util.*

class CouponTextActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null
    var  most =arrayOf("수량","1","3","5","10","20","∞")
//    var day = arrayOf("기간","1일","5일","7일","10일","30일","60일")
    var member_id = -1
    val type = "6"
    var contents = ""
    var getmost = ""
    var getday=""
    var geterror = ""
    var coupon_type = ""
    var uses_start_date = ""
    var uses_end_date  = ""
    var menu_name = ""
    var sale_per = ""
    var sale_price = ""
    var cal = Calendar.getInstance()

    lateinit var adpater: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coupon_activity)
        this.context = this
        progressDialog = ProgressDialog(context)
        viewcoupon()
        intent = getIntent()
        print("----------getmost"+getmost)


        adpater = ArrayAdapter<String>(this, R.layout.spinner_item, most)
        most4SP.adapter = adpater

        member_id =  PrefUtils.getIntPreference(context,"member_id")







        store2LL.setOnClickListener {
            viewcoupon()
            storeLL.visibility = View.VISIBLE
            coupon_type = "3"
            startdate3TV.setOnClickListener {
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yy.MM.dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                    startdate3TV.text = sdf.format(cal.time)
                }
                DatePickerDialog(context, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            enddate3TV.setOnClickListener {
                val dateSetListener2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yy.MM.dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                    enddate3TV.text = sdf.format(cal.time)
                }
                DatePickerDialog(context, dateSetListener2,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        }
        coupon2LL.setOnClickListener {
            viewcoupon()
            couponLL.visibility = View.VISIBLE
            coupon_type = "2"
            startdate2TV.setOnClickListener {
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yy.MM.dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                    startdate2TV.text = sdf.format(cal.time)
                }
                DatePickerDialog(context, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            enddate2TV.setOnClickListener {
                val dateSetListener2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yy.MM.dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                    enddate2TV.text = sdf.format(cal.time)
                }
                DatePickerDialog(context, dateSetListener2,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
        sale2LL.setOnClickListener {
            viewcoupon()
            saleLL.visibility = View.VISIBLE
            coupon_type = "1"
            startdateTV.setOnClickListener {
                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yy.MM.dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                    startdateTV.text = sdf.format(cal.time)
                }
                DatePickerDialog(context, dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            enddateTV.setOnClickListener {
                val dateSetListener2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "yy.MM.dd" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                    enddateTV.text = sdf.format(cal.time)
                }
                DatePickerDialog(context, dateSetListener2,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }



        }
        nextTX.setOnClickListener {
            getmost = most4SP.selectedItem.toString()
            if (getmost.equals("수량")){
                Toast.makeText(context,"수량을 선택해주세요",Toast.LENGTH_SHORT).show()
            }else if (coupon_type.equals("1")) {
                menu_name = Utils.getString(titleET)
                sale_per = Utils.getString(saleET)
                uses_start_date = Utils.getString(startdateTV)
                uses_end_date = Utils.getString(enddateTV)
                contents = Utils.getString(content2ET)
                write()
            }else if (coupon_type.equals("2")){
                menu_name = Utils.getString(title2ET)
                sale_per = Utils.getString(saleET)
                uses_start_date = Utils.getString(startdate2TV)
                uses_end_date = Utils.getString(enddate2TV)
                contents = Utils.getString(content3ET)
                write()
            }else if (coupon_type.equals("3")){
                menu_name = Utils.getString(title3ET)
                sale_price = Utils.getString(moneyET)
                uses_start_date = Utils.getString(startdate3TV)
                uses_end_date = Utils.getString(enddate3TV)
                contents = Utils.getString(content4ET)
                write()
            }

        }




    }



    fun viewcoupon(){
        saleLL.visibility = View.GONE
        couponLL.visibility = View.GONE
        storeLL.visibility = View.GONE
    }





    fun write(){
        getmost = most4SP.selectedItem.toString()
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("current_school_id", PrefUtils.getIntPreference(context, "current_school_id"))
        params.put("type", type)
        params.put("contents", contents)
        params.put("count", getmost)
        params.put("coupon_type", coupon_type)
        params.put("uses_start_date", uses_start_date)
        params.put("uses_end_date", uses_end_date)
        params.put("menu_name", menu_name)
        params.put("sale_per", sale_per)
        params.put("sale_price", sale_price)


        PostingAction.write(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")
                    if ("ok" == result) {
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
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }


    }


}
