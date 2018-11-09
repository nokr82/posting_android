package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.Toast
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_schooljoin.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.LoginAction
import posting.devstories.com.posting_android.Actions.SchoolAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.SchoolAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

class SchoolActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    private lateinit var adapter: SchoolAdapter

    // 2-학생 / 3-사업자
    var member_type = "2"
    var school_id = -1
    var schoolname = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schooljoin)


        intent = getIntent()
        member_type = intent.getStringExtra("member_type")





        nextLL.setOnClickListener {

            if (school_id < 1) {

                Toast.makeText(this,"학교를 선택해주세요",Toast.LENGTH_SHORT).show()


            } else {

                if ("2" == member_type) {//2가 학생용
                    val intent = Intent(this, StudentJoinActivity::class.java)
                    intent.putExtra("school_id",school_id)
                    intent.putExtra("schoolname",schoolname)
                    startActivity(intent)
                } else {//3이면 사업자
                    var intent = Intent()
                    intent.putExtra("school_id", school_id)
                    intent.putExtra("schoolname",schoolname)
                    setResult(RESULT_OK, intent)
                    finish()
                }

            }

        }

        finishLL.setOnClickListener {
            finish()
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

//        schoolET.setOnEditorActionListener { textView, i, keyEvent ->
//
//            when (i) {
//                EditorInfo.IME_ACTION_SEARCH -> {
//
//
//                    var keyword = Utils.getString(schoolET)
//
//                    School(keyword)
//
//
//                }
//            }
//            return@setOnEditorActionListener true
//
//        }

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
                    val list = response!!.getJSONArray("list")

                    println(response)

                    if("ok" == result) {

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

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }


    }


}
