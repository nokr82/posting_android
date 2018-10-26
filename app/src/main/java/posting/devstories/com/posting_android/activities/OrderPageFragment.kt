package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.Toast
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.OrderAdapter
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.adapter.ReviewAdapter
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class OrderPageFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null
    lateinit var activity: MainActivity
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterOrder: OrderAdapter
    lateinit var adapterReview: ReviewAdapter

    var member_id = -1
    lateinit var reviewLL: LinearLayout
    lateinit var reviewV:View
    lateinit var couponLL: LinearLayout
    lateinit var couponV:View
    lateinit var couponGV:ExpandableHeightGridView
    var clicktype = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context
        if (null != ctx) {
            doSomethingWithContext(ctx)
        }


        return inflater.inflate(R.layout.fra_orderpg, container, false)
    }
    fun doSomethingWithContext(context: Context) {
        // TODO: Actually do something with the context
        this.ctx = context
        progressDialog = ProgressDialog(ctx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviewLL = view.findViewById(R.id.reviewLL)
        reviewV = view.findViewById(R.id.reviewV)
        couponLL = view.findViewById(R.id.couponLL)
        couponV = view.findViewById(R.id.couponV)
        couponGV = view.findViewById(R.id.couponGV)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity() as MainActivity
        member_id = PrefUtils.getIntPreference(context, "member_id")

        reviewLL.setOnClickListener {
            adapterData.clear()
            reviewV.visibility = View.VISIBLE
            couponV.visibility = View.INVISIBLE
            adapterReview = ReviewAdapter(activity, R.layout.item_post,adapterData)
            couponGV.adapter = adapterReview
            clicktype = 2
            loadData()




        }


        couponGV.isExpanded = true
        couponLL.setOnClickListener {
            adapterData.clear()
            couponV.visibility = View.VISIBLE
            reviewV.visibility = View.INVISIBLE
            adapterOrder = OrderAdapter(activity, R.layout.item_post,adapterData)
            couponGV.adapter = adapterOrder
            clicktype = 1
          loadData()
            couponGV.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
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

    }

    fun loadData() {
        val params = RequestParams()
        params.put("company_member_id", member_id)
        params.put("member_id",member_id)

        MemberAction.company_page(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        if (clicktype ==1){

                        val data = response.getJSONArray("postList")

                        for (i in 0..data.length() - 1) {

                            println("data[i] : " + data[i])

                            adapterData.add(data[i] as JSONObject)

                        }
                        adapterOrder.notifyDataSetChanged()
                        }else{
                            val data = response.getJSONArray("reviewList")
                            for (i in 0..data.length() - 1) {

                                println("data[i] : " + data[i])

                                adapterData.add(data[i] as JSONObject)

                            }
                            adapterReview.notifyDataSetChanged()
                        }

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

}
