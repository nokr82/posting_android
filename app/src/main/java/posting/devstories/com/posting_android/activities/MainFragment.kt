package posting.devstories.com.posting_android.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import org.json.JSONObject
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter

open class MainFragment : Fragment() {

    lateinit var activity: MainActivity
    var tabType = 1;

    var adapterData: ArrayList<JSONObject> = ArrayList<JSONObject>()
    lateinit var adapter:PostAdapter

    lateinit var gideGV: GridView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fra_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gideGV = view.findViewById<GridView>(R.id.gideGV)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity = getActivity() as MainActivity
        tabType = activity.tabType

        adapter = PostAdapter(activity, R.layout.item_post, adapterData)
        gideGV.adapter = adapter

        loadData();

    }

    fun loadData(){



    }

}
