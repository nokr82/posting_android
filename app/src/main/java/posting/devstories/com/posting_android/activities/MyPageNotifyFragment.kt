package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.AlarmAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.AlarmAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

class MyPageNotifyFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null

    private lateinit var listLV:ListView
    private lateinit var adapter: AlarmAdapter
    private var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()

    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context
        progressDialog = ProgressDialog(myContext, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        return inflater.inflate(R.layout.fra_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listLV = view.findViewById(R.id.listLV)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = AlarmAdapter(myContext, R.layout.item_alram, adapterData)
        listLV.adapter = adapter
        listLV.setOnItemClickListener { parent, view, position, id ->

            var data = adapterData.get(position)
            var posting= data.getJSONObject("Posting")

            val chatting_yn = Utils.getString(posting, "chatting_yn")
            val type = Utils.getInt(posting, "type")
            val posting_id = Utils.getString(posting, "id")

            if (type == 3 || type == 4 || type == 5 || chatting_yn == "Y") {

                val intent = Intent(myContext, MatchInfoActivity::class.java)
                intent.putExtra("posting_id", posting_id)
                startActivity(intent)
            } else {
//            var intent = Intent(myContext, DetailActivity::class.java)
                var intent = Intent(myContext, DlgDetailActivity::class.java)
                intent.putExtra("id", posting_id)
                startActivity(intent)
            }

        }

        loadData();

    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))
        params.put("page", page)

        AlarmAction.alarm_list(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if(page == 1) {
                        adapterData.clear()
                        adapter.notifyDataSetChanged()
                    }

                    if ("ok" == result) {
                        val alarm_count = Utils.getInt(response, "alarm_count")

                        val data = response.getJSONArray("list")

                        for (i in 0..(data.length() - 1)) {
                            adapterData.add(data[i] as JSONObject)
                        }

                        adapter.notifyDataSetChanged()

                        var intent = Intent()
                        intent.action = "UPDATE_ALARM_CNT"
                        intent.putExtra("alarm_count", alarm_count)
                        myContext.sendBroadcast(intent)

                    } else if("empty".equals(result)) {

                    } else {

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseString: String?) {

                // System.out.println(responseString);
            }

            private fun error() {
                Utils.alert(myContext, "조회중 장애가 발생하였습니다.")
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


