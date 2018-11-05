package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import android.widget.TextView
import android.view.LayoutInflater
import com.nostra13.universalimageloader.core.ImageLoader
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.Utils

open class DetailAdapter(context: Context, data: JSONObject) {
//open class DetailAdapter(context: Context, data: JSONObject) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    var context:Context = context
    var data:JSONObject = data

    private val inflater: LayoutInflater = LayoutInflater.from(context)

//    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        return ViewHolder(inflater.inflate(R.layout.item_detail, parent, false));
//    }
//
//    override fun getItemCount(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//
////        val spot = data.get(position)
//
//        val image_uri = Utils.getString(data, "image_uri")
//        val contents = Utils.getString(data, "contents")
//
//        holder.imgIV.visibility = View.GONE
//        holder.contentsTV.visibility = View.GONE
//
//        if (!image_uri.isEmpty() && image_uri != "") {
//            var image = Config.url + image_uri
//            ImageLoader.getInstance().displayImage(image, holder.imgIV, Utils.UILoptionsPosting)
//            holder.imgIV.visibility = View.VISIBLE
//        } else {
//            holder.contentsTV.text = contents
//            holder.contentsTV.visibility = View.VISIBLE
//        }
//
//    }
//
//    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
//        internal var contentsTV: TextView
//        internal var imgIV: ImageView
//
//        init {
//            this.contentsTV = view.findViewById(R.id.contentsTV)
//            this.imgIV = view.findViewById(R.id.imgIV)
//        }
//    }
}