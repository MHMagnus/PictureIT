package dk.nodes.template.presentation.util

import dk.nodes.template.presentation.ui.home.HomeFragment
import dk.nodes.template.presentation.ui.main.LocalSVM.*
import kotlin.system.exitProcess


object SVM {
    private var model = svm_model()
    private const val TAG = "TestSVM"
    private fun buildParameter(): svm_parameter {
        val param = svm_parameter()
        param.svm_type = svm_parameter.C_SVC
        param.kernel_type = svm_parameter.LINEAR
        param.degree = 3
        param.gamma = 0.0 //experiment
        param.coef0 = 0.0
        param.nu = 0.5
        param.cache_size = 40.0
        param.C = 10.0 //experiment
        param.eps = 1e-3
        param.p = 0.1
        param.shrinking = 0
        param.probability = 0
        param.nr_weight = 0
        // trying to assign higher penality for class 1 --> experiment
        param.weight_label = IntArray(2)
        param.weight_label[0] = 1
        param.weight_label[1] = -1
        param.weight = DoubleArray(2)
        param.weight[0] = 1.0
        param.weight[1] = 5.0
        return param
    }

    fun buildSVMProblem(ratings: DoubleArray, trainingData: Array<Array<svm_node?>>?): svm_problem {
        val problem = svm_problem()
        problem.l = ratings.size
        problem.x = trainingData
        problem.y = ratings
        return problem
    }

    @JvmStatic
    fun buildModel(ratingsList: List<Int>, vectorValuesList: List<FloatArray>, vectorLabelsList: List<IntArray>): svm_model {
        // Building SVM parameters
        val beginning = System.nanoTime()
        val param = buildParameter()

        // Building SVM training data
        val sparseTrainingVectors = buildSparseVectorValuesArray(ratingsList.size, vectorValuesList, vectorLabelsList)
        val ratings = convertLabelsListToArray(ratingsList)

        // Building SVM problem
        val problem = buildSVMProblem(ratings, sparseTrainingVectors)
        // Checking parameters for model
        val errorMsg = svm.svm_check_parameter(problem, param)
        if (errorMsg != null) {
            System.err.print("Error: $errorMsg\n")
            exitProcess(1)
        } else {
            model = svm.svm_train(problem, param)
        }
        val end = System.nanoTime() - beginning
        //        System.out.println("Elapsed time buildModel nanosec: " + end);
        return model
    }

    private fun convertLabelsListToArray(ratingsList: List<Int>): DoubleArray {
        val startTime = System.nanoTime()
        val tmp = DoubleArray(ratingsList.size)
        for (i in ratingsList.indices) {
            tmp[i] = ratingsList[i].toDouble()
        }
        val elapsedTime = System.nanoTime() - startTime
        //        System.out.println("Elapsed time convertLabelListArray nanosec: " + elapsedTime);
        return tmp
    }

    //sparse vector with 0s
    private fun buildSparseVectorValuesArray(numberOfRatings: Int, vectorValuesList: List<FloatArray>, vectorLabelsList: List<IntArray>): Array<Array<svm_node?>> {
        val startTime = System.nanoTime()
        val probs = Array(numberOfRatings) { arrayOfNulls<svm_node>(HomeFragment.NUMBEROFHIGHESTPROBS) }
        for (i in 0 until numberOfRatings) {
            val svmNodeVector = buildOneSparseVector6(vectorValuesList[i], vectorLabelsList[i])
            //Log.i("SVM", "value added to sparse vector " + vectorValuesList.get(i) + " label index " + vectorLabelsList.get(i));
            for (j in 0 until HomeFragment.NUMBEROFHIGHESTPROBS) {
                probs[i][j] = svmNodeVector[j]
            }
        }
        val elapsedTime = System.nanoTime() - startTime
        //        System.out.println("Elapsed time buildSparseVectorValuesArray nanosec: " + elapsedTime);
        return probs
    }

