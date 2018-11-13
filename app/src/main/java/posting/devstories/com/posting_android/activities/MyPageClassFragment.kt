package posting.devstories.com.posting_android.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MyPageClassFragment : MyPageParentFragment() {

    var getImage=""

    override var tab = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        var bundle: Bundle = this.arguments!!
        tab = bundle.getInt("tab")

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData(4)
    }


}
