package dk.nodes.template.presentation.util

import android.content.Context
import android.os.Environment
import dk.nodes.template.presentation.util.SharedPreferenceUtil.writeToSharedPreferences
import dk.nodes.template.presentation.BuildConfig
import dk.nodes.template.presentation.ui.home.HomeFragment
import dk.nodes.template.presentation.ui.main.MainActivity
import dk.nodes.template.presentation.ui.main.database.VectorBaseHelper
import dk.nodes.template.presentation.util.VectorLab.Companion.get
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ServerClient {
    private val SIZEOFBATCH = 100
    var TAG = "Server"
    private var id = 0
    var imagesinDBBeforeStart = 0

    //https://heartbeat.fritz.ai/uploading-images-from-android-to-a-python-based-flask-server-691e4092a95e
    //https://github.com/ahmedfgad/AndroidFlask/blob/master/Part%201/FlaskServer/flask_server.py
    fun connectServer(c: Context?, paths: List<String?>) {
        val jsonObjectsList: List<JSONObject> = ArrayList()
        // add method to retrieve highest index
        val thread = Thread(Runnable {
            try {
                val imagesToZipArray = arrayOfNulls<String>(SIZEOFBATCH)
                val numberOfImages = paths.size
                var iterations = numberOfImages / SIZEOFBATCH
                val startSecondIf = iterations * SIZEOFBATCH
                val rest = numberOfImages % SIZEOFBATCH
                var i = 0
                while (i < numberOfImages) {
                    if (iterations > 0) {
                        for (j in 0 until SIZEOFBATCH) {
                            imagesToZipArray[j] = paths[i + j]
                        }
                        val oneZipped = zip(imagesToZipArray, "zipped_$i")

                        //create request body
                        //here replace "file" with your parameter name
                        //Change media type according to your file type
                        val fileRequestBody: RequestBody = MultipartBody.Builder()
                                .setType(MultipartBody.FORM) //.addFormDataPart("numberOfImages", String.valueOf(numberOfImages))
                                .addFormDataPart("file", "zipped_$i",
                                        RequestBody.create(("application/zip".toMediaType()), oneZipped))
                                .build()
                        postRequest(c, fileRequestBody)
                        iterations--
                    }
                    i += SIZEOFBATCH
                }
                if (rest > 0 || numberOfImages < SIZEOFBATCH) {
                    val restImagesArray: Array<String?> = if (rest == 0) arrayOfNulls(numberOfImages) else {
                        arrayOfNulls(rest)
                    }
                    for ((y, z) in (startSecondIf until numberOfImages).withIndex()) {
                        restImagesArray[y] = paths[z]
                    }
                    val oneZipped = zip(restImagesArray, "zipped_" + "rest")

                    //create request body
                    //here replace "file" with your parameter name
                    //Change media type according to your file type
                    val fileRequestBody: RequestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM) //.addFormDataPart("numberOfImages", String.valueOf(numberOfImages))
                            .addFormDataPart("file", "zipped_" + "rest",
                                    RequestBody.create(("application/zip".toMediaType()), oneZipped))
                            .build()
                    postRequest(c, fileRequestBody)
                }
                VectorBaseHelper.exportDB()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    private fun zip(_files: Array<String?>, zipFileName: String?): File {
        val internalPath = "/data/data/" + MainActivity.context?.packageName
        Timber.d("$internalPath test234")
        val zippedFile = File(internalPath, zipFileName)
        try {
            //boolean fileCreation = zippedFile.createNewFile();
            var origin: BufferedInputStream? = null
            val dest = FileOutputStream(zippedFile)
            val out = ZipOutputStream(BufferedOutputStream(
                    dest))
            val data = ByteArray(BUFFER)
            for (idx in _files.indices) {
                val fi = FileInputStream(_files[idx])
                origin = BufferedInputStream(fi, BUFFER)
                val entry = ZipEntry(_files[idx]!!.substring(_files[idx]!!.lastIndexOf("/") + 1))
                //ZipEntry entry = new ZipEntry(_files[idx].substring(_files[idx].lastIndexOf("DCIM/")));
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, BUFFER).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return zippedFile
    }

    @Throws(IOException::class)
    fun postRequest(c: Context?, fileRequestBody: RequestBody?) {
        //to debug http posts
        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        val client = OkHttpClient.Builder()
                .readTimeout(360, TimeUnit.SECONDS)
                .connectTimeout(360, TimeUnit.SECONDS) //.addInterceptor(logging)
                .build()
        //create the request
        val fileRequest = Request.Builder().url(posturl)
                .post(fileRequestBody!!)
                .build()
        //send the request
        getResponse(client, fileRequest, id, c)
        id++
    }

    private fun getResponse(client: OkHttpClient, fileRequest: Request, id: Int, c: Context?) {
        //SYNCHRONOUS
        try {
            val vl = MainActivity.context?.let { get(it) }
            val response = client.newCall(fileRequest).execute()
            val responseText = response.body!!.string()
            val jsonObject = JSONObject(responseText)
            val vectorCollection = jsonObject.getJSONArray("vector_collection")
            for (i in 0 until vectorCollection.length()) {
                val obj = vectorCollection.getJSONObject(i)
                val path = obj.getString("path")
                val imageID = vl!!.lastImageID + 1
                vl.addIDPathPairs(imageID, path)
                val labels = obj.getString("labels")
                val probabilities = obj.getString("probabilities")
                val labelsIntArray = getIntLabels(labels)
                val probsFloatArray = getFloatProbs(probabilities)
                for (j in 0 until HomeFragment.NUMBEROFHIGHESTPROBS) {
                    vl.addLabelProbToVectors(imageID, labelsIntArray[j], probsFloatArray[j])
                }
            }
            writeToSharedPreferences(c)
        } catch (e: IOException) {
        } catch (e: JSONException) {
        }
    }

    private fun getIntLabels(labels: String): IntArray {
        val labelsS = labels.replace("[", "").replace("]", "").split(",".toRegex()).toTypedArray()
        //Log.i(TAG, "Split labels: " + Arrays.toString(labelsS));
        val labelsIntArray = IntArray(labelsS.size)
        for (i in labelsS.indices) labelsIntArray[i] = labelsS[i].toInt()
        return labelsIntArray
    }

    private fun getFloatProbs(probs: String): FloatArray {
        val probsS = probs.replace("[", "").replace("]", "").split(",".toRegex()).toTypedArray()
        val probsFloatArray = FloatArray(probsS.size)
        for (i in probsS.indices) probsFloatArray[i] = probsS[i].toFloat()
        return probsFloatArray
    }

    companion object {
        private const val BUFFER = 65536 // 524288

        //    private static final String posturl = "http://192.168.1.200:5000";
        private const val posturl = "http://192.168.0.104:5000"
    }
}