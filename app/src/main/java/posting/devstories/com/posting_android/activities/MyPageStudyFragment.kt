package posting.devstories.com.posting_android.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MyPageStudyFragment : MyPageParentFragment() {

    var getImage=""

    internal var setViewReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            if (intent != null) {
                val tabType = intent.getIntExtra("tabType", 1)

                if (tabType == 3) {
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

        var bundle: Bundle = this.arguments!!
        tab = bundle.getInt("tab")

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData(3)
    }


}