    // containes all the nodes for all the ratings and builds a 2-dimensional array with it
    private fun buildVectorValuesArray(numberOfRatings: Int, vectorValuesList: List<FloatArray>): Array<Array<svm_node?>> {
        val probs = Array(numberOfRatings) { arrayOfNulls<svm_node>(1001) }
        for (i in 0 until numberOfRatings) {
            val svmNodeVector = buildOneDenseVector1001(vectorValuesList[i])
            for (j in 0..1000) {
                probs[i][j] = svmNodeVector[j]
            }
        }
        return probs
    }

    //Used in create SVM model
    // build a vector based on all 1000 features
    private fun buildOneDenseVector1001(probs: FloatArray): Array<svm_node?> {
        val svmNodeArray = arrayOfNulls<svm_node>(1001)
        for (i in 0..1000) {
            svmNodeArray[i] = buildOneIndexLabelEntry(i, probs[i].toDouble())
        }
        return svmNodeArray
    }

    //Used in create SVM model
    // build a vector based on all 1000 features
    private fun buildOneSparseVector6(probsLists: FloatArray, labelList: IntArray): Array<svm_node?> {
        val startTime = System.nanoTime()
        val svmNodeArray = arrayOfNulls<svm_node>(HomeFragment.NUMBEROFHIGHESTPROBS)
        for (i in 0 until HomeFragment.NUMBEROFHIGHESTPROBS) {
            svmNodeArray[i] = buildOneIndexLabelEntry(labelList[i], probsLists[i].toDouble())
        }
        val elapsedTime = System.nanoTime() - startTime
        //        System.out.println("Elapsed time buildOneSparseVector nanosec: " + elapsedTime);
        return svmNodeArray
    }

    // Used to create SVM model
    // Turn String values to Float for 6 best feature values AND other feature values with 0
    private fun getFloatValuesDense(values: String): FloatArray {
        val stringValues = values.split(" ".toRegex()).toTypedArray()
        val floatValues = FloatArray(1001)
        // add float probabilites to right position in 1001 long probability array
        for (i in 0..1000) {
            floatValues[i] = stringValues[i].toFloat()
        }
        return floatValues
    }

    // used in prediction
    // Building one vector -> array of nodes
    private fun buildOneSparseVector(bestProbsString: String, bestLabelsString: String): Array<svm_node?> {
        val probsArray = getFloatValues(bestProbsString)
        val labelsArray = getIntValues(bestLabelsString)
        val svmNodeArray = arrayOfNulls<svm_node>(HomeFragment.NUMBEROFHIGHESTPROBS)
        for (i in 0..5) {
            svmNodeArray[i] = buildOneIndexLabelEntry(labelsArray[i], probsArray[i].toDouble())
        }
        return svmNodeArray
    }

    // used in prediction
    // Turn String values to Float array
    private fun getFloatValues(values: String): FloatArray {
        val stringValues = values.split(" ".toRegex()).toTypedArray()
        val floatValues = FloatArray(HomeFragment.NUMBEROFHIGHESTPROBS)
        for (i in stringValues.indices) {
            floatValues[i] = stringValues[i].toFloat()
        }
        return floatValues
    }

    // one node = (index,value) --> one probability value in the vector --> if we work with the entire vector, we would need 1000
    fun buildOneIndexLabelEntry(index: Int, x: Double): svm_node {
        val startTime = System.nanoTime()
        val node = svm_node()
        node.index = index
        node.value = x
        val elapsedTime = System.nanoTime() - startTime
        //        System.out.println("Elapsed time buildOneIndexLabelEntry nanosec: " + elapsedTime);
        return node
    }

    private fun getIntValues(values: String): IntArray {
        val stringValues = values.split(" ".toRegex()).toTypedArray()
        val intValues = IntArray(stringValues.size)
        for (i in stringValues.indices) intValues[i] = stringValues[i].toInt()
        return intValues
    }

    // Todo adjust to arrays
    @JvmStatic
    fun doPredictionDistanceBased(model: svm_model?, probsLists: FloatArray, labelList: IntArray): Double {
        val startTime = System.nanoTime()
        // Get the vector of the image that we want to predict
        val node = buildOneSparseVector6(probsLists, labelList)
        val elapsedTime = System.nanoTime() - startTime
        //        System.out.println("Elapsed time doPredictionDistanceBased nanosec: " + elapsedTime);
        return svm.svm_predict_distance(model, node)
    }
}