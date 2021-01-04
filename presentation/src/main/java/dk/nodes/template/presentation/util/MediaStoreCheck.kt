package dk.nodes.template.presentation.util

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dk.nodes.template.presentation.ui.main.database.ServerClientTwo
import dk.nodes.template.presentation.util.SharedPreferenceUtil.getLastAnalysisInMilliSecSharedPreferences
import java.util.*

class MediaStoreCheck(private val mContext: Context) {
    private val TAG = "MedieStoreCheck"
    private var cursor: Cursor? = null
    private lateinit var projection: Array<String>
    private val newImageList: MutableList<String?> = ArrayList()

    private fun checkForNewImages(): List<String?> {
        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            projection = arrayOf(
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME)
            cursor = mContext.contentResolver.query(contentUri, projection, null, null, MediaStore.Images.ImageColumns._ID)
            val lastUpdate = getLastAnalysisInMilliSecSharedPreferences(mContext)
            if (cursor?.moveToFirst()!!) {
                val dateTakenIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)
                val displayNameIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                while (cursor!!.moveToNext()) {
                    var filename = displayNameIndex?.let { cursor?.getString(it) }
                    val millis = dateTakenIndex?.let { cursor?.getLong(it) }
                    if (millis != null) {
                        if (millis >= lastUpdate) {
                            if (filename != null) {
                                filename = if (filename.startsWith("Screen")) {
                                    getFullPath("Screenshots/$filename")
                                } else {
                                    getFullPath("Camera/$filename")
                                }
                            }
                            newImageList.add(filename)
                        }
                    }
                }
            }
        } catch (ex: NullPointerException) {
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }
        return newImageList
    }


    fun analyseNewImages() {
        val newImages = checkForNewImages()
        println("New images: " + newImages.toTypedArray().contentToString())
        println("New images size: " + newImages.size)
        if (newImages.isNotEmpty()) {
            val serverClient = ServerClientTwo()
            serverClient.connectServer(mContext, newImages)
        }
    }
}