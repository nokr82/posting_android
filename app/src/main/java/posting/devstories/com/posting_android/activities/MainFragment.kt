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
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.GridView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MainFragment : Fragment(), AbsListView.OnScrollListener {

    private var progressDialog: ProgressDialog? = null

    lateinit var myContext: Context

    lateinit var activity: MainActivity
    var tabType = 1;
    var member_id = -1
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterMain: PostAdapter

    lateinit var gideGV: GridView

    var keyword = ""
    var type = 1

    private var page = 1
    private var totalPage = 0
    private val visibleThreshold = 10
    private var userScrolled = false
    private var lastItemVisibleFlag = false
    private var lastcount = 0
    private var totalItemCountScroll = 0

    internal var savePostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var posting_id = intent.getStringExtra("posting_id")
                var count = intent.getIntExtra("count", 1)

                for (i in 0..(adapterData.size - 1)) {
                    var data = adapterData[i]
                    var posting = data.getJSONObject("Posting")

                    if (Utils.getString(posting, "id") == posting_id) {

                        if(Utils.getInt(posting, "leftCount") != 9999) {
                            var cnt = Utils.getInt(posting, "count") - count

                            if(cnt < 1) {
                                adapterData.removeAt(i)
                            } else {
                                posting.put("leftCount", cnt)
                            }

                        }
                        break
                    }

                }

                adapterMain.notifyDataSetChanged()

            }
        }
    }

    internal var delPostingReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                // type = intent.getIntExtra("type", 1)
                // loadData(type)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.myContext = container!!.context

        progressDialog = ProgressDialog(myContext)

        return inflater.inflate(R.layout.fra_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gideGV = view.findViewById(R.id.gideGV)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity
        tabType = activity.tabType


        val filter1 = IntentFilter("SAVE_POSTING")
        activity.registerReceiver(savePostingReceiver, filter1)


        val filter2 = IntentFilter("DEL_POSTING")
        activity.registerReceiver(delPostingReceiver, filter2)

        adapterMain = PostAdapter(activity, R.layout.item_post, adapterData)
        gideGV.adapter = adapterMain
        member_id = PrefUtils.getIntPreference(myContext, "member_id")
        gideGV.setOnScrollListener(this)
        gideGV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            try {
                val Posting = adapterData[position].getJSONObject("Posting")

                //                    Intent intent = new Intent(myContext, _StoreDetailActivity.class);

                val type = Utils.getString(Posting, "type")

//                if("3" == type || "4" == type || "5" == type) {
                    // 채팅 화면
//                    val intent = Intent(myContext, MatchInfoActivity::class.java)
//                    intent.putExtra("posting_id", Utils.getString(Posting, "id"))
//                    startActivity(intent)
//                } else {
                    val intent = Intent(myContext, DetailActivity::class.java)
                    intent.putExtra("id", Utils.getString(Posting, "id"))
                    startActivity(intent)
//                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    fun reloadData(type:Int) {
        page = 1
        loadData(type)
    }

    fun loadData(type: Int) {
        val params = RequestParams()
        params.put("member_id", PrefUtils.getIntPreference(myContext, "member_id"))
        params.put("current_school_id", PrefUtils.getIntPreference(myContext, "current_school_id"))
        params.put("type", type)
        params.put("keyword", keyword)
        params.put("page", page)

//        var postFragment:PostFragment = PostFragment()
//
//        println("keyword : " + postFragment.keyword)
//        println("type : " + type)

        PostingAction.view(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")


                    if ("ok" == result) {
                        val data = response.getJSONArray("list")

                        totalPage = response.getInt("totalPage");
                        page = response.getInt("page");

                        if(page == 1) {

                            adapterData.clear()
                            adapterMain.notifyDataSetChanged()

                        }

                        for (i in 0..(data.length() - 1)) {

                            adapterData.add(data[i] as JSONObject)

                        }

                        adapterMain.notifyDataSetChanged()


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

    override fun onScroll(p0: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

        if (userScrolled && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold && page < totalPage && totalPage > 0) {
            if (totalPage > page) {
                //page++;
                //threemeals_store_index1();
            }
        }

        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem)
        // + 현재 화면에 보이는 리스트 아이템의갯수(visibleItemCount)가
        // 리스트 전체의 갯수(totalOtemCount)-1 보다 크거나 같을때
        lastItemVisibleFlag = totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount
        totalItemCountScroll = totalItemCount

    }

    override fun onScrollStateChanged(p0: AbsListView?, scrollState: Int) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            userScrolled = true
        } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            userScrolled = false

            //화면이 바닥에 닿았을때
            if (totalPage > page) {
                page++
                lastcount = totalItemCountScroll

                loadData(type)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

        try {
            if (savePostingReceiver != null) {
                myContext!!.unregisterReceiver(savePostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

        try {
            if (delPostingReceiver != null) {
                myContext!!.unregisterReceiver(delPostingReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

    }

}
