package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import posting.devstories.com.posting_android.R

open class OrderPageFragment : Fragment() {

    var ctx: Context? = null
    private var progressDialog: ProgressDialog? = null

    lateinit var mainActivity:MainActivity




    lateinit var reviewLL: LinearLayout
    lateinit var reviewV:View
    lateinit var couponLL: LinearLayout
    lateinit var couponV:View
    lateinit var couponGV:GridView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ctx = context
        if (null != ctx) {
            doSomethingWithContext(ctx)
        }

        mainActivity = activity as MainActivity
        return inflater.inflate(R.layout.fra_orderpg, container, false)
    }
    fun doSomethingWithContext(context: Context) {
        // TODO: Actually do something with the context
        this.ctx = context
        progressDialog = ProgressDialog(ctx)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviewLL = view.findViewById(R.id.reviewLL)
        reviewV = view.findViewById(R.id.reviewV)
        couponLL = view.findViewById(R.id.couponLL)
        couponV = view.findViewById(R.id.couponV)
        couponGV = view.findViewById(R.id.couponGV)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        reviewLL.setOnClickListener {
            reviewV.visibility = View.VISIBLE
            couponV.visibility = View.INVISIBLE
        }
        couponLL.setOnClickListener {
            couponV.visibility = View.VISIBLE
            reviewV.visibility = View.INVISIBLE
        }



    }



}
