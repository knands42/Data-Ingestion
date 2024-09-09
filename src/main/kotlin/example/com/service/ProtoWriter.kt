package example.com.service

import com.google.cloud.bigquery.TableId
import com.google.cloud.bigquery.storage.v1.*
import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import com.google.protobuf.DynamicMessage
import kotlinx.datetime.LocalDate
import java.io.Closeable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * Util extension function to simplify the creation of a ProtoWriter on
 * the table.
 */
fun TableId.protoWriter() = ProtoWriter(this)

class ProtoWriter(
    tableId: TableId
) : Closeable {

    val descriptor: Descriptors.Descriptor

    private val streamWriter: StreamWriter
    private val client: BigQueryWriteClient = BigQueryWriteClient.create()

    init {
        val parentTable: TableName = TableName.of(
            tableId.project,
            tableId.dataset,
            tableId.table
        )

        val newWriteStream = WriteStream.newBuilder()
            .setType(WriteStream.Type.COMMITTED)
            .build()

        val createWriteStreamRequest = CreateWriteStreamRequest.newBuilder()
            .setParent(parentTable.toString())
            .setWriteStream(newWriteStream)
            .build()

        val writeStream = client.createWriteStream(createWriteStreamRequest)

        descriptor = BQTableSchemaToProtoDescriptor.convertBQTableSchemaToProtoDescriptor(writeStream.tableSchema)
        streamWriter = StreamWriter.newBuilder(
            "projects/${tableId.project}/datasets/${tableId.dataset}/tables/${tableId.table}/streams/_default",
            client
        ).setWriterSchema(
            ProtoSchemaConverter.convert(descriptor)
        ).build()
    }

    suspend fun appendRows(rows: ProtoRows): AppendRowsResponse =
        streamWriter.append(rows).await()

    override fun close() {
        streamWriter.close()
        client.shutdown()
        client.close()
        client.awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Builds the protobuf message for row insertion
 */
fun ProtoWriter.insertTestRow(
    a: String, b: Long, c: LocalDate, d: BigDecimal, fileName: String, now: Long
): ByteString =
    DynamicMessage.newBuilder(descriptor)
        .apply {
            setField(descriptor.fields[0], a)
            setField(descriptor.fields[1], b)
            setField(descriptor.fields[2], c.toProtoField())
            setField(descriptor.fields[3], BigDecimalByteStringEncoder.encodeToNumericByteString(d))
            setField(descriptor.fields[4], fileName)
            setField(descriptor.fields[5], now)

        }.build().toByteString()