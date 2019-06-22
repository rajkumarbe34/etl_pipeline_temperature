package com.combient.etl.src

import java.net.URL

import org.apache.spark.sql.{DataFrame, SparkSession}

class TimeZoneHandleTest extends TestBase {

  System.setProperty("hadoop.home.dir","C:\\hadoop\\winutils-master\\hadoop-2.6.0\\")

  var csvSrcPath = ""
  var targetPath = ""

  implicit val sparkSession: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName(getClass.getSimpleName)
    .getOrCreate()

  before{
     csvSrcPath = "C:\\Raj\\workspace_intelliJ\\etl_pipeline\\src\\test\\resources\\input_src\\*.csv"
     targetPath = "C:\\Raj\\workspace_intelliJ\\etl_pipeline\\src\\test\\resources\\base_target\\"
  }


  test("temperature"){
    val srcDF =     TransformHandler.read(spark, csvSrcPath,"","CSV")
    //val outDF = TransformHandler.doTransform(spark,srcDF)
    srcDF.printSchema()
    //srcDF.count() > 0 shouldBe true
  }



  test("test for write with partitions") {

    val srcDF =     TransformHandler.read(spark, csvSrcPath,"","CSV")
    val outDF = TransformHandler.doTransform(spark,srcDF)
    TransformHandler.writeWithPartition(TransformHandler.genericValidation(outDF) : DataFrame, targetPath: String)
  }

  test("dry validation of written data"){
    val resultDF = TransformHandler.read(sparkSession, targetPath,"","AVRO")
    resultDF.show()
  }


  def toPath(fileName: String): String = this.getClass.getClassLoader.getResource(fileName).getPath
  def getResource(fileName: String): URL = this.getClass.getClassLoader.getResource(fileName)
  def toExternalForm(fileName: String): String = getResource(fileName).toExternalForm
}

