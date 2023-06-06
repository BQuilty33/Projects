package com.example.autotrackerca400


import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.autotrackerca400.databinding.ActivityObjectDetectionBinding
import com.google.common.util.concurrent.ListenableFuture
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.*
import kotlin.concurrent.*
import kotlin.jvm.*

class DetectionActivity : AppCompatActivity(),DetectObjects.DetectorListener {

    private lateinit var binding: ActivityObjectDetectionBinding

    private lateinit var detectObjectsScript: DetectObjects

    private lateinit var bitmapBuffer: Bitmap

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var setLabel: TextView

    private lateinit var distanceLabel: TextView

    private lateinit var voiceListener: VoiceListener

    private lateinit var locationService : LocationService

    private var greenOn : Boolean = false

    private var redIndication : Boolean = false

    private var redOn : Boolean = false

    private lateinit var mainActivity : MainActivity

    private var redTime : Long = 0
    private var redTimePrev : Long = 0

    private var greenTime : Long = 0
    private var greenTimePrev : Long = 0

    private var distance1 : Int = 0
    private var distance2 : Int = 0
    private var distance3 : Int = 0
    private var averageDistance : Int = 0
    private var totalDistance : Int = 0


    companion object {
        @JvmField
        var speed: Int = 0
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getpermission()

        binding = ActivityObjectDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLabel = findViewById(R.id.textV)

        locationService = LocationService()

        distanceLabel = findViewById(R.id.distance)


        mainActivity = MainActivity()

        voiceListener = VoiceListener(this)

        // set up camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // bind the preview
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider = cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        detectObjectsScript = DetectObjects(
            context = this,
            objectDetectorListener = this
        )
    }

    override fun onError(error: String) {
        TODO("Not yet implemented")
    }

    private fun detectTrafficLights(resultsTrafficLights: List<Detection> = LinkedList<Detection>()){
        redTime = System.currentTimeMillis() / 1000
        // red has higher accuracy so make sure score is above 0.45
        if (!redIndication && resultsTrafficLights[0].categories[0].label == "red" && redTime - redTimePrev >= 6 )  {
            setLabel.text = "Red Traffic Light ahead"
            thread(start = true) {
                redTimePrev = System.currentTimeMillis() / 1000
                redOn = true
                redIndication = true
                voiceListener.voiceSpeak("Red traffic light Near")
                Thread.sleep(2000)
                redIndication = false
            }
        }
        greenTime = System.currentTimeMillis() / 1000
        if (!greenOn && !redOn && resultsTrafficLights[0].categories[0].label == "green" && greenTime - greenTimePrev >= 6) {
            setLabel.text = "Green Traffic Light ahead"
            thread(start = true) {
                greenTimePrev = System.currentTimeMillis() / 1000
                greenOn = true
                voiceListener.voiceSpeak("Green traffic light Near")
                Thread.sleep(2000)
                greenOn = false
            }
        }
        if (redOn && resultsTrafficLights[0].categories[0].label == "green") {
            setLabel.text = "Traffic Light has turned green"
            thread(start = true) {
                redOn = false;
                voiceListener.voiceSpeak("Traffic light has turned green")
                Thread.sleep(2000)
            }
        }
    }

    private fun maintainDistance(){
        var stoppingDistance = "Maintain Distance! Stopping distance reached"
        setLabel.text = stoppingDistance
        voiceListener.voiceSpeak(stoppingDistance)
    }

    private fun detectCars(resultsCars: List<Detection> = LinkedList<Detection>()){
        if(resultsCars[0].categories[0].label == "car" && resultsCars[0].categories[0].score >= 0.65 ) {
            if ((resultsCars[0].boundingBox.right + resultsCars[0].boundingBox.left).toInt() in 2500..3500 ) {
                setLabel.text = "Car in front.."
                var distanceEstimate : Int = (resultsCars[0].boundingBox.bottom - resultsCars[0].boundingBox.top).toInt()
                // get average of distance estimate
                totalDistance += 1
                if (totalDistance == 1) {
                    distance1 =
                        distanceEstimate
                }
                if(totalDistance == 2){
                    distance2 = distanceEstimate
                }
                if (totalDistance == 3) {
                    distance3 = distanceEstimate
                    // will now get average distance
                    averageDistance = (distance1 + distance2 + distance3 ) / 3
                    // get stopping distance with speed + meter estimate

                    // speed 60-80km and average distance is 33m.
                    if (averageDistance in 111..239) {
                        distanceLabel.text = "Car between 25-33 metres away"
                        if(speed in 60..80 ) {
                            maintainDistance()
                        }
                    }

                    // speed 50-60km and average distance is 25m
                    if (averageDistance in 241..319) {
                        distanceLabel.text = "Car between 18-25 metres away"
                        if(speed in 50..60) {
                            maintainDistance()
                        }
                    }

                    // speed 40-50km and average distance is 18m
                    if (averageDistance in 321..479) {
                        distanceLabel.text = "Car between 12-18 metres away"
                        if(speed in 40..50) {
                            maintainDistance()
                        }
                    }

                    // speed 30-40km and average distance is 12m
                    if ( averageDistance > 480) {
                        distanceLabel.text = "Car less than 12 metres away"
                        if(speed in 30..40){
                            maintainDistance()
                        }
                    }
                    totalDistance = 0
                }
            }
        }
    }

    // listen for objects being detected
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        this.runOnUiThread() {

            if (results != null) {
                if (results.size > 0) {
                    setLabel.text = ""
                    distanceLabel.text = ""
                    // functionality for traffic lights
                    detectTrafficLights(results)
                    // functionality for car
                    detectCars(results)
                }
            }
        }
    }


    private fun bindPreview(cameraProvider: ProcessCameraProvider){

        val preview = Preview.Builder().build()

        // select back facing camera
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        // select options for analysing the image, such as resoloution.
        val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(1280,720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
        // extract image from the camera
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->
            if (!::bitmapBuffer.isInitialized)
            {
                // create bitmap of image
                bitmapBuffer = Bitmap.createBitmap( image.width, image.height, Bitmap.Config.ARGB_8888 )
            }
            // detect objects using the image.
            detectObjects(image)
        }

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)



    }


    private fun detectObjects(image: ImageProxy) {
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        detectObjectsScript.detect(bitmapBuffer,imageRotation)


    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getpermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            getpermission()
        }
    }


}

