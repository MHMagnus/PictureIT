package dk.nodes.template.presentation.ui.main.database

import android.database.Cursor
import android.database.CursorWrapper
import dk.nodes.template.presentation.ui.main.database.VectorDBSchema.VectorTable

class VectorCursorWrapper
/**
 * Creates a cursor wrapper.
 *
 * @param cursor The underlying cursor to wrap.
 */
(cursor: Cursor?) : CursorWrapper(cursor) {
    val floatProbs: Float
        get() = getFloat(getColumnIndex(VectorTable.Cols.PROBS))

    val featureInts: Int
        get() = getInt(getColumnIndex(VectorTable.Cols.FEATURES))

    val imageIDVectorsTable: Int
        get() = getInt(getColumnIndex(VectorTable.Cols.IMAGEID))

    val imageIDIDPathTable: Int
        get() = getInt(getColumnIndex(IDPathSchema.IDPathTable.Cols.IMAGEID))

    val imagePathIDPathTable: String
        get() = getString(getColumnIndex(IDPathSchema.IDPathTable.Cols.PATH))
}