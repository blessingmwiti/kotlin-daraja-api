package com.blessingmwiti.kotlindarajaapi.util

import android.util.Base64
import android.util.Base64.encodeToString
import com.blessingmwiti.kotlindarajaapi.data.DarajaException
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


internal fun getDarajaPassword(businessShortCode: String, passKey: String, timestamp: String): String {
    val password = businessShortCode + passKey + timestamp
    val password1 = encodeToString((password).toByteArray(), Base64.NO_WRAP)
    return password1
}

internal fun String.getDarajaPhoneNumber(): String {
    val phoneNumber = this.replace("\\s".toRegex(),"")

    return when {
        phoneNumber.matches(Regex("^(?:254)?[17](?:\\d\\d|0[0-8]|(9[0-2]))\\d{6}\$")) -> phoneNumber
        phoneNumber.matches(Regex("^0?[17](?:\\d\\d|0[0-8]|(9[0-2]))\\d{6}\$")) ->
            phoneNumber.replaceFirst("0", "254")
        phoneNumber.matches(Regex("^(?:\\+254)?[17](?:\\d\\d|0[0-8]|(9[0-2]))\\d{6}\$")) ->
            phoneNumber.replaceFirst("+", "")
        else -> throw DarajaException("Invalid phone number format provided: $this")
    }
}

internal fun String.getDarajaAmount(): String {
    return if (this.matches(Regex("^\\d{1,6}\$"))) {
        this
    } else {
        throw DarajaException("Invalid amount format provided: $this")
    }
}

/**Format current timestamp to YYYYMMDDHHmmss format*/
internal fun Instant.getDarajaTimestamp(): String {
    val currentDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())

    val year = currentDateTime.year
    val month = currentDateTime.monthNumber.asFormattedWithZero()
    val dayOfMonth = currentDateTime.dayOfMonth.asFormattedWithZero()
    val hour = currentDateTime.hour.asFormattedWithZero()
    val minutes = currentDateTime.minute.asFormattedWithZero()
    val seconds = currentDateTime.second.asFormattedWithZero()

    return "$year$month$dayOfMonth$hour$minutes$seconds"
}

internal fun Int.asFormattedWithZero(): Comparable<*> = when (this < 10) {
    true -> "0$this"
    false -> this
}

