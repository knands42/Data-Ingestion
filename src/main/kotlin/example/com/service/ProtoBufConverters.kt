package example.com.service

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Converts the Instant object to a protocol buffer field value represented as a Long (millis).
 */
fun Instant.toProtoField(): Long = toEpochMilliseconds() * 1_000

/**
 * The local date is converted into an Int (epochDays)
 */
fun LocalDate.toProtoField(): Int = toEpochDays()
