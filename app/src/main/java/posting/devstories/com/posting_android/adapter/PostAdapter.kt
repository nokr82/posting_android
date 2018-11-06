package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.R.id.coupon_TV
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config


open class PostAdapter(context:Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

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

        var posting = json.getJSONObject("Posting")

        var id = Utils.getString(posting, "id")

        var member_id =   Utils.getString(posting, "member_id")
        var Image = Utils.getString(posting, "Image")
        var created =   Utils.getString(posting, "created")
        var coupon_type:String =  Utils.getString(posting, "coupon_type")
        var uses_start_date =   Utils.getString(posting, "created")
        var uses_end_date:String =  Utils.getString(posting, "uses_end_date")
        var menu_name:String =  Utils.getString(posting, "menu_name")
        var sale_per:String =  Utils.getString(posting, "sale_per")
        var sale_price:String =  Utils.getString(posting, "sale_price")
        var contents =   Utils.getString(posting, "contents")
        var image_uri = Utils.getString(posting, "image_uri")
        var leftCount = Utils.getString(posting, "leftCount")

        item.postIV.visibility = View.GONE
        item.contentsTV.visibility = View.GONE

        if (coupon_type.equals("1")){
            item.contentsTV.visibility = View.GONE
            item.couponLL.visibility = View.VISIBLE
            item.coupon_titleTV.text = menu_name
            item.coupon_saleTV.text = sale_per
            item.coupon_sale2TV.text = "할인"
            item.coupon_startdateTV.text = uses_start_date
            item.coupon_contentTV.text = contents
            item.coupon_enddateTV.text = uses_end_date
        }else if (coupon_type.equals("2")){
            item.contentsTV.visibility = View.GONE
            item.couponLL.setBackgroundColor(Color.parseColor("#FB2B70"))
            item.couponLL.visibility = View.VISIBLE
            item.coupon_titleTV.text = menu_name
            item.coupon_saleTV.text = "FREE"
            item.coupon_TV.visibility = View.GONE
            item.coupon_sale2TV.visibility = View.GONE
            item.coupon_contentTV.text = contents
            item.coupon_startdateTV.text = uses_start_date
            item.coupon_enddateTV.text = uses_end_date
        }else if (coupon_type.equals("3")){
            item.contentsTV.visibility = View.GONE
            item.couponLL.visibility = View.VISIBLE
            item.couponLL.setBackgroundColor(Color.parseColor("#A12BFB"))
            item.coupon_titleTV.text = menu_name
            item.coupon_saleTV.text = sale_price
            item.coupon_contentTV.text = contents
            item.coupon_TV.text = "원"
            item.coupon_sale2TV.text = "할인"
            item.coupon_startdateTV.text = uses_start_date
            item.coupon_enddateTV.text = uses_end_date
        }

        else {
            item.couponLL.visibility = View.GONE
            if (!image_uri.isEmpty() && image_uri != "") {
                var image = Config.url + image_uri
                ImageLoader.getInstance().displayImage(image, item.postIV, Utils.UILoptionsPosting)
                item.postIV.visibility = View.VISIBLE
            } else {
                item.contentsTV.text = contents
                item.contentsTV.visibility = View.VISIBLE
            }
        }



        item.leftCntTV.text = leftCount

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
        var postIV :ImageView
        var contentsTV :TextView
        var leftCntTV :TextView
        var couponLL :LinearLayout
        var coupon_orderTV :TextView
        var coupon_titleTV :TextView
        var coupon_saleTV :TextView
        var coupon_sale2TV :TextView
        var coupon_TV :TextView
        var coupon_startdateTV :TextView
        var coupon_enddateTV :TextView
        var coupon_contentTV :TextView





        init {
            postIV = v.findViewById(R.id.postIV) as ImageView
            contentsTV = v.findViewById(R.id.contentsTV) as TextView
            leftCntTV = v.findViewById(R.id.leftCntTV) as TextView
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