package posting.devstories.com.posting_android.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import posting.devstories.com.posting_android.adapter.PostAdapter

class FreeFragment : MainFragment() {

    var getid = ""
    var getImage=""
    var getmember = ""
    var getcontents=""
    var getImageurl = ""
    var getupdate=""
    var gettype = ""
    var getdel = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        adapterData.clear()
        loadData(1)

        return view
    }


}
