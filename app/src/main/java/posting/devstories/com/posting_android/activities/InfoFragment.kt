package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter

class InfoFragment : MainFragment() {


    lateinit var adapterFree: PostAdapter
    lateinit var adapterPost: PostAdapter
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

        //리스트뷰초기화 중복생성방지
        adapterData.clear()
        loadData(2)

        return view
    }



}