package com.combient.etl.src

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

class TestBase extends FunSuite with Matchers with BeforeAndAfter {

  System.setProperty("hadoop.home.dir","C:\\hadoop\\winutils-master\\hadoop-2.6.0\\")


  implicit val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName(getClass.getSimpleName)
    .getOrCreate()



  test("spark Version") {
    println(spark.version)
  }

}