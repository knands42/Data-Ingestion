package example.com

import com.google.cloud.bigquery.TableId
import com.google.cloud.bigquery.storage.v1.ProtoRows
import example.com.service.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Loader")

const val bucketName = "ingest_data_kotlin"
const val fileName = "output.csv"

const val projectId = "localdevelopment-392016"
const val databaseName = "ingest_demo"
const val tableName = "ingest_test"

val tableId = TableId.of(projectId, databaseName, tableName)

fun main(args: Array<String>) = runBlocking {
    val reader = Storage(projectId)
        .getBlob(bucketName, fileName)
        .also { logger.info("${it.size / 1024 / 1024} MiB") }
        .bufferedReader()

    val writer = tableId.protoWriter()
    val now = Clock.System.now().toProtoField()

    csvFormat
        .parse(reader)
        .asFlow()
        .chunked(2000)
        .parallelProcessing {
            val beforeProcess = System.nanoTime()
            val rowsBuilder = ProtoRows.newBuilder()
            for (record in it) {
                rowsBuilder.addAllSerializedRows(record.toProtoMessage(writer, fileName, now))
            }
            val rows = rowsBuilder.build()
            val afterProcess = System.nanoTime()
            writer.appendRows(rows)
            val afterRequest = System.nanoTime()
        }

    writer.close()
    return@runBlocking
}