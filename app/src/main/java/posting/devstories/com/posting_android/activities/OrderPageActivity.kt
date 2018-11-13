package posting.devstories.com.posting_android.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ScrollView
import android.widget.Toast
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.nostra13.universalimageloader.core.ImageLoader
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_order_page.*
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.MemberAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.OrderAdapter
import posting.devstories.com.posting_android.adapter.ReviewAdapter
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.RootActivity
import posting.devstories.com.posting_android.base.Utils

open class OrderPageActivity : RootActivity() {

    val WRTIE_REVIEW = 101;

    private lateinit var context:Context

    private var progressDialog: ProgressDialog? = null
    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapterOrder: OrderAdapter
    lateinit var adapterReview: ReviewAdapter

    var clicktype = 1
    var company_id = -1

    var lat:Double = 0.0
    var lng:Double = 0.0
    var companyName:String = ""

    internal var delReviewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var review_id:Int = intent.getIntExtra("review_id", 1)

                if(clicktype == 2) {
                    for (i in 0..(adapterData.size - 1)) {
                        var data = adapterData.get(i)
                        var review = data.getJSONObject("Review")

                        if(review_id == Utils.getInt(review, "id")) {
                            adapterData.removeAt(i)
                        }
                    }
                    adapterReview.notifyDataSetChanged()
                }

            }
        }
    }

    internal var editReviewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                var review_id:Int = intent.getIntExtra("review_id", 1)

                    loadData()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_page)

        this.context = this

        company_id = intent.getIntExtra("company_id", -1)

        val image_uri =   PrefUtils.getStringPreference(context, "school_image")
        var univimg = Config.url +image_uri
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(univimg, univIV, Utils.UILoptionsProfile)

        val filter1 = IntentFilter("DEL_REVIEW")
        registerReceiver(delReviewReceiver, filter1)

        val filter2 = IntentFilter("EDIT_REVIEW")
        registerReceiver(editReviewReceiver, filter2)

        adapterReview = ReviewAdapter(context, R.layout.item_post,adapterData)
        adapterOrder = OrderAdapter(context, R.layout.item_post,adapterData)

        couponGV.adapter = adapterOrder
        couponGV.isExpanded = false

        couponLL.setOnClickListener {
            reviewWriteLL.visibility = View.GONE
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

        reviewLL.setOnClickListener {
            reviewWriteLL.visibility = View.VISIBLE
            couponV.visibility = View.VISIBLE
            adapterData.clear()
            reviewV.visibility = View.VISIBLE
            couponV.visibility = View.INVISIBLE
            clicktype = 2
            couponGV.adapter = adapterReview
            loadData()

        }

        reviewWriteLL.setOnClickListener {
            var intent = Intent(context, ReviewWriteActivity::class.java)
            intent.putExtra("company_member_id", company_id)
            startActivityForResult(intent, WRTIE_REVIEW)
        }

        gpsLL.setOnClickListener {
            if(lat == 0.0 && lng == 0.0) {
                Toast.makeText(context, "위치 정보가 없습니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var intent = Intent(context, OrderMapActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lng", lng)
            intent.putExtra("name", companyName)
            startActivity(intent)
        }

        loadData()
    }


    fun loadData() {
        val params = RequestParams()
        params.put("company_member_id", company_id)
        params.put("member_id",PrefUtils.getIntPreference(context, "member_id"))
        params.put("type",clicktype)

        MemberAction.company_page(params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                try {

                    println(response)

                    val result = response!!.getString("result")

                    if ("ok" == result) {

                        var reviewCnt = Utils.getString(response, "reviewCnt")
                        var postCnt = Utils.getString(response, "postCnt")

                        couponCntTV.text = postCnt
                        reviewCntTV.text = reviewCnt

                        val member = response.getJSONObject("member")
                        val school = response.getJSONObject("school")

                        val school_image_uri = Utils.getString(school, "image_uri")
                        var univimg = Config.url + school_image_uri
                        ImageLoader.getInstance().displayImage(univimg, univIV, Utils.UILoptionsUserProfile)

                        companyName = Utils.getString(member, "company_name")
                        companyNameTV.text = companyName
                        infoTV.text = Utils.getString(member, "address") + Utils.getString(member, "address_detail")

                        val image = Utils.getString(school,"image_uri")

                        var school_image = Config.url + image
                        ImageLoader.getInstance().displayImage(school_image, univIV, Utils.UILoptionsUserProfile)

                        lat = Utils.getDouble(member, "lat")
                        lng = Utils.getDouble(member, "lng")

                        var profile = Config.url + Utils.getString(member,"image_uri")
                        ImageLoader.getInstance().displayImage(profile, profileIV, Utils.UILoptionsUserProfile)

                        adapterData.clear()

                        if (clicktype ==1){
                            adapterOrder.notifyDataSetChanged()

                            val data = response.getJSONArray("postList")

                            for (i in 0..data.length() - 1) {

                                adapterData.add(data[i] as JSONObject)

                            }
                            adapterOrder.notifyDataSetChanged()

                        } else {
                            adapterReview.notifyDataSetChanged()

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

            override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    responseString: String?,
                    throwable: Throwable
            ) {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }

                System.out.println(responseString);

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                WRTIE_REVIEW -> {
                    loadData()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()


        try {
            if(null != delReviewReceiver) {
                unregisterReceiver(delReviewReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }

        try {
            if(null != editReviewReceiver) {
                unregisterReceiver(editReviewReceiver)
            }

        } catch (e: IllegalArgumentException) {
        }

    }

}
