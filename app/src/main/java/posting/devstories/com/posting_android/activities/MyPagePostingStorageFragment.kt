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
import android.widget.*
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

open class MyPagePostingStorageFragment : Fragment() {

    lateinit var myContext: Context

    private var progressDialog: ProgressDialog? = null
    lateinit var activity: MainActivity
    var member_id = -1
    var tabType = 1
    var tab = 1

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterMy: MyPostingAdapter

    var totalPage = 0
    var page = 1

    lateinit var storageGV: GridView

    internal var writePostReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {

                if (tab == 1) {
                    page = 1
                    tab = 1
                    loadData()
                }

            }

        }
    }

    internal var savePostReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {
                page = 1
                tab = 2
                loadData()
            }

        }
    }

    internal var delPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                tab = 1
                page = 1
                loadData()
            }
        }
    }

    internal var saveDelPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                tab = 2
                page = 1
                loadData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        val filter3 = IntentFilter("SET_VIEW")
        getActivity()!!.registerReceiver(writePostReceiver, filter3)

        val filter1 = IntentFilter("SAVE_POSTING")
        getActivity()!!.registerReceiver(savePostReceiver, filter1)

        val filter2 = IntentFilter("DEL_POSTING")
        getActivity()!!.registerReceiver(delPostingReceiver, filter2)

        val filter4 = IntentFilter("SAVE_DEL_POSTING")
        getActivity()!!.registerReceiver(saveDelPostingReceiver, filter4)

        val filter5 = IntentFilter("WRITE_POST")
        getActivity()!!.registerReceiver(writePostReceiver, filter5)

        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)

        return inflater.inflate(R.layout.fra_new_my_page_posting_storage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        member_id = PrefUtils.getIntPreference(context, "member_id")

        storageGV = view.findViewById(R.id.storageGV)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity

        adapterMy = MyPostingAdapter(activity, R.layout.item_storage, adapterData)
        storageGV.adapter = adapterMy

        storageGV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            try {

                val Posting = adapterData[position].getJSONObject("Posting")
                val type = Utils.getInt(Posting, "type")
                val chatting_yn = Utils.getString(Posting, "chatting_yn")

                if (tab == 1) {

                    if (chatting_yn == "Y" || type == 3 || type == 4 || type == 5) {

                        val intent = Intent(myContext, MatchInfoActivity::class.java)
                        intent.putExtra("posting_id", Utils.getString(Posting, "id"))
                        startActivity(intent)

                    } else {

                        val intent = Intent(myContext, DlgDetailActivity::class.java)
                        intent.putExtra("id", Utils.getString(Posting, "id"))
                        startActivity(intent)

                    }

                } else {

                    val PostingSave = adapterData[position].getJSONObject("PostingSave")

                    if (chatting_yn == "Y" || type == 3 || type == 4 || type == 5) {

                        val intent = Intent(myContext, MatchInfoActivity::class.java)
                        intent.putExtra("posting_id", Utils.getString(Posting, "id"))
                        intent.putExtra("save_id", Utils.getInt(PostingSave, "id"))
                        startActivity(intent)

                    } else {

                        val intent = Intent(myContext, DlgDetailActivity::class.java)
                        intent.putExtra("id", Utils.getString(Posting, "id"))
                        intent.putExtra("save_id", Utils.getInt(PostingSave, "id"))
                        intent.putExtra("taptype", tab)
                        startActivity(intent)

                    }

                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        loadData()

    }

    fun loadData() {
        val params = RequestParams()
        params.put("member_id", member_id)
        params.put("tab", tab)
        params.put("type", -1)

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

                        if (page == 1) {
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
            if (writePostReceiver != null) {
                getActivity()!!.unregisterReceiver(writePostReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        try {
            if (savePostReceiver != null) {
                getActivity()!!.unregisterReceiver(savePostReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        try {
            if (delPostingReceiver != null) {
                getActivity()!!.unregisterReceiver(delPostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        try {
            if (saveDelPostingReceiver != null) {
                getActivity()!!.unregisterReceiver(saveDelPostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        try {
            if (writePostReceiver != null) {
                getActivity()!!.unregisterReceiver(writePostReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

}


