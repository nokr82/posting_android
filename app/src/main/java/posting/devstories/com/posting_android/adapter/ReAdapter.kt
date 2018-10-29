package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.R.id.*
import posting.devstories.com.posting_android.base.Utils
import posting.devstories.com.posting_android.base.Config


open class ReAdapter(context:Context, view:Int, data:ArrayList<JSONObject>) : ArrayAdapter<JSONObject>(context, view, data) {

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


        for (i in 0..json.length() - 1) {
            var id = Utils.getString(json, "member_name")
            var comments = Utils.getString(json, "comments")
            var created = Utils.getString(json, "created")
            var depth = Utils.getInt(json,"depth")
            var p_comments_id = Utils.getString(json,"p_comments_id")





            if (p_comments_id != "-1"){
                item.reIV.visibility = View.VISIBLE
            } else{
                item.reIV.visibility = View.GONE
            }

            item.reTX.text = comments
            item.writerTX.text = id + " | " + created + " | "
        }

//            item.writerTX.text = id+"|"+created+"|"







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
        var reIV:ImageView
        var reTX:TextView
        var re2TX:TextView
        var writerTX:TextView
        var reLL:LinearLayout


        init {
            reLL = v.findViewById(R.id.reLL)as LinearLayout
            reIV = v.findViewById(R.id.reIV) as ImageView
            reTX = v.findViewById(R.id.reTX) as TextView
            re2TX = v.findViewById(R.id.re2TX) as TextView
            writerTX = v.findViewById(R.id.writerTX) as TextView
        }
    }
}