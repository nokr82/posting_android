package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.activities.ReviewDetailActivity
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.RationalRelativeLayout


open class ReviewAdapter(context:Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

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

        var Review = json.getJSONObject("Review")
        var contents =   Utils.getString(Review, "contents")
        var image_uri = Utils.getString(Review, "image_uri")
        var created = Utils.getString(Review, "created")
        var member_id = Utils.getString(Review,"member_id")

        item.postIV.visibility = View.GONE
        item.contentsTV.visibility = View.GONE

        if (!image_uri.isEmpty() && image_uri != "") {
            var image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, item.postIV, Utils.UILoptionsPosting)
            item.postIV.visibility = View.VISIBLE
        } else {
            item.contentsTV.text = contents
            item.contentsTV.visibility = View.VISIBLE
        }
        item.countRL.visibility = View.GONE

        item.postRL.setOnClickListener {
            var intent = Intent(context, ReviewDetailActivity::class.java)
            intent.putExtra("review_id", Utils.getInt(Review,"id"))
            context.startActivity(intent)
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
        var postIV :ImageView
        var contentsTV :TextView
        var leftCntTV :TextView
        var countRL : RationalRelativeLayout
        var postRL : RelativeLayout

        init {
            countRL = v.findViewById(R.id.countRL)as RationalRelativeLayout
            postIV = v.findViewById(R.id.postIV) as ImageView
            contentsTV = v.findViewById(R.id.contentsTV) as TextView
            leftCntTV = v.findViewById(R.id.leftCntTV) as TextView
            postRL = v.findViewById(R.id.postRL) as RelativeLayout
        }
    }
}