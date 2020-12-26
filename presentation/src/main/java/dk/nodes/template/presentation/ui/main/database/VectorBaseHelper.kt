package dk.nodes.template.presentation.ui.main.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.Environment
import android.util.Log
import dk.nodes.template.presentation.BuildConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class VectorBaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    // scala code to generate the columns
    // scala> for(w <- 0 to 1000) {println( "VectorDBSchema.VectorTable.Cols.PROB" + i + " + " + """", """" + " + "); i= i+1;}
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + IDPathSchema.IDPathTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                IDPathSchema.IDPathTable.Cols.IMAGEID + ", " +
                IDPathSchema.IDPathTable.Cols.PATH +
                ")"
        )
        db.execSQL("CREATE TABLE IF NOT EXISTS " + VectorDBSchema.VectorTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                VectorDBSchema.VectorTable.Cols.IMAGEID + ", " +
                VectorDBSchema.VectorTable.Cols.SEEN + ", " +
                VectorDBSchema.VectorTable.Cols.FEATURES + ", " +
                VectorDBSchema.VectorTable.Cols.PROBS +  //", " +
                ")"
        )
    }

    override fun onOpen(database: SQLiteDatabase) {
        super.onOpen(database)
        if (Build.VERSION.SDK_INT >= 28) {
            database.disableWriteAheadLogging()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        private const val TAG = "VectorBaseHelper"
        private const val VERSION = 1
        private const val DATABASE_NAME = "vectorDB.db"

        // Copying SQLite DB to external storage
        fun exportDB() {
            //Thread thread = new Thread() {
            //public void run() {
            val startTime = System.nanoTime()
            println("exportDB was called")
            try {
                val backUpDir = File(Environment.getExternalStorageDirectory(), "DBBackup2")
                if (!backUpDir.exists()) {
                    backUpDir.mkdirs()
                } else {
                }
                if (Environment.getExternalStorageDirectory().canWrite()) {
                    //                        String backupDBPath = backUpDir + "\\vector_db_backup.db";
                    val currentDB = File("/data/data/ ${BuildConfig.LIBRARY_PACKAGE_NAME}/databases/vectorDB.db")
                    // /data/data/ng.com.obkm.exquisitor/databases/vectorDB.db
                    val backupDB = File(backUpDir, "vectorDB.db")
                    if (currentDB.exists()) {
                        val src = FileInputStream(currentDB).channel
                        val dst = FileOutputStream(backupDB).channel
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                    } else {
                    }
                } else {
                }
            } catch (e: Exception) {
            }
            // }
            //};
            val endTime = System.nanoTime()
            //thread.start();
        }

        fun importDB(backupDB: File?, internalPath: String?, context: Context?) {
            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        val newImportedDB = File(internalPath, "vectorDB.db")
                        val fileCreation = newImportedDB.createNewFile()
                        val src = FileInputStream(backupDB).channel
                        val dst = FileOutputStream(newImportedDB).channel
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                    } catch (e: Exception) {
                    }
                }
            }
            thread.start()
        }
    }
}