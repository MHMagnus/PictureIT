package dk.nodes.template.presentation.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.presentation.R
import java.io.File
import java.io.FileOutputStream


fun sharePhotoPicker(photoPath: String?, context: Context) {
    val myBitmap = BitmapFactory.decodeFile(photoPath)
    val file = File(context.externalCacheDir, "myFileName.png")
    val fileOutputStream = FileOutputStream(file)

    myBitmap?.compress(Bitmap.CompressFormat.PNG, 20, fileOutputStream)
    fileOutputStream.flush()
    fileOutputStream.close()
    val fileProviderUri = getUriForFile(context, context.applicationContext.packageName
            + ".provider", file);

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        putExtra(Intent.EXTRA_STREAM, fileProviderUri)
        type = "image/jpeg"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    } else {
        Toast.makeText(context, "Error sharing photo", Toast.LENGTH_SHORT).show()
    }
}

 fun getRatedPhotoList(data: List<Photo>, rating: Int): List<RatedPhoto> {
    val res = mutableListOf<RatedPhoto>()
    for (item in data) {
        val imageId = VectorLab.getIDFromPath(getShortPath(item.photoPath))
        val vectorValues = VectorLab.queryProbsAsFloats(imageId)
        val vectorLabels = VectorLab.queryLabelsAsInts(imageId)
        res.add(RatedPhoto(item.photoPath,vectorValues, vectorLabels, rating))
    }
    return res
}

fun getRatedPhoto(data: String, rating: Int): RatedPhoto {
    val imageId = VectorLab.getIDFromPath(getShortPath(data))
    val res = RatedPhoto(data, VectorLab.queryProbsAsFloats(imageId), VectorLab.queryLabelsAsInts(imageId), rating)
    println("$res RatedPhoto with labels and confidences")
    return res
}



fun getFullPath(shortPath: String?): String? {
    var fullPath = ""
    if (shortPath != null) {
        return if (shortPath.startsWith(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).toString())) {
            shortPath
        } else {
            fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + "/Camera/" + shortPath
            fullPath
            //                shortPath
        }
    }
    return fullPath
}

fun getShortPath(fullPath: String): String? {
    val shortPath: String
    if (fullPath.startsWith(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString())) {
        shortPath = fullPath.replace(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/", "")
        return shortPath
    }
    return fullPath
}