package posting.devstories.com.posting_android.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FreeFragment : MainFragment() {

    var getImage=""

    internal var searchKeywordReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                type = intent.getIntExtra("type", 1)
                keyword = intent.getStringExtra("keyword")
                loadData(1)
            }
        }
    }

    internal var setViewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {
                tabType = intent.getIntExtra("tabType", 1)

                if (tabType == 1) {
                    reloadData(tabType)
                }
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        val filter3 = IntentFilter("SET_VIEW")

        try {
            if (setViewReceiver != null) {
                getActivity()!!.unregisterReceiver(setViewReceiver)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        getActivity()!!.registerReceiver(setViewReceiver, filter3)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val filter3 = IntentFilter("SEARCH_KEYWORD")
        activity.registerReceiver(searchKeywordReceiver, filter3)

        type = 1
        keyword = arguments!!.getString("keyword")

        loadData(1)
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
