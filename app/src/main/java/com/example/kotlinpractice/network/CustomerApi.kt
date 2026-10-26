package com.example.kotlinpractice.network

import androidx.compose.ui.graphics.Color
import com.example.kotlinpractice.ui.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class ExhibitionStats(
    val exhibitionId: Int,
    val exhibitionName: String,
    val totalCustomers: Int
)

object CustomerApi {
    private const val BASE_URL =
        "http://10.0.2.2:3000/api/customers"

    suspend fun getCustomers(): List<Customer> =
        withContext(Dispatchers.IO) {
            try {
                val connection =
                    URL(BASE_URL).openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 1500
                connection.readTimeout = 1500

                val responseCode = connection.responseCode

                if (responseCode !in 200..299) {
                    return@withContext emptyList()
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val jsonArray = JSONArray(response)
                val result = mutableListOf<Customer>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    val status = item.optString("status", "New")
                    val (statusColor, statusBg) =
                        getStatusColorsForApi(status)

                    result.add(
                        Customer(
                            id = item.optInt("id", System.currentTimeMillis().toInt()).toString(),
                            name = item.optString("name", "Customer"),
                            exhibition = item.optString(
                                "exhibition",
                                "No Exhibition"
                            ),
                            phone = item.optString("phone", ""),
                            email = item.optString("email", ""),
                            time = formatCustomerTime(item.optString("added_at")),
                            status = status,
                            statusColor = statusColor,
                            statusBg = statusBg,
                            note = item.optString("note")
                                .takeIf { it.isNotBlank() && it != "null" }
                        )
                    )
                }

                result
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun addCustomer(
        customer: Customer,
        exhibitionId: String? = null
    ): String {
        return sendCustomerRequest(
            "POST",
            BASE_URL,
            customer,
            exhibitionId
        )
    }


    suspend fun updateCustomer(
        customer: Customer,
        exhibitionId: String
    ): String {
        return sendCustomerRequest(
            method = "PUT",
            url = "$BASE_URL/${customer.id}",
            customer = customer,
            exhibitionId = exhibitionId
        )
    }


    suspend fun deleteCustomer(customerId: String) {
        val url = URL("$BASE_URL/$customerId")

        withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "DELETE"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode

                if (responseCode !in 200..299) {
                    throw Exception("Failed to delete customer: HTTP $responseCode")
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    suspend fun getTodayVisitors(): Int = withContext(Dispatchers.IO) {
        val connection =
            URL("$BASE_URL/today-visitors").openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode

            val stream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val response = stream?.bufferedReader()?.use {
                it.readText()
            } ?: ""

            if (responseCode !in 200..299) {
                throw Exception(
                    "Today's visitors API failed: HTTP $responseCode, $response"
                )
            }

            val json = JSONObject(response)

            android.util.Log.d(
                "TodayVisitors",
                "API response: $response"
            )

            json.getInt("todayVisitors")
        } finally {
            connection.disconnect()
        }
    }

    private suspend fun sendCustomerRequest(
        method: String,
        url: String,
        customer: Customer,
        exhibitionId: String? = null
    ): String = withContext(Dispatchers.IO) {

        val connection = URL(url).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = method
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.connectTimeout = 1500
            connection.readTimeout = 1500

            val json = JSONObject().apply {
                put("name", customer.name)
                put("phone", customer.phone)
                put("email", customer.email)
                put("status", customer.status)
                put("note", customer.note)

                if (exhibitionId != null) {
                    try {
                        put("exhibitionId", exhibitionId.toInt())
                    } catch (e: Exception) {
                        // ignore if not int
                    }
                }
            }

            OutputStreamWriter(connection.outputStream).use {
                it.write(json.toString())
            }

            val responseCode = connection.responseCode

            val stream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val response = stream?.bufferedReader()?.use {
                it.readText()
            } ?: ""

            if (responseCode !in 200..299) {
                throw Exception("Customer request failed: $response")
            }

            response
        } finally {
            connection.disconnect()
        }
    }

    private fun formatCustomerTime(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.US
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val date = inputFormat.parse(dateString) ?: return dateString

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
            today.timeZone = TimeZone.getDefault()
            timeFormat.timeZone = TimeZone.getDefault()
            dateFormat.timeZone = TimeZone.getDefault()

            if (today.format(date) == today.format(Date())) {
                "Today, ${timeFormat.format(date)}"
            } else {
                dateFormat.format(date)
            }
        } catch (e: Exception) {
            dateString
        }
    }

    suspend fun getExhibitionStats(): List<ExhibitionStats> =
        withContext(Dispatchers.IO) {
            val connection = URL("$BASE_URL/exhibition-stats")
                .openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode !in 200..299) {
                    throw Exception("Failed to fetch exhibition statistics")
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val jsonArray = JSONArray(response)
                val result = mutableListOf<ExhibitionStats>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    result.add(
                        ExhibitionStats(
                            exhibitionId = item.getInt("exhibition_id"),
                            exhibitionName = item.getString("exhibition_name"),
                            totalCustomers = item.getInt("total_customers")
                        )
                    )
                }

                result
            } finally {
                connection.disconnect()
            }
        }
}

private fun getStatusColorsForApi(
    status: String
): Pair<Color,
        Color> {
    return when (status) {
        "New" -> Pair(
            androidx.compose.ui.graphics.Color(0xFF10B981),
            androidx.compose.ui.graphics.Color(0xFFECFDF5)
        )

        "Follow Up" -> Pair(
            androidx.compose.ui.graphics.Color(0xFFF59E0B),
            androidx.compose.ui.graphics.Color(0xFFFEF3C7)
        )

        "Confirmed" -> Pair(
            androidx.compose.ui.graphics.Color(0xFF3B82F6),
            androidx.compose.ui.graphics.Color(0xFFEFF6FF)
        )

        else -> Pair(
            Color.Gray,
            Color.LightGray
        )
    }


}
