# Google Storage Sink
A small backup tool which collects data from a given PubSub topic and writes it to the designated Google storage
bucket. The application is built upon `Apache Beam` which is a stream/batch data processing tool. [click here]
(https://beam.apache.org/get-started/beam-overview/) to learn more about the Apache Beam model. Due to the source
of the application (PubSub), the Dataflow job that will be created is a `streaming` job.

## Requirements

* Java 1.8 SDK
* Google Service account with the following access:
    * Compute Viewer
    * Dataflow Developer
    * Storage Admin to the bucket where it will write to
    * PubSub Admin from the PubSub topic where it will read from

## Deployment

Since the application is a streaming application, it's as simple as running the following commands:

Build the application with all the added dependencies

```
    $ ./gradlew clean fatJar
```

Run the application with a storage sink

```
    java -jar build/libs/snowplow-data-sink-all-1.0-SNAPSHOT.jar
        --runner=DataflowRunner
        --sink=storage
        --windowDuration=1
        --subscription=projects/PROJECT_ID/subscriptions/SUBSCRIPTION
        --outputDirectory=gs://BUCKET/YYYY/MM/DD/
        --outputFilenamePrefix=enriched-
        --tempLocation=gs://BUCKET/tmp
        --stagingLocation=gs://BUCKET/stg
        --subnetwork=regions/europe-west1/subnetworks/default
        --region=europe-west1
        --diskSizeGb=50
```

Or run the application with a bigquery sink (be sure to create the dataset first)

```
    java -jar build/libs/snowplow-data-sink-all-1.0-SNAPSHOT.jar
        --subscription=projects/bigquery-1316/subscriptions/test
        --separator=\t
        --sink=bigquery
        --projectId=PROJECT_ID
        --datasetId=DATASET_ID
        --tableName=TABLE_NAME
        --sourceScheme=name:string,age:integer:price:float64
```

###Supported options:

Option | Description | Required | Default
--- | --- | --- | ---
subscription | The Cloud Pub/Sub subscription to read from | No | None
sink | Pick the sink to write to. supported values: bigquery, storage | Yes | None
outputDirectory | The directory to output files to. Must end with a slash | No | None
outputFilenamePrefix | The filename prefix of the files to write to | No | None
outputFilenameSuffix | The suffix of the files to write | No | None
outputShardTemplate | The shard template of the output file. Specified as repeating sequences | No | W-P-SS-of-NN
numShards | The maximum number of output shards produced when writing | No | 1
windowDuration | The window duration in seconds for aggregation of PubSub data | No | 120
projectId | The Google Project ID | Yes | None
datasetId | The BigQuery dataset name | No | None
tableName | The BigQuery table name | No | None
sourceScheme | Definition of the source data in Pubsub. | No | None
separator | Separator of the incoming Pubsub data (examples: ,:;) | No | None

Apache Beam supports multiple default parameters when running the application. The complete list [can be found here]
(https://cloud.google.com/dataflow/pipelines/specifying-exec-params).

The image below shows the Dataflow job that will be created with the commands shown above.


<img src="dataflow.png" width="300">
