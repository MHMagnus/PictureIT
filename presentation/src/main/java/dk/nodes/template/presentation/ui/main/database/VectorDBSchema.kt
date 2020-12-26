package dk.nodes.template.presentation.ui.main.database

//command to generate 1000 columns in scala
// scala> for(w <- 0 to 1000){println("public static final String PROB" + i +" = " + """"prob"""+i+'"'+";"); i=i+1;}
class VectorDBSchema {
    object VectorTable {
        const val NAME = "Vectors"

        object Cols {
            const val IMAGEID = "imageID"
            const val SEEN = "seen"
            const val FEATURES = "features"
            const val PROBS = "probs"
        }
    }
}