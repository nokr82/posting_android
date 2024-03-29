package posting.devstories.com.posting_android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.Config
import posting.devstories.com.posting_android.base.RationalRelativeLayout
import posting.devstories.com.posting_android.base.Utils
import java.util.*
import posting.devstories.com.posting_android.base.PrefUtils

class DetailAnimationRecyclerAdapter(detailAnimationRecyclerAdapterData: ArrayList<JSONObject>) : RecyclerView.Adapter<ListItem>() {

    val items = detailAnimationRecyclerAdapterData

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        return ListItem(
                LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_detail_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListItem, position: Int) {
        holder.bind(getItems().get(position))
    }

    fun getItems(): List<JSONObject> {
        return items
    }

    fun removeTopItem() {
        items.removeAt(0)
        notifyDataSetChanged()
    }
}

class ListItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal lateinit var imgIV: ImageView
    internal lateinit var contentsTV: TextView
    internal lateinit var postingLL: RationalRelativeLayout
    init {
        postingLL= itemView.findViewById(R.id.postingLL)
        imgIV = itemView.findViewById(R.id.imgIV)
        contentsTV = itemView.findViewById(R.id.contentsTV)
    }

    fun bind(posting:JSONObject) {

        val image_uri = Utils.getString(posting, "image_uri")
        val contents =   Utils.getString(posting, "contents")
        var school_id = Utils.getInt(posting, "school_id")
        var current_school_id = PrefUtils.getIntPreference(itemView.context,"detail_current_school_id")
        println("=========학교잼"+current_school_id)
        println("=========학교잼"+school_id)
        if (current_school_id != school_id){
            postingLL.background = itemView.context.getDrawable(R.mipmap.write_bg2)
        }else{
            postingLL.background = itemView.context.getDrawable(R.mipmap.wtite_bg)
        }


        //uri를 이미지로 변환시켜준다
        if (!image_uri.isEmpty() && image_uri != "") {
            var image = Config.url + image_uri
            ImageLoader.getInstance().displayImage(image, imgIV, Utils.UILoptionsUserProfile)
            imgIV.visibility = View.VISIBLE
        } else {
            contentsTV.text = contents
            contentsTV.visibility = View.VISIBLE
        }
    }
}