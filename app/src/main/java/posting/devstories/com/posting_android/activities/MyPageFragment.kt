package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import posting.devstories.com.posting_android.Actions.PostingAction
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.FullScreenImageAdapter
import posting.devstories.com.posting_android.adapter.MainAdapter
import posting.devstories.com.posting_android.adapter.PostAdapter
import posting.devstories.com.posting_android.base.NonSwipeableViewPager
import posting.devstories.com.posting_android.base.PrefUtils
import posting.devstories.com.posting_android.base.Utils

open class MyPageFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null

    lateinit var mainActivity:MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context
        if (null != ctx) {
            doSomethingWithContext(ctx)
        }

        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fra_my_page, container, false)
    }
    fun doSomethingWithContext(context: Context) {
        // TODO: Actually do something with the context
        this.ctx = context
        progressDialog = ProgressDialog(ctx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}
