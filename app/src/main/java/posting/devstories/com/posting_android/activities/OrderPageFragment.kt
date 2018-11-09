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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fra_orderpg.*
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.OrderAdapter
import posting.devstories.com.posting_android.adapter.ReviewAdapter
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class OrderPageFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null
    lateinit var activity: MainActivity
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterOrder: OrderAdapter
    lateinit var adapterReview: ReviewAdapter


    var member_type = ""
    var member_id = -1
    lateinit var reviewLL: LinearLayout
    lateinit var reviewV:View
    lateinit var couponLL: LinearLayout
    lateinit var couponV:View
    lateinit var couponGV:ExpandableHeightGridView
    lateinit var gpsLL:LinearLayout
    lateinit var storeInfoTV:TextView

    var clicktype = 1

    var name = ""
    var lng = 0.0
    var lat = 0.0

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
        gpsLL = view.findViewById(R.id.gpsLL)
        storeInfoTV = view.findViewById(R.id.storeInfoTV)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity() as MainActivity
        member_id = PrefUtils.getIntPreference(context, "member_id")
        member_type = PrefUtils.getStringPreference(context,"member_type")

        adapterData.clear()
       menuLL.setOnClickListener {
           val intent = Intent(context, MyPageActivity::class.java)
           startActivity(intent)
       }


        gpsLL.setOnClickListener {
            val intent = Intent(context, OrderMapActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        adapterOrder = OrderAdapter(activity, R.layout.item_post,adapterData)
        adapterReview = ReviewAdapter(activity, R.layout.item_post,adapterData)

        couponGV.adapter = adapterOrder
        couponGV.isExpanded = true


        reviewLL.setOnClickListener {

            if (member_type.equals("3")){
                review2LL.visibility = View.GONE
            }else{
                review2LL.visibility = View.VISIBLE
            }
            adapterData.clear()
            reviewV.visibility = View.VISIBLE
            couponV.visibility = View.INVISIBLE
            couponGV.adapter = adapterReview
            clicktype = 2
            loadData()

        }

        couponGV.isExpanded = true
        couponLL.setOnClickListener {
            review2LL.visibility = View.GONE
            adapterData.clear()
            couponV.visibility = View.VISIBLE
            reviewV.visibility = View.INVISIBLE
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
        loadData()

    }

    fun loadData() {
        val params = RequestParams()
        params.put("company_member_id", member_id)
        params.put("member_id",member_id)
        params.put("type",clicktype)

        MemberAction.company_page(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {
                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        val member = response.getJSONObject("member")

                        name = Utils.getString(member, "company_name")
                        lng = Utils.getDouble(member, "lat")
                        lat = Utils.getDouble(member, "lng")

                        companyNameTV.text = name
                        storeInfoTV.text = Utils.getString(member, "address") + " " + Utils.getString(member, "address_detail")

                        var profile_uri = Config.url + Utils.getString(member,"image_uri")
                        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(profile_uri, profileIV, Utils.UILoptionsProfile)

                        if (clicktype ==1){

                        val data = response.getJSONArray("postList")

                        for (i in 0..(data.length() - 1)) {
                            adapterData.add(data[i] as JSONObject)
                        }

                        adapterOrder.notifyDataSetChanged()

                        } else {
                            val data = response.getJSONArray("reviewList")
                            for (i in 0..data.length() - 1) {
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

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseString: String?, throwable: Throwable) {
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
