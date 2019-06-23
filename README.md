# Pipeline

Spark project Name : etl_pipeline_temperature
Airflow DAG Name : temperature_etl_pipeline_daily

### Features

* Adapt dynamic schema changes using InferSchema
* Data types based on the values
* History data will be structured by AVRO files in the storage system(HDFS)
* Dates from filenames are included into each record so history can be tracked easily
* Partitioned by file name based Year,Month,Day in the storage system level.
* Data validations are applied based on data types for specific field. Job will if any unexpected field values are received.


## Requirement Information

|  Requirement | Comments |
| ------ | ------ |
| Consume all the data and put them in a store of your choice | Daily data will be stored into HDFS in structured AVRO files and partitioned by year,month,day which came from file name   |
| Handle the change of schema (if any)  | Dynamic schema feature(InferSchema) from Spark CSV reader is used so changes in the header will be handled automatically |
| Handle the potential data issues (if any) | Data type based validations are used for specific fields so if any unexpected value received then pipeline will fail and throw error. Example : at_risk column should not be decimal/text/boolean values.   |
| Keep in mind that the data flows in everyday so adding new data to the provided dataset should be consumed by the ETL pipeline efficiently  | It will be handled by Dynamic schema. I believe the data flows every day and not a one time dump. |
| Use of Docker and Docker-compose for the setup of the test environment | The test environment is based on Spark 2.1 , Java 8 (Scala for dev) and Airflow. All Docker information are attached in the same project. Need time to implement more features |
| Sample output directory structure for daily ETL | output/year=2018/month=12/day=21/part-00000-eae5c38c-a303-496f-b735-8a853142c6ea.avro |


#### Final result - daily ETL


 +-----------+-------+------------+------------------+----------+--------------+----+-----+---+
|temperature|at_risk|skipped_beat|             price|      date|ingestion_date|year|month|day|
+-----------+-------+------------+------------------+----------+--------------+----+-----+---+
|       HIGH|      0|         1.0|  76091.3639674888|2018-12-12| 1560636000000|2018|   12| 12|
|        LOW|      0|         1.0|22158.673267039365|2018-12-12| 1560636000000|2018|   12| 12|
|        LOW|      0|         0.0|2346.1348396927824|2018-12-12| 1560636000000|2018|   12| 12|
|       HIGH|      1|         0.0|  49532.2235497147|2018-12-12| 1560636000000|2018|   12| 12|
|     MEDIUM|      0|         1.0| 9975.043099338847|2018-12-12| 1560636000000|2018|   12| 12|
|       HIGH|      0|         0.0| 48670.98895817491|2018-12-12| 1560636000000|2018|   12| 12|
|     MEDIUM|      0|         3.0| 27741.16524190955|2018-12-12| 1560636000000|2018|   12| 12|
|     MEDIUM|      0|         1.0| 8620.461372122476|2018-12-12| 1560636000000|2018|   12| 12|
|     MEDIUM|      1|         0.0| 175607.7451551359|2018-12-12| 1560636000000|2018|   12| 12|
|     MEDIUM|      0|         1.0|31344.520336495516|2018-12-12| 1560636000000|2018|   12| 12|
+-----------+-------+------------+------------------+----------+--------------+----+-----+---+


#### Final schema - daily ETL

root
 |-- temperature: string (nullable = true)
 |-- at_risk: integer (nullable = true)
 |-- skipped_beat: double (nullable = true)
 |-- price: double (nullable = true)
 |-- date: string (nullable = true)
 |-- year: integer (nullable = true)
 |-- month: integer (nullable = true)
 |-- day: integer (nullable = true)
 |-- ingestion_date: date (nullable = false)




### Step by step process to test the program/application in Docker .
#This flow is created on Window's Docker desktop.
#Login to the Docker desktop and go the directory where all configuration files (Dockerfile, compose file and etc)
#Move to the directory where this project's home directory is available in local.

docker build -f dev.Dockerfile -t spark .
docker network create dev
docker-compose up


 docker run -d -p 8083:8083 -v C:/Users/scj470/Docker/dags/:/usr/local/airflow/dags  puckel/docker-airflow webserver -network dev
     #docker ps
     #docker exec -ti <AIRFLOW_CONTAINER_NAME> bash
     #exit
 docker run -it -p 8088:8088 -p 8042:8042 -p 4041:4040 --name driver -h driver spark:latest bash
	#If the name already exist then please change it to new one.
	#Now you have entered to the spark container.

 mkdir jars  input  #Inside spark container

 #Open new PowerShell terminal and copy below files to specific containers.
 #If you changed the container name in previous steps then please modify the same here.  "driver" is the current name.
 docker cp C://Raj//workspace_intelliJ//etl_pipeline//target//etl_pipeline-1.0.0.jar driver:workspace/jars/
 docker cp C:/Raj/workspace_intelliJ/etl_pipeline/src/test/resources/input_src/2018-12-02.csv driver:workspace/input/

  docker ps
 #Get the container name of Airflow and paste it to below command.
 docker exec -ti <container name> bash
 airflow scheduler -D #Some time need to trigger the scheduler manually
 airflow test temperature_etl_pipeline entry 2019-06-01

### URLs in docker test environment

 http://localhost:8083/admin/
 http://127.0.0.1:8080
 http://127.0.0.1:8081

#### Note :
This solution is for daily load.  The input provided data set contains different file content in it, one of the example is below. So advise to run the flows daily not for one time dump.

Header orders are different:
2018-07-10 --> temperature,skipped_beat,at_risk
2018-11-17 --> temperature,at_risk,skipped_beat,price

