package example.com.service

import com.google.auth.oauth2.GoogleCredentials
import java.util.logging.Level

typealias UtilLogger = java.util.logging.Logger

val defaultCredentials: GoogleCredentials by lazy { GoogleCredentials.getApplicationDefault() }

fun attenuateGoogleLogs() {
    UtilLogger.getLogger("com.google").apply {
        level = Level.WARNING
    }
}