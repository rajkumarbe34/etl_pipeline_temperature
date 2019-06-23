from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime, timedelta
from airflow.contrib.operators.spark_submit_operator import SparkSubmitOperator

args = {
    'owner': 'RAJ',
    'depends_on_past': False,
    'start_date': datetime(2019, 6, 21),
    'email': ['rajkumarbe34@gmail.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=1),
}

dag = DAG('temperature_etl_pipeline_daily', schedule_interval='@daily', default_args=args)

t1 = BashOperator(
    task_id='entry',
    bash_command='echo "Entry point"',
    dag=dag)


spark_submit = SparkSubmitOperator(
    task_id='transform',
    conn_id='spark_default',
    conf={
		'master':'spark://spark-master:7077',
        'spark.hadoop.yarn.timeline-service.enabled': 'false',
        'spark.authenticate': 'false',
    },
	java_class='com.combient.etl.src.TransformDriver',
    application='/usr/local/airflow/jars/etl_pipeline-1.0.0.jar',
	application_args=["input/","output/"],
    dag=dag
)

t3 = BashOperator(
    task_id='exit',
    bash_command='echo "Job is success"',
    dag=dag)

	
t1 >> spark_submit >> t3


