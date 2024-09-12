# Data Ingestion

## About the project

This project is about data ingestion. It is a simple project that reads data from a csv file and writes it to a
database. It is written in Kotlin and it fetch a csv file from Google Cloud storage and process using Kotlin Flow to
persist into BigQuery.

## How to run

1- Generate the sample csv file

```shell
make gen-csv
```

2- Log into Google Cloud

```shell
make gcloud-login
```

3- Upload to Google Cloud Storage

```shell
make gcloud-upload
```

4- Start the application

```shell
make run
```

## Credits

The project was made following the tutorial from [Kotlin by Jetbrains](youtube.com/watch?v=0urxga3q3ty)
