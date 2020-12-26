package dk.nodes.template.presentation.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

import dk.nodes.template.presentation.ui.home.HomeFragment
import dk.nodes.template.presentation.ui.main.database.IDPathSchema.*
import dk.nodes.template.presentation.ui.main.database.VectorBaseHelper
import dk.nodes.template.presentation.ui.main.database.VectorCursorWrapper
import dk.nodes.template.presentation.ui.main.database.VectorDBSchema.VectorTable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class VectorLab @Inject constructor(context: Context) {
    private val mContext: Context = context.applicationContext
    fun addLabelProbToVectors(imageID: Int, label: Int, prob: Float) {
        val values = getContentValuesForLabelProb(imageID, label, prob)
        mDatabase.insert(VectorTable.NAME, null, values)
    }

    // Step one: list of paths
    // Step two: SELECT feature FROM db WHERE imagePath = path;
    // Loop iterate through list
    fun queryUnseenProbs(): Map<Int, MutableList<Float>?>? {
        val unseenProbs: MutableMap<Int, MutableList<Float>?> = HashMap()
        val whereClause = "CAST(" + VectorTable.Cols.SEEN + " as TEXT) = ?"
        val cursor = queryItemsVectors(
                whereClause, arrayOf(0.toString()))
        return cursor.use { cursor: VectorCursorWrapper ->
            if (cursor.count == 0) {
                return null
            }
            var list: MutableList<Float>?
            while (cursor.moveToNext()) {
//                String path = cursor.getPath();
                val imageID = cursor.imageIDVectorsTable
                if (!unseenProbs.containsKey(imageID)) {
                    list = ArrayList()
                    list.add(cursor.floatProbs)
                    unseenProbs[cursor.imageIDVectorsTable] = list
                } else {
                    list = unseenProbs[imageID]
                    list!!.add(cursor.floatProbs)
                    unseenProbs[imageID] = list
                }
            }
            unseenProbs
        }
    }

    fun queryUnseenFeatures(): Map<Int, MutableList<Int>?>? {
        val unseenLabels: MutableMap<Int, MutableList<Int>?> = HashMap()
        val WHERE = "CAST(" + VectorTable.Cols.SEEN + " as TEXT) = ?"
        val cursor = queryItemsVectors(
                WHERE, arrayOf(0.toString()))
        return try {
            if (cursor.count == 0) {
                return null
            }
            var list: MutableList<Int>?
            while (cursor.moveToNext()) {
                val imageID = cursor.imageIDVectorsTable
                //                Log.i(TAG, "keyset " + unseenLabels.entrySet());
                if (!unseenLabels.containsKey(imageID)) {
                    list = ArrayList()
                    list.add(cursor.featureInts)
                    unseenLabels[imageID] = list
                } else {
                    list = unseenLabels[imageID]
                    list?.add(cursor.featureInts)
                    unseenLabels[imageID] = list
                }
            }
            unseenLabels
        } finally {
            cursor.close()
        }
    }

    fun updateSeen(imageID: Int) {
        val values = ContentValues()
        values.put("seen", 1)
        mDatabase.update(
                VectorTable.NAME, values, "CAST(" + IDPathTable.Cols.IMAGEID + " as TEXT) = ?", arrayOf(imageID.toString()))
    }

    fun removeSeen() {
        val values = ContentValues()
        //Log.i("home", "remove rated is called.");
        values.put("seen", 0)
        mDatabase.update(VectorTable.NAME, values, "CAST(" + VectorTable.Cols.SEEN + " as TEXT)" + "= ?", arrayOf(1.toString()))
    }

    fun addIDPathPairs(imageID: Int, path: String) {
        val values = ContentValues()
        values.put(IDPathTable.Cols.IMAGEID, imageID)
        values.put(IDPathTable.Cols.PATH, path)
        mDatabase.insert(IDPathTable.NAME, null, values)
    }

    fun getPathFromID(imageID: Int): String? {
        val idString = imageID.toString()
        val WHERE = "CAST(" + IDPathTable.Cols.IMAGEID + " as TEXT) = ?"
        val cursor = queryItemsIDPath(
                WHERE, arrayOf(idString))
        return try {
            if (cursor.count == 0) {
                return null
            }
            cursor.moveToFirst()
            cursor.imagePathIDPathTable
        } finally {
            cursor.close()
        }
    }

    fun sizeOfDatabase() {
        val dbSize = mDatabase.rawQuery("SELECT * FROM IDPathTable", null)

        Timber.d("amount of rows in IDPathTable = ${dbSize.count} test123")
//        return dbSize

    }

    fun queryRandomUnseen(): Int {
        val WHERE = "CAST(" + VectorTable.Cols.SEEN + " as TEXT) =? "
        val ORDERBY = "RANDOM() LIMIT 1"
        val cursor = queryItemsVectorsRandom(
                WHERE, arrayOf(0.toString()),
                ORDERBY
        )
        return try {
            if (cursor.count == 0) {
                return -1
            }
            cursor.moveToFirst()
            cursor.imageIDVectorsTable
        } finally {
            cursor.close()
        }
    }

    fun deleteEntryFromDB(path: String) {
        var imageID = -1
        val cursor = queryItemsIDPath(
                IDPathTable.Cols.PATH + " = ?", arrayOf(path))
        imageID = try {
            if (cursor.count == 0) {
                return
            }
            cursor.moveToFirst()
            cursor.imageIDIDPathTable
        } finally {
            cursor.close()
        }
        val idString = imageID.toString()
        val WHERE = "CAST(" + IDPathTable.Cols.IMAGEID + " as TEXT) = ?"
        println("imageID in deleteEntry: $imageID")
        mDatabase.delete(VectorTable.NAME,
                WHERE, arrayOf(idString))
        mDatabase.delete(
            IDPathTable.NAME,
                IDPathTable.Cols.PATH + " = ?", arrayOf(path))
    }

    // Source: https://stackoverflow.com/questions/35333864/get-the-max-of-id-and-store-value-in-sqlite-db
    val lastImageID: Int
        get() {
            var maxID = -1
            val cursor = mDatabase.rawQuery("Select max(IMAGEID) from IDPathTable", null)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    maxID = cursor.getInt(0)
                    cursor.moveToNext()
                }
                cursor.close()
            }
            return maxID
        }

    companion object {
        private const val TAG = "VectorLab"
        private var sVectorLab: VectorLab? = null
        lateinit var mDatabase: SQLiteDatabase
        @JvmStatic
        operator fun get(context: Context): VectorLab? {
            if (sVectorLab == null) {
                sVectorLab = VectorLab(context)
            }
            return sVectorLab
        }

        private fun queryItemsVectors(whereClause: String, whereArgs: Array<String>): VectorCursorWrapper {
            val cursor = mDatabase.query(
                    VectorTable.NAME,
                    null,  // columns - null selects all columns
                    whereClause,
                    whereArgs,
                    null,  // groupBy
                    null,  // having
                    null // orderBy
            )
            return VectorCursorWrapper(cursor)
        }

        private fun queryItemsVectorsRandom(whereClause: String, whereArgs: Array<String>, orderByArguments: String): VectorCursorWrapper {
            val cursor = mDatabase.query(
                    VectorTable.NAME,
                    null,  // columns - null selects all columns
                    whereClause,
                    whereArgs,
                    null,  // groupBy
                    null,  // having
                    orderByArguments // orderBy
            )
            return VectorCursorWrapper(cursor)
        }

        private fun queryItemsIDPath(whereClause: String?, whereArgs: Array<String?>): VectorCursorWrapper {
            val cursor = mDatabase.query(
                    IDPathTable.NAME,
                    null,  // columns - null selects all columns
                    whereClause,
                    whereArgs,
                    null,  // groupBy
                    null,  // having
                    null // orderBy
            )
            //        Log.i("home", "VectorLab are we looking in idpath schema db ? " +  IDPathSchema.IDPathTable.NAME);
            return VectorCursorWrapper(cursor)
        }

        @JvmStatic
        fun queryProbsAsFloats(imageID: Int): FloatArray {
//        Log.i("home", "query probsAsFloats is called with " + imageID);
            val startTime = System.nanoTime()
            val imageIDS = imageID.toString()
            //        Log.i("home", imageIDS);
            val probsArray = FloatArray(HomeFragment.NUMBEROFHIGHESTPROBS)
            var i = 0
            val WHERE = "CAST(" + IDPathTable.Cols.IMAGEID + " as TEXT) = ?"
            //String WHERE = IDPathSchema.IDPathTable.Cols.IMAGEID+" = ?";
            val cursor = queryItemsVectors(
                    WHERE, arrayOf(imageIDS))
            return try {
//            Log.i("home", "Cursor size: " + cursor.getCount());
                while (cursor.moveToNext()) {
//                Log.i("home", "Entered while loop");
                    probsArray[i] = cursor.floatProbs
                    //                Log.i("home", "Value added to probsArray: " + probsArray[i]);
                    i++
                }
                probsArray
            } finally {
                cursor.close()
                //            long elapsedTime = System.nanoTime() - startTime;
//            System.out.println("Elapsed time queryProbsAsFloats nanosec: " + elapsedTime);
            }
        }

        @JvmStatic
        fun queryLabelsAsInts(imageID: Int): IntArray {
//        Log.i(TAG, "query one float is called");
            val startTime = System.nanoTime()
            val labelsArray = IntArray(HomeFragment.NUMBEROFHIGHESTPROBS)
            var i = 0
            //String WHERE = IDPathSchema.IDPathTable.Cols.IMAGEID+ " = ?";
            val WHERE = "CAST(" + IDPathTable.Cols.IMAGEID + " as TEXT) = ?"
            val cursor = queryItemsVectors(
                    WHERE, arrayOf(imageID.toString()))
            return try {
                while (cursor.moveToNext()) {
                    labelsArray[i] = cursor.featureInts
                    i++
                }
                labelsArray
            } finally {
                cursor.close()
                //            long elapsedTime = System.nanoTime() - startTime;
//            System.out.println("Elapsed time queryLabelsAsInts nanosec: " + elapsedTime);
            }
        }

        /* private static ContentValues getContentValuesForLabelProb(int imageID, int topLabels, float topProbs) {
//        Log.i("DB", "getContentValuesForLabelProb was called");
        ContentValues values = new ContentValues();
        values.put("CAST("+IDPathSchema.IDPathTable.Cols.IMAGEID+" as TEXT) = ?", imageID);
        values.put("CAST("+VectorTable.Cols.SEEN+" as TEXT) = ?", 0);
        values.put(VectorTable.Cols.FEATURES, topLabels);
        values.put(VectorTable.Cols.PROBS, topProbs);
        return values;
    }*/
        private fun getContentValuesForLabelProb(imageID: Int, topLabels: Int, topProbs: Float): ContentValues {
//        Log.i("DB", "getContentValuesForLabelProb was called");
            val values = ContentValues()
            values.put(VectorTable.Cols.IMAGEID, imageID)
            values.put(VectorTable.Cols.SEEN, 0)
            values.put(VectorTable.Cols.FEATURES, topLabels)
            values.put(VectorTable.Cols.PROBS, topProbs)
            return values
        }

        @JvmStatic
        fun removeSeenForID(id: Int) {
            val values = ContentValues()
            //        Log.i("DB", "remove rated is called.");
            values.put("seen", 0)
            mDatabase.update(VectorTable.NAME, values, "CAST(" + IDPathTable.Cols.IMAGEID + " as TEXT) = ? AND " + "CAST(" + VectorTable.Cols.SEEN + " as TEXT) = ?", arrayOf(id.toString(), 1.toString()))
        }

        @JvmStatic
        fun getIDFromPath(path: String?): Int {
            //Log.i("DB", "getIDFromPath was called" + path);
            val cursor = queryItemsIDPath(IDPathTable.Cols.PATH + " = ?", arrayOf(path))
            return try {
                if (cursor.count == 0) { return -1 }
                cursor.moveToFirst()
                cursor.imageIDIDPathTable
            } finally {
                cursor.close()
            }
        }
    }

    init {
        mDatabase = VectorBaseHelper(mContext).writableDatabase
    }
}