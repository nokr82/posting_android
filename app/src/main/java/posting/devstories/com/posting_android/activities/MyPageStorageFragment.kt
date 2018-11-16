package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.adapter.PostAdapter

class MyPageStorageFragment : MyPagePostingStorageFragment() {

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tab = 2

        pagerAdapter.setTabType(tab)

    }

}


