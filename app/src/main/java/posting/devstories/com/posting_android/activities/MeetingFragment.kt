package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MeetingFragment : MainFragment() {

    var getImage=""

    internal var searchKeywordReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                type = intent.getIntExtra("type", 1)
                keyword = intent.getStringExtra("keyword")
                loadData(5)
            }
        }
    }

    internal var setViewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {
                tabType = intent.getIntExtra("tabType", 5)

                if (tabType == 5) {
                    reloadData(tabType)
                }
            }

        }
    }

    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        try {
            if (setViewReceiver != null) {
                getActivity()!!.unregisterReceiver(setViewReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        val filter3 = IntentFilter("SET_VIEW")
        getActivity()!!.registerReceiver(setViewReceiver, filter3)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val filter3 = IntentFilter("SEARCH_KEYWORD")
        activity.registerReceiver(searchKeywordReceiver, filter3)

        type = 5
        keyword = arguments!!.getString("keyword")

        loadData(5)
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            if (searchKeywordReceiver != null) {
                context!!.unregisterReceiver(searchKeywordReceiver)
            }
        } catch (e: IllegalArgumentException) {
        }

        try {
            if (setViewReceiver != null) {
                getActivity()!!.unregisterReceiver(setViewReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

}

