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

        if (type == "free") {

            title = "자유 NEW"
            color = "#1D9AD7"

        } else if (type == "info") {

            title = "정보 NEW"
            color = "#2A3890"

        } else if (type == "study") {

            title = "스터디 NEW"
            color = "#FAA71A"

        } else if (type == "class") {

            title = "동아리 NEW"
            color = "#00A99D"

        } else if (type == "meeting") {

            title = "미팅 NEW"
            color = "#EC4095"

        } else if (type == "coupon") {


            title = "쿠폰 NEW"
            color = "#ED2123"

        }

        item.titleTV.text = title
        item.titleTV.setTextColor(Color.parseColor(color))

        item.postingLL.removeAllViews()

        for (i in 0 ..(list.length() - 1)){

            var p: JSONObject = list[i] as JSONObject
            var posting = p.getJSONObject("Posting")

            var id = Utils.getString(posting, "id")
            var contents = Utils.getString(posting, "contents")
            var image_uri = Utils.getString(posting, "image_uri")
            var leftCnt = Utils.getString(posting, "leftCount")

            val postingView = View.inflate(context, R.layout.item_post, null)

            var postRL: RelativeLayout = postingView.findViewById(R.id.postRL)
            var postIV: ImageView = postingView.findViewById(R.id.postIV);
            var leftCntTV: TextView = postingView.findViewById(R.id.leftCntTV);
            var contentsTV: TextView = postingView.findViewById(R.id.contentsTV);

            val params = postRL.layoutParams as LinearLayout.LayoutParams
            params.setMargins(10, 0, 0, 0)
            postRL.layoutParams = params

            if(!image_uri.isEmpty() && image_uri != "") {
                var image:String = Config.url+image_uri
                ImageLoader.getInstance().displayImage(image, postIV, Utils.UILoptionsPosting)
                postIV.visibility = View.VISIBLE
            } else {
                contentsTV.visibility = View.VISIBLE
                contentsTV.text = contents
            }

            postRL.setOnClickListener {
                var intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("id", id)
                context.startActivity(intent)
            }

            leftCntTV.text = leftCnt
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

        init {
            titleTV = v.findViewById(R.id.titleTV) as TextView
            postingLL = v.findViewById(R.id.postingLL) as LinearLayout
        }
    }
}