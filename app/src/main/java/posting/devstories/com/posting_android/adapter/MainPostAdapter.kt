package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color

import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import com.nostra13.universalimageloader.core.ImageLoader
import posting.devstories.com.posting_android.activities.DetailActivity
import java.text.SimpleDateFormat
import java.util.*

open class MainPostAdapter(context: Context?, view: Int, data: ArrayList<JSONObject>) :
    ArrayAdapter<JSONObject>(context, view, data) {

    private lateinit var item: ViewHolder
    var view: Int = view
    var data: ArrayList<JSONObject> = data



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        lateinit var retView: View

        if (convertView == null) {
            retView = View.inflate(context, view, null)
            item = ViewHolder(retView)
            retView.tag = item
        } else {
            retView = convertView
            item = convertView.tag as ViewHolder
            if (item == null) {
                retView = View.inflate(context, view, null)
                item = ViewHolder(retView)
                retView.tag = item
            }
        }

        var json = data.get(position)

        var type = Utils.getString(json, "type")
        var list = json.getJSONArray("list")

        var title = "자유 NEW"
        var color = "#1D9AD7"
        var tabType = 1

        if (type == "free") {

            title = "자유 NEW"
            color = "#1D9AD7"
            tabType = 1

        } else if (type == "info") {

            title = "정보 NEW"
            color = "#2A3890"
            tabType = 2

        } else if (type == "study") {

            title = "스터디 NEW"
            color = "#FAA71A"
            tabType = 3

        } else if (type == "class") {

            title = "동아리 NEW"
            color = "#00A99D"
            tabType = 4

        } else if (type == "meeting") {

            title = "미팅 NEW"
            color = "#EC4095"
            tabType = 5

        } else if (type == "coupon") {

            title = "쿠폰 NEW"
            color = "#ED2123"
            tabType = 6

        }

        item.titleTV.text = title
        item.titleTV.setTextColor(Color.parseColor(color))

        item.titleLL.setOnClickListener {

            println("tabType adatper: $tabType")

            var intent = Intent()
            intent.putExtra("tabType", tabType)
            intent.action = "SET_VIEW"
            context.sendBroadcast(intent)

        }

        item.postingLL.removeAllViews()

        for (i in 0 ..(list.length() - 1)){

            var p: JSONObject = list[i] as JSONObject
            var posting = p.getJSONObject("Posting")
            val member = p.getJSONObject("Member")
            var company_name = Utils.getString(member, "company_name")
            var id = Utils.getString(posting, "id")
            var current_school_id = Utils.getString(member, "school_id")
            var school_id = Utils.getString(posting, "school_id")
            var contents = Utils.getString(posting, "contents")
            var image_uri = Utils.getString(posting, "image_uri")
            var leftCnt = Utils.getString(posting, "leftCount")
            var coupon_type:String =  Utils.getString(posting, "coupon_type")
            var uses_start_date =   Utils.getString(posting, "uses_start_date")
            var uses_end_date:String =  Utils.getString(posting, "uses_end_date")
            var menu_name:String =  Utils.getString(posting, "menu_name")
            var sale_per:String =  Utils.getString(posting, "sale_per")
            var sale_price:String =  Utils.getString(posting, "sale_price")


            val postingView = View.inflate(context, R.layout.item_main_post, null)
            var postbgIV:ImageView = postingView.findViewById(R.id.postbgIV)
            var postRL: RelativeLayout = postingView.findViewById(R.id.postRL)
            var postIV: ImageView = postingView.findViewById(R.id.postIV);
            var leftCntTV: TextView = postingView.findViewById(R.id.leftCntTV);
            var contentsTV: TextView = postingView.findViewById(R.id.contentsTV);
           var couponLL = postingView.findViewById(R.id.couponLL) as LinearLayout
            var  coupon_orderTV = postingView.findViewById(R.id.coupon_orderTV) as TextView
            var  coupon_titleTV = postingView.findViewById(R.id.coupon_titleTV) as TextView
            var coupon_contentTV = postingView.findViewById(R.id.coupon_contentTV) as TextView
            var coupon_saleTV = postingView.findViewById(R.id.coupon_saleTV) as TextView
            var coupon_sale2TV = postingView.findViewById(R.id.coupon_sale2TV) as TextView
            var  coupon_TV = postingView.findViewById(R.id.coupon_TV) as TextView
            var coupon_startdateTV = postingView.findViewById(R.id.coupon_startdateTV) as TextView
            var  coupon_enddateTV = postingView.findViewById(R.id.coupon_enddateTV) as TextView

            if (current_school_id != school_id){
                postbgIV.setImageResource(R.mipmap.write_bg2)
            }else{
                postbgIV.setImageResource(R.mipmap.bg)
            }



            val cymd = SimpleDateFormat("yy.MM.dd", Locale.KOREA)

            val params = postRL.layoutParams as LinearLayout.LayoutParams
            params.setMargins(10, 0, 0, 0)
            postRL.layoutParams = params

            if (!coupon_type.equals("")) {
                val coupon_startdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uses_start_date)
                //디테일 이미지에보이는 날짜
                val ctv_startdate = cymd.format(coupon_startdate)
                val coupon_enddate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uses_end_date)
                val ctv_enddate = cymd.format(coupon_enddate)

                if (coupon_type.equals("1")) {
                    contentsTV.visibility = View.GONE
                    couponLL.visibility = View.VISIBLE
                    coupon_orderTV.text = company_name
                    coupon_titleTV.text = menu_name
                    coupon_saleTV.text = sale_per
                    coupon_sale2TV.text = "할인"
                    coupon_startdateTV.text = ctv_startdate+"~"
                    coupon_contentTV.text = contents
                    coupon_enddateTV.text = ctv_enddate
                } else if (coupon_type.equals("2")) {
                    contentsTV.visibility = View.GONE
                    coupon_orderTV.text = company_name
                    couponLL.setBackgroundColor(Color.parseColor("#FB2B70"))
                    couponLL.visibility = View.VISIBLE
                    coupon_titleTV.text = menu_name
                    coupon_saleTV.text = "FREE"
                    coupon_TV.visibility = View.GONE
                    coupon_sale2TV.visibility = View.GONE
                    coupon_contentTV.text = contents
                    coupon_startdateTV.text = ctv_startdate+"~"
                    coupon_enddateTV.text = ctv_enddate
                } else if (coupon_type.equals("3")) {
                    contentsTV.visibility = View.GONE
                    couponLL.visibility = View.VISIBLE
                    coupon_orderTV.text = company_name
                    couponLL.setBackgroundColor(Color.parseColor("#A12BFB"))
                    coupon_titleTV.text = menu_name
                    coupon_saleTV.text = sale_price
                    coupon_contentTV.text = contents
                    coupon_TV.text = "원"
                    coupon_sale2TV.text = "할인"
                    coupon_startdateTV.text = ctv_startdate+"~"
                    coupon_enddateTV.text = ctv_enddate
                }
            }else{
                couponLL.visibility = View.GONE
            if(!image_uri.isEmpty() && image_uri != "") {
                var image:String = Config.url+image_uri
                ImageLoader.getInstance().displayImage(image, postIV, Utils.UILoptionsPosting)
                postIV.visibility = View.VISIBLE
            } else {
                contentsTV.visibility = View.VISIBLE
                contentsTV.text = contents
            }
            }

            postRL.setOnClickListener {
                var intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", id)
                context.startActivity(intent)
            }

            if(leftCnt == "9999") {
                leftCntTV.text = "∞"
            } else {
                leftCntTV.text = leftCnt
            }


            item.postingLL.addView(postingView)

        }

        return retView
    }

    override fun getItem(position: Int): JSONObject {

        return data.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getCount(): Int {

        return data.count()
    }

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyDataSetChanged()

    }

    class ViewHolder(v: View) {

        var titleTV: TextView
        var postingLL: LinearLayout
        var titleLL: LinearLayout

        init {
            titleTV = v.findViewById(R.id.titleTV) as TextView
            postingLL = v.findViewById(R.id.postingLL) as LinearLayout
            titleLL = v.findViewById(R.id.titleLL) as LinearLayout
        }
    }
}