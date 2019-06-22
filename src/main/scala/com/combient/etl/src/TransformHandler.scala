package com.combient.etl.src

import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}


object TransformHandler {

  def read(implicit spark: SparkSession, srcPath: String, partitionPath: String, format :String): DataFrame = {

    val srcDF = format.toUpperCase match {
      case "CSV" =>
        spark.read.option("header",true).option("inferSchema",true).csv(srcPath)
      case "AVRO" =>
        spark.read.option("basePath",srcPath).format("com.databricks.spark.avro").load(srcPath+partitionPath)
    }
    srcDF
  }



  def doTransform(spark: SparkSession,srcDF: DataFrame): DataFrame = {
    //register udf
    var get_date_from_file_name = spark.udf
      .register("get_date_from_file_name", (fullPath: String) => fullPath.split("/").last.substring(0,10))

    val transSrcDF = srcDF.withColumn("date", get_date_from_file_name(input_file_name()))
        .withColumn("year",year(col("date")))
        .withColumn("month",month(col("date")))
        .withColumn("day",dayofmonth(col("date")))
        .withColumn("ingestion_date",current_date())

    transSrcDF.show(100, false)
    transSrcDF.select("skipped_beat").distinct().show()
    transSrcDF.select("at_risk").distinct().show()

    transSrcDF.printSchema()
    transSrcDF
  }

  def genericValidation(dataDF: DataFrame): DataFrame = {

    if(dataDF.schema("at_risk").dataType != IntegerType)
      throw new Exception("There is something wrong in the source file.  The field 'at_rist' has some unusual values.")

    if(dataDF.schema("skipped_beat").dataType != DoubleType)
      throw new Exception("There is something wrong in the source file.  The field 'skipped_beat' has some unusual values.")
    dataDF
  }

  def writeWithPartition(transformedDF: DataFrame, targetPath: String) {

    transformedDF
      .write.mode(SaveMode.Append)
      .format("com.databricks.spark.avro")
      .partitionBy("year", "month", "day")
      .save(targetPath)
  }



}
