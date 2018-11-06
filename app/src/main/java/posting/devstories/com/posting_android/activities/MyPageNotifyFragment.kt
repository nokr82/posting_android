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

    private var progressDialog: ProgressDialog? = null

    private lateinit var listLV:ListView
    private lateinit var adapter: AlarmAdapter
    private var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()

    private lateinit var mContext: Context

    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = this!!.context!!
        progressDialog = ProgressDialog(context)

        return inflater.inflate(R.layout.fra_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listLV = view.findViewById(R.id.listLV)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = AlarmAdapter(mContext, R.layout.item_alram, adapterData)
        listLV.adapter = adapter
        listLV.setOnItemClickListener { parent, view, position, id ->

            var data = adapterData.get(position)
            var posting= data.getJSONObject("Posting")

            var intent = Intent(mContext, DetailActivity::class.java)
            intent.putExtra("id", Utils.getString(posting, "id"))
            startActivity(intent)

        }

        loadData();

    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(context, "member_id"))
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
                        mContext.sendBroadcast(intent)

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


