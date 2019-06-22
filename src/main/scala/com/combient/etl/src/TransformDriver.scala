package com.combient.etl.src

import TransformHandler.genericValidation
import TransformHandler.doTransform
import org.apache.spark.sql.{SparkSession, DataFrame}


object TransformDriver {

  def main(args: Array[String]): Unit = {


    if (args.length == 0) {
      println("**************** Arguments are missing ***********")
      sys.exit(1)
    }

    println("Provided arguments are : ")
    args.foreach(arg => println(arg))

    implicit val spark: SparkSession = SparkSession.builder()
      .appName("etl-pipeline-temperature")
      .getOrCreate()


    var inputPath = args(0)
    var outputPath = args(1)


    val srcDF =     TransformHandler.read(spark, inputPath,"","CSV")

    genericValidation(srcDF)

    TransformHandler.writeWithPartition(doTransform(spark, srcDF): DataFrame, outputPath: String)

    spark.stop()

  }

}
