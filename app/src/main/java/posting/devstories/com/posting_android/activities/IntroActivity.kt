package posting.devstories.com.posting_android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class IntroActivity : RootActivity() {

    protected var _splashTime = 2000 // time to display the splash screen in ms
    private val _active = true
    private var splashThread: Thread? = null

    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        this.context = this

        splashThread = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    while (waited < _splashTime && _active) {
                        Thread.sleep(100)
                        waited += 100
                    }
                } catch (e: InterruptedException) {
                    // do nothing
                } finally {
                    stopIntro()
                }
            }
        }
        (splashThread as Thread).start()

    }

    private fun stopIntro() {

//        val autoLogin = PrefUtils.getBooleanPreference(context, "autoLogin")
//        val first = PrefUtils.getBooleanPreference(context, "first")

//        if (!autoLogin) {
//
//            PrefUtils.clear(context)
//
//            PrefUtils.setPreference(context, "first", first)
//
//            if ("-1" == menu_type && !first) {
//                val intent = Intent(context, FirstActivity::class.java)
//                //            Intent intent = new Intent(getDialogContext(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//            } else {
//                val intent = Intent(dialogContext, MainActivity::class.java)
//                intent.putExtra("menu_type", menu_type)
//                intent.putExtra("menu_id", menu_id)
//                intent.putExtra("company_member_id", company_member_id)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//            }
//
//        } else {
//            handler.sendEmptyMessage(0)
//        }

        handler.sendEmptyMessage(0)

    }

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //versionInfo();
            login()
        }
    }

    private fun login() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


}
