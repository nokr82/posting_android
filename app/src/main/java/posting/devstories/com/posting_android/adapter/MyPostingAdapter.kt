package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import java.text.SimpleDateFormat
import java.util.*


open class MyPostingAdapter(context:Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

    private lateinit var item: ViewHolder
    var view:Int = view
    var data:ArrayList<JSONObject> = data

    override fun getView(position: Int, convertView: View?, parent : ViewGroup?): View {

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
        val member = json.getJSONObject("Member")
        var posting = json.getJSONObject("Posting")
        var current_school_id = Utils.getString(member, "school_id")
        var company_name = Utils.getString(member, "company_name")
        var school_id = Utils.getString(posting, "school_id")
        var contents =   Utils.getString(posting, "contents")
        var image_uri = Utils.getString(posting, "image_uri")
        var leftCount = Utils.getString(posting, "leftCount")
        var type = Utils.getString(posting, "type")
        var coupon_type:String =  Utils.getString(posting, "coupon_type")
        var uses_start_date =   Utils.getString(posting, "uses_start_date")
        var uses_end_date:String =  Utils.getString(posting, "uses_end_date")
        var menu_name:String =  Utils.getString(posting, "menu_name")
        var sale_per:String =  Utils.getString(posting, "sale_per")
        var sale_price:String =  Utils.getString(posting, "sale_price")

        item.storageIV.visibility = View.GONE
        item.storageTV.visibility = View.GONE

//        if (current_school_id != school_id){
//            item.postbg2IV.setImageResource(R.mipmap.write_bg2)
//        }else{
//            item.postbg2IV.setImageResource(R.mipmap.bg)
//        }

        if("" != coupon_type) {

            val cymd = SimpleDateFormat("yy.MM.dd", Locale.KOREA)

            val coupon_startdate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uses_start_date)
            //디테일 이미지에보이는 날짜
            val ctv_startdate = cymd.format(coupon_startdate)
            val coupon_enddate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uses_end_date)
            val ctv_enddate = cymd.format(coupon_enddate)

            if (coupon_type.equals("1")){
                item.coupon_orderTV.text = company_name
                item.storageTV.visibility = View.GONE
                item.couponLL.visibility = View.VISIBLE
                item.coupon_titleTV.text = menu_name
                item.coupon_saleTV.text = sale_per
                item.coupon_sale2TV.text = "할인"
                item.coupon_TV.text = "%"
                item.coupon_startdateTV.text = ctv_startdate + "~"
                item.coupon_contentTV.text = contents
                item.coupon_enddateTV.text = ctv_enddate
            }else if (coupon_type.equals("2")){
                item.coupon_orderTV.text = company_name
                item.storageTV.visibility = View.GONE
                item.couponLL.setBackgroundColor(Color.parseColor("#FB2B70"))
                item.couponLL.visibility = View.VISIBLE
                item.coupon_titleTV.text = menu_name
                item.coupon_saleTV.text = "FREE"
                item.coupon_TV.visibility = View.GONE
                item.coupon_sale2TV.visibility = View.GONE
                item.coupon_contentTV.text = contents
                item.coupon_startdateTV.text = ctv_startdate + "~"
                item.coupon_enddateTV.text = ctv_enddate
            }else if (coupon_type.equals("3")){
                item.coupon_orderTV.text = company_name
                item.storageTV.visibility = View.GONE
                item.couponLL.visibility = View.VISIBLE
                item.couponLL.setBackgroundColor(Color.parseColor("#A12BFB"))
                item.coupon_titleTV.text = menu_name
                item.coupon_saleTV.text = sale_price
                item.coupon_contentTV.text = contents
                item.coupon_TV.text = "원"
                item.coupon_sale2TV.text = "할인"
                item.coupon_startdateTV.text = ctv_startdate + "~"
                item.coupon_enddateTV.text = ctv_enddate
            }

        } else {
            item.couponLL.visibility = View.GONE
            if (!image_uri.isEmpty() && image_uri != "") {
                var image = Config.url + image_uri
                ImageLoader.getInstance().displayImage(image, item.storageIV, Utils.UILoptionsPosting)
                item.storageIV.visibility = View.VISIBLE
            } else {
                item.storageTV.text = contents
                item.storageTV.visibility = View.VISIBLE
                item.storageIV.visibility = View.GONE
            }
        }

        if(leftCount == "9999") {
            item.leftCntTV.text = "∞"
        } else {
            item.leftCntTV.text = leftCount
        }

        var chattingCnt = Utils.getInt(posting, "chattingCnt")

        item.chatAlarmCntTV.visibility = View.GONE

        if(chattingCnt > 0 && (type == "3" || type == "4" || type == "5")) {
            item.chatAlarmCntTV.visibility = View.VISIBLE
            item.chatAlarmCntTV.text = chattingCnt.toString()
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

    fun removeItem(position: Int){
        data.removeAt(position)
        notifyDataSetChanged()

    }

    class ViewHolder(v: View) {
        var storageIV :ImageView
        var leftCntTV :TextView
        var storageTV: TextView
        var chatAlarmCntTV: TextView
        var couponLL :LinearLayout
        var coupon_orderTV :TextView
        var coupon_titleTV :TextView
        var coupon_saleTV :TextView
        var coupon_sale2TV :TextView
        var coupon_TV :TextView
        var coupon_startdateTV :TextView
        var coupon_enddateTV :TextView
        var coupon_contentTV :TextView
        var postbg2IV:ImageView
        init {
            postbg2IV = v.findViewById(R.id.postbgIV)as ImageView
            storageIV = v.findViewById(R.id.storageIV) as ImageView
            storageTV = v.findViewById(R.id.storageTV) as TextView
            leftCntTV = v.findViewById(R.id.leftCntTV) as TextView
            chatAlarmCntTV = v.findViewById(R.id.chatAlarmCntTV) as TextView
            couponLL = v.findViewById(R.id.couponLL) as LinearLayout
            coupon_orderTV = v.findViewById(R.id.coupon_orderTV) as TextView
            coupon_titleTV = v.findViewById(R.id.coupon_titleTV) as TextView
            coupon_contentTV = v.findViewById(R.id.coupon_contentTV) as TextView
            coupon_saleTV = v.findViewById(R.id.coupon_saleTV) as TextView
            coupon_sale2TV = v.findViewById(R.id.coupon_sale2TV) as TextView
            coupon_TV = v.findViewById(R.id.coupon_TV) as TextView
            coupon_startdateTV = v.findViewById(R.id.coupon_startdateTV) as TextView
            coupon_enddateTV = v.findViewById(R.id.coupon_enddateTV) as TextView


        }
    }
}