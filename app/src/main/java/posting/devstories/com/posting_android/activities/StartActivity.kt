package posting.devstories.com.posting_android.activities

import android.content.Context
import android.content.Intent

/**
 * Created by dev1 on 2018-02-28.
 */

class StartActivity(context: Context, message: String) : Runnable {
    private val context: Context = context
    internal var message: String = message

    override fun run() {
        val intent1 = Intent(context, DlgCommonActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent1.putExtra("contents", message)
        context.startActivity(intent1)
    }


}
