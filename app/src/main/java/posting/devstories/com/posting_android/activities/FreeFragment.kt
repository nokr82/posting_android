package posting.devstories.com.posting_android.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FreeFragment : MainFragment() {

    var getImage=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapterData.clear()
        loadData(1)
    }


}
