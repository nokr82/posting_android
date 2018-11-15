package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.MyPostingAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MyPageParentFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null

    lateinit var activity:MainActivity
    lateinit var gideGV:GridView

    var member_id = -1
    open var tab = 1

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterMy: MyPostingAdapter

    var totalPage = 0
    var page = 1

    internal var delPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var type:Int = intent.getIntExtra("type", 1)
                loadData(type)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        var view = super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fra_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(myContext)

        member_id = PrefUtils.getIntPreference(myContext, "member_id")

        gideGV = view.findViewById(R.id.gideGV)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity

        val filter2 = IntentFilter("DEL_POSTING")
        activity.registerReceiver(delPostingReceiver, filter2)

        adapterMy = MyPostingAdapter(activity, R.layout.item_storage, adapterData)
        gideGV.adapter = adapterMy
        member_id = PrefUtils.getIntPreference(myContext, "member_id")

        gideGV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            try {

                val Posting = adapterData[position].getJSONObject("Posting")
                val type = Utils.getInt(Posting,"type")

                if (tab ==1) {

                    if (type == 3 || type == 4 || type == 5) {

                        val intent = Intent(myContext, MatchInfoActivity::class.java)
                        intent.putExtra("posting_id", Utils.getString(Posting, "id"))
                        startActivity(intent)

                    } else {

                        val intent = Intent(myContext, DetailActivity::class.java)
                        intent.putExtra("id", Utils.getString(Posting, "id"))
                        startActivity(intent)

                    }

                } else {

                    if (type == 3 || type == 4 || type == 5) {

                        val intent = Intent(myContext, MatchInfoActivity::class.java)
                        intent.putExtra("posting_id", Utils.getString(Posting, "id"))
                        startActivity(intent)

                    } else {

                        val PostingSave = adapterData[position].getJSONObject("PostingSave")

                        val intent = Intent(myContext, DetailActivity::class.java)
                        intent.putExtra("id", Utils.getString(Posting, "id"))
                        intent.putExtra("save_id", Utils.getString(PostingSave, "id"))
                        intent.putExtra("taptype",tab)
                        startActivity(intent)

                    }

                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    fun loadData(type: Int) {
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("tab", tab)
        params.put("type", type)

        MemberAction.my_page_index(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        page = response.getInt("page")
                        totalPage = response.getInt("totalPage")

                        if(page == 1) {
                            adapterData.clear()
                            adapterMy.notifyDataSetChanged()
                        }

                        var member = response.getJSONObject("member");

                        val data = response.getJSONArray("list")

                        for (i in 0..(data.length() - 1)) {

                            adapterData.add(data[i] as JSONObject)

                        }
                        adapterMy.notifyDataSetChanged()

                    } else {
                        Toast.makeText(myContext, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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


    override fun onDestroy() {
        super.onDestroy()

        try {
            if (delPostingReceiver != null) {
                myContext!!.unregisterReceiver(delPostingReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }

    }

}
