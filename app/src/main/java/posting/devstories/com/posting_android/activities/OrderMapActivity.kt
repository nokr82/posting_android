package posting.devstories.com.posting_android.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_order_map.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import posting.devstories.com.posting_android.R
import posting.devstories.com.posting_android.base.RootActivity

class OrderMapActivity : RootActivity() {
    lateinit var context: Context
    private var progressDialog: ProgressDialog? = null

    private var mapViewContainer: LinearLayout? = null
    private var mapView: MapView? = null

    var lat:Double = 0.0
    var lng:Double = 0.0
    var name:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_map)

        this.context = this
        progressDialog = ProgressDialog(context, R.style.progressDialogTheme)
        progressDialog!!.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Large)
        progressDialog!!.setCancelable(false)


        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)
        name = intent.getStringExtra("name")

        finishLL.setOnClickListener {
            finish()
        }

        mapViewContainer = mapViewLL
        mapView = MapView(this)

        var mapPoint:MapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)
        var marker: MapPOIItem = MapPOIItem()
        marker.itemName = name
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.CustomImage
        marker.customImageResourceId = R.mipmap.map_point

        mapView!!.addPOIItem(marker)
        mapView!!.setMapCenterPointAndZoomLevel(mapPoint, 3, true)

        mapViewContainer!!.addView(mapView)

    }

    override fun onDestroy() {
        super.onDestroy()

        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }

    }

}
