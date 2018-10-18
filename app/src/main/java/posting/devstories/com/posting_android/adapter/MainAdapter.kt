package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Utils


open class MainAdapter(context:Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

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


        var type = Utils.getString(json, "type")

        println(type + " ====================================================== ")

        var title = "자유 NEW"
        var color = "#1D9AD7"

        if(type == "free") {

            title = "자유 NEW"
            color = "#1D9AD7"
            item.tackIV.setImageResource(R.mipmap.apjung)

        } else if (type == "info") {

            title = "정보 NEW"
            color = "#2A3890"
            item.tackIV.setImageResource(R.mipmap.blue)

        } else if (type == "study") {

            title = "스터디 NEW"
            color = "#FAA71A"
            item.tackIV.setImageResource(R.mipmap.yellow)

        } else if (type == "class") {

            title = "동아리 NEW"
            color = "#00A99D"
            item.tackIV.setImageResource(R.mipmap.green)

        } else if (type == "meeting") {

            title = "미팅 NEW"
            color = "#EC4095"
            item.tackIV.setImageResource(R.mipmap.pink)

        } else if (type == "coupon") {

            title = "쿠폰 NEW"
            color = "#ED2123"
            item.tackIV.setImageResource(R.mipmap.red)

        }

        item.titleTV.text = title
        item.titleTV.setTextColor(Color.parseColor(color))

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

        var titleTV: TextView
        var tackIV: ImageView

        init {
            titleTV = v.findViewById(R.id.titleTV) as TextView
            tackIV = v.findViewById(R.id.tackIV) as ImageView
        }
    }
}