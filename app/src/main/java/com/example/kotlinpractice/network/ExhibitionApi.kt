package com.example.kotlinpractice.network

import com.example.kotlinpractice.ui.Exhibition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.json.JSONObject
import java.io.OutputStreamWriter

object ExhibitionApi {

    private const val BASE_URL =
        "http://10.0.2.2:3000/api/exhibitions"

    suspend fun getExhibitions(): List<Exhibition> =
        withContext(Dispatchers.IO) {
            try {
                val connection =
                    URL(BASE_URL).openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 1500
                connection.readTimeout = 1500

                if (connection.responseCode != 200) {
                    return@withContext emptyList()
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                val jsonArray = JSONArray(response)

                val formatter = DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
                )

                val result = mutableListOf<Exhibition>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    fun formatDate(date: String): String {
                        return try {
                            LocalDate.parse(date.take(10))
                                .format(formatter)
                        } catch (e: Exception) {
                            date
                        }
                    }

                    result.add(
                        Exhibition(
                            id = item.optInt("id", System.currentTimeMillis().toInt()).toString(),
                            name = item.optString("name", "Exhibition"),
                            venue = item.optString("venue", "Venue"),
                            startDate = item.optString("startDate").let { if (it.isNotBlank() && it != "null") formatDate(it) else "Today" },
                            endDate = item.optString("endDate").let { if (it.isNotBlank() && it != "null") formatDate(it) else "Upcoming" },
                            status = item.optString("status", "Upcoming"),
                            description = item.optString("description")
                                .takeIf {
                                    it.isNotBlank() && it != "null"
                                }
                        )
                    )
                }

                result
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun addExhibition(exhibition: Exhibition): Exhibition =
        withContext(Dispatchers.IO) {

            val connection =
                URL(BASE_URL).openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )
                connection.connectTimeout = 1500
                connection.readTimeout = 1500

                val dateFormatter = DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
                )

                fun toApiDate(date: String): String {
                    return try {
                        LocalDate.parse(date, dateFormatter).toString()
                    } catch (e: Exception) {
                        LocalDate.now().toString()
                    }
                }

                val json = JSONObject().apply {
                    put("name", exhibition.name)
                    put("venue", exhibition.venue)
                    put("startDate", toApiDate(exhibition.startDate))
                    put("endDate", toApiDate(exhibition.endDate))
                    put("status", exhibition.status)
                    put("description", exhibition.description)
                }

                OutputStreamWriter(connection.outputStream).use {
                    it.write(json.toString())
                }

                val responseCode = connection.responseCode

                val responseStream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val response = responseStream
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    ?: ""

                if (responseCode !in 200..299) {
                    throw Exception(
                        "Failed to save exhibition: $response"
                    )
                }

                val item = JSONObject(response)

                fun readDate(camelCase: String, snakeCase: String): String {
                    return try {
                        val value = item.optString(
                            camelCase,
                            item.optString(snakeCase)
                        )
                        LocalDate.parse(value.take(10))
                            .format(dateFormatter)
                    } catch (e: Exception) {
                        "Today"
                    }
                }

                Exhibition(
                    id = item.optInt("id", System.currentTimeMillis().toInt()).toString(),
                    name = item.optString("name", exhibition.name),
                    venue = item.optString("venue", exhibition.venue),
                    startDate = readDate("startDate", "start_date"),
                    endDate = readDate("endDate", "end_date"),
                    status = item.optString("status", exhibition.status),
                    description = item.optString("description")
                        .takeIf { it.isNotBlank() && it != "null" }
                )

            } finally {
                connection.disconnect()
            }
        }


    suspend fun updateExhibition(exhibition: Exhibition) =
        withContext(Dispatchers.IO) {

            val connection = URL("$BASE_URL/${exhibition.id}")
                .openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "PUT"
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )
                connection.connectTimeout = 1500
                connection.readTimeout = 1500

                val dateFormatter = DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
                )

                fun toApiDate(date: String): String {
                    return try {
                        LocalDate.parse(date, dateFormatter).toString()
                    } catch (e: Exception) {
                        LocalDate.now().toString()
                    }
                }

                val json = JSONObject().apply {
                    put("name", exhibition.name)
                    put("venue", exhibition.venue)
                    put("startDate", toApiDate(exhibition.startDate))
                    put("endDate", toApiDate(exhibition.endDate))
                    put("status", exhibition.status)
                    put("description", exhibition.description)
                }

                OutputStreamWriter(connection.outputStream).use {
                    it.write(json.toString())
                }

                val responseCode = connection.responseCode

                if (responseCode !in 200..299) {
                    val error = connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: "HTTP $responseCode"

                    throw Exception("Failed to update exhibition: $error")
                }

            } finally {
                connection.disconnect()
            }
        }

}
