package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
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
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MyPagePostingStorageFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null
    lateinit var activity: MainActivity
    var member_id = -1
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterMy: MyPostingAdapter
    var tabType = 1

    var taptype = 1

    lateinit var free2TV:TextView
    lateinit var info2TV:TextView
    lateinit var study2TV:TextView
    lateinit var class2TV:TextView
    lateinit var meeting2TV:TextView
    lateinit var coupon2TV:TextView


    lateinit var storageGV: GridView
    lateinit var free2V:View
    lateinit var info2V:View
    lateinit var study2V:View
    lateinit var class2V:View
    lateinit var meeting2V:View
    lateinit var coupon2V:View


    lateinit var free2RL: RelativeLayout
    lateinit var info2RL: RelativeLayout
    lateinit var study2RL: RelativeLayout
    lateinit var class2RL: RelativeLayout
    lateinit var meeting2RL: RelativeLayout
    lateinit var coupon2RL: RelativeLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        progressDialog = ProgressDialog(context)

        return inflater.inflate(R.layout.fra_my_page_posting_storage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        free2TV = view.findViewById(R.id.free2TX)
        info2TV = view.findViewById(R.id.info2TX)
        study2TV = view.findViewById(R.id.Study2TX)
        class2TV = view.findViewById(R.id.class2TX)
        meeting2TV = view.findViewById(R.id.Miting2TX)
        coupon2TV = view.findViewById(R.id.Coupon2TX)



        free2V = view.findViewById(R.id.free2V)
        info2V = view.findViewById(R.id.info2V)
        study2V = view.findViewById(R.id.Study2V)
        class2V = view.findViewById(R.id.class2V)
        meeting2V = view.findViewById(R.id.miting2V)
        coupon2V = view.findViewById(R.id.coupon2V)


        free2RL = view.findViewById(R.id.free2RL)
        info2RL = view.findViewById(R.id.info2RL)
        study2RL = view.findViewById(R.id.study2RL)
        class2RL = view.findViewById(R.id.class2RL)
        meeting2RL = view.findViewById(R.id.meeting2RL)
        coupon2RL = view.findViewById(R.id.coupon2RL)


        storageGV = view.findViewById(R.id.storageGV)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity


        free2RL.setOnClickListener {
            adapterData.clear()
            tabType = 1;
            loadData(taptype)
            setMenuTabView()

        }

        info2RL.setOnClickListener {
            adapterData.clear()
            tabType = 2;
            loadData(taptype)
            setMenuTabView()

        }

        study2RL.setOnClickListener {
            adapterData.clear()
            tabType = 3;
            loadData(taptype)
            setMenuTabView()



        }

        class2RL.setOnClickListener {
            adapterData.clear()
            tabType = 4;
            loadData(taptype)
            setMenuTabView()

        }

        meeting2RL.setOnClickListener {
            adapterData.clear()
            tabType = 5;
            loadData(1)
            setMenuTabView()

        }

        coupon2RL.setOnClickListener {
            adapterData.clear()
            tabType = 6;
            loadData(taptype)
            setMenuTabView()
        }





        adapterMy = MyPostingAdapter(activity, R.layout.item_storage, adapterData)
        storageGV.adapter = adapterMy
        member_id = PrefUtils.getIntPreference(context, "member_id")

        storageGV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            try {
                val Posting = adapterData[position].getJSONObject("Posting")

                //                    Intent intent = new Intent(context, _StoreDetailActivity.class);
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", Utils.getString(Posting, "id"))
                startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }



    }

    fun loadData(tab: Int) {
        val params = RequestParams()
        member_id = PrefUtils.getIntPreference(context, "member_id")
        params.put("member_id", member_id)
        params.put("tab", tab)
        params.put("type", tabType)

        MemberAction.my_page_index(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {
                        val data = response.getJSONArray("list")
                        for (i in 0..data.length() - 1) {

                            println("data[i] : " + data[i])

                            adapterData.add(data[i] as JSONObject)

                        }
                        adapterMy.notifyDataSetChanged()

                    } else {
                        Toast.makeText(context, "일치하는 회원이 존재하지 않습니다.", Toast.LENGTH_LONG).show()
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
    fun setMenuTabView() {
        free2TV.setTextColor(Color.parseColor("#A19F9B"))
        info2TV.setTextColor(Color.parseColor("#A19F9B"))
        study2TV.setTextColor(Color.parseColor("#A19F9B"))
        class2TV.setTextColor(Color.parseColor("#A19F9B"))
        meeting2TV.setTextColor(Color.parseColor("#A19F9B"))
        coupon2TV.setTextColor(Color.parseColor("#A19F9B"))

        free2V.visibility = View.INVISIBLE
        info2V.visibility = View.INVISIBLE
        study2V.visibility = View.INVISIBLE
        class2V.visibility = View.INVISIBLE
        meeting2V.visibility = View.INVISIBLE
        coupon2V.visibility = View.INVISIBLE


        if(tabType == 1) {
            free2TV.setTextColor(Color.parseColor("#01b4ec"))
            free2V.visibility = View.VISIBLE
        } else if (tabType == 2) {
            info2V.visibility = View.VISIBLE
            info2TV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 3) {
            study2V.visibility = View.VISIBLE
            study2TV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 4) {
            class2V.visibility = View.VISIBLE
            class2TV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 5) {
            meeting2V.visibility = View.VISIBLE
            meeting2TV.setTextColor(Color.parseColor("#01b4ec"))
        } else if (tabType == 6) {
            coupon2V.visibility = View.VISIBLE
            coupon2TV.setTextColor(Color.parseColor("#01b4ec"))
        }

    }

}


