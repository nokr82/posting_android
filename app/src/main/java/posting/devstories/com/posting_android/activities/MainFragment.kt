package posting.devstories.com.posting_android.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fra_main.*
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter

open class MainFragment : Fragment() {

    lateinit var activity: MainActivity
    var tabType = 1;

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapter:PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fra_main, container, false)

        activity = getActivity() as MainActivity
        tabType = activity.tabType

//        adapter = PostAdapter(activity, R.layout.item_post, adapterData)
//        gideGV.adapter = adapter


        loadData();

        return v
    }

    fun loadData(){
    }

}
