package example.com.service

import com.google.protobuf.ByteString
import kotlinx.datetime.LocalDate
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor

val csvFormat: CSVFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT)
    .setIgnoreSurroundingSpaces(true)
    .setDelimiter(",")
    .build()

/**
 * Converts the CSVRecord to a ProtoBuf message
 */
fun CSVRecord.toProtoMessage(
    writer: ProtoWriter,
    fileName: String,
    now: Long,
): ByteString = writer.insertTestRow(
    get(0),
    get(1).toLong(),
    get(2).toLocalDate(),
    get(3).toBigDecimal(),
    fileName,
    now
)

/**
 * Convert a String to a LocalDateTime.
 */
fun String.toLocalDate(): LocalDate = localDateFormatter.parse(this).toLocalDate()

/**
 * @return The `Instant` equivalent of the `TemporalAccessor` instance. Only YEAR, MONTH and DAY are taken.
 */
fun TemporalAccessor.toLocalDate(): LocalDate =
    LocalDate(
        get(ChronoField.YEAR),
        get(ChronoField.MONTH_OF_YEAR),
        get(ChronoField.DAY_OF_MONTH),
    )

/**
 * A permissive date formatter used for parsing and formatting date and time strings in a specific format.
 * The format follows the pattern "yyyy-MM-dd['T'][ ][[HH:mm:ss.SSSSSS]]",
 * where each component is optional.
 */
val localDateFormatter =
    DateTimeFormatterBuilder()
        .parseLenient()
        .append(DateTimeFormatter.ofPattern("yyyy[/][-]MM[/][-]dd"))
        .optionalStart()
        .appendLiteral('T')
        .optionalEnd()
        .optionalStart()
        .appendLiteral(' ')
        .optionalEnd()
        .optionalStart()
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .optionalEnd()
        .optionalStart()
        .appendValue(ChronoField.MILLI_OF_SECOND, 3)
        .optionalEnd()
        .optionalStart()
        .appendFraction(ChronoField.MICRO_OF_SECOND, 3, 6, true)
        .optionalEnd()
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 6, 9, true)
        .optionalEnd()
        .optionalEnd()
        .toFormatter()