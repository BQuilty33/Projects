/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.autotrackerca400

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.InputStream
import java.util.*

@RunWith(AndroidJUnit4::class)
class TFObjectDetectionTest {



    @Test
    @Throws(Exception::class)
    fun detectedImageIsGreenLight() {
        val DetectObjects =
            DetectObjects(
                context = ApplicationProvider.getApplicationContext(),
                objectDetectorListener =
                    object : DetectObjects.DetectorListener {
                        override fun onError(error: String) {}

                        override fun onResults(
                          results: MutableList<Detection>?,
                          inferenceTime: Long,
                          imageHeight: Int,
                          imageWidth: Int
                        ) {
                            var results2: List<Detection> = LinkedList<Detection>()
                            if (results != null) {
                                results2 = results
                                assertEquals(results2[0].categories[0].label,"green")
                            }

                        }
                    }
            )

            // Create Bitmap and convert to TensorImage
            val bitmap = loadImage("GreenTraffic.jpeg")
            // Run the object detector on the sample image
            DetectObjects.detect(bitmap!!, 0)
    }


    @Test
    @Throws(Exception::class)
    fun detectedImageIsRedLight() {
        val DetectObjects =
            DetectObjects(
                context = ApplicationProvider.getApplicationContext(),
                objectDetectorListener =
                object : DetectObjects.DetectorListener {
                    override fun onError(error: String) {}

                    override fun onResults(
                        results: MutableList<Detection>?,
                        inferenceTime: Long,
                        imageHeight: Int,
                        imageWidth: Int
                    ) {
                        var results2: List<Detection> = LinkedList<Detection>()
                        if (results != null) {
                            results2 = results
                            assertEquals(results2[0].categories[0].label,"red")
                        }

                    }
                }
            )

        // Create Bitmap and convert to TensorImage
        val bitmap = loadImage("RedLight.jpeg")
        // Run the object detector on the sample image
        DetectObjects.detect(bitmap!!, 0)
    }

    @Test
    @Throws(Exception::class)
    fun detectedImageIsCar() {
        val DetectObjects =
            DetectObjects(
                context = ApplicationProvider.getApplicationContext(),
                objectDetectorListener =
                object : DetectObjects.DetectorListener {
                    override fun onError(error: String) {}

                    override fun onResults(
                        results: MutableList<Detection>?,
                        inferenceTime: Long,
                        imageHeight: Int,
                        imageWidth: Int
                    ) {
                        var results2: List<Detection> = LinkedList<Detection>()
                        if (results != null) {
                            results2 = results
                            assertEquals(results2[0].categories[0].label,"car")
                        }

                    }
                }
            )

        // Create Bitmap and convert to TensorImage
        val bitmap = loadImage("Car.jpeg")
        // Run the object detector on the sample image
        DetectObjects.detect(bitmap!!, 0)
    }


    @Throws(Exception::class)
    private fun loadImage(fileName: String): Bitmap? {
        val context: Context = ApplicationProvider.getApplicationContext()
        val inputStream: InputStream = context.assets.open(fileName)
        return BitmapFactory.decodeStream(inputStream)
    }
}
