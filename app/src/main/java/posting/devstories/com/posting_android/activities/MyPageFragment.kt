package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTabHost
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.tab_my_page_noti_view.*
import kotlinx.android.synthetic.main.tab_my_page_posting_view.*
import kotlinx.android.synthetic.main.tab_my_page_storage_view.*
import posting.devstories.com.posting_android.R

open class MyPageFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null

    lateinit var mainActivity:MainActivity

    lateinit var fragmentFT: FragmentTabHost
    lateinit var fragmentFL: FrameLayout

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

        fragmentFT = view.findViewById(R.id.fragmentFT)
        fragmentFL = view.findViewById(R.id.fragmentFL)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentFT.setup(context, childFragmentManager, R.id.fragmentFL)

        val tabPostingV = View.inflate(context, R.layout.tab_my_page_posting_view, null)
        val tabStorageV = View.inflate(context, R.layout.tab_my_page_storage_view, null)
        val tabNotiV = View.inflate(context, R.layout.tab_my_page_noti_view, null)

        fragmentFT.tabWidget.dividerDrawable = null

        fragmentFT.addTab(fragmentFT.newTabSpec("posting").setIndicator(tabPostingV), MyPagePostingFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("storage").setIndicator(tabStorageV), MyPageStorageFragment::class.java, null)
        fragmentFT.addTab(fragmentFT.newTabSpec("notify").setIndicator(tabNotiV), MyPageNotifyFragment::class.java, null)

        postingRL.setOnClickListener {

            setTabView()
            postingV.visibility = View.VISIBLE

            fragmentFT.onTabChanged("posting")
        }

        storageRL.setOnClickListener {

            setTabView()
            storageV.visibility = View.VISIBLE

            fragmentFT.onTabChanged("storage")
        }

        notiRL.setOnClickListener {

            setTabView()
            notifyV.visibility = View.VISIBLE

            fragmentFT.onTabChanged("notify")
        }

    }

    fun setTabView(){
        postingV.visibility = View.INVISIBLE
        storageV.visibility = View.INVISIBLE
        notifyV.visibility = View.INVISIBLE
    }

}
