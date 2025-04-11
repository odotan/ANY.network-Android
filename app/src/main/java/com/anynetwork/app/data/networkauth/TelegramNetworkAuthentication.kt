package com.anynetwork.app.data.networkauth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class TelegramNetworkAuthentication @Inject constructor() {

    private val baseUrl = "https://gatewayapi.telegram.org/"
    private val token = "AAE5EQAAVUstF9McmeEkOy2vbh3vjw-rPzflNDAC2C-TaA"
    private var requestId: String? = null

    private val client = OkHttpClient()
    private val gson = Gson()

    private fun headers(): Headers = Headers.Builder()
        .add("Authorization", "Bearer $token")
        .add("Content-Type", "application/json")
        .build()

    suspend fun sendCode(phone: String): String? = withContext(Dispatchers.IO) {
        val url = "${baseUrl}sendVerificationMessage"

        val params = mapOf(
            "phone_number" to phone,
            "code_length" to 6,
            "ttl" to 60,
            "payload" to "",
            "callback_url" to "https://any.network"
        )

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            gson.toJson(params)
        )

        val request = Request.Builder()
            .url(url)
            .headers(headers())
            .post(body)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) throw IOException("Unexpected code ${response.code}")

        val responseBody = response.body?.string()
        Timber.i("telegram auth code response: $responseBody")

        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val json = gson.fromJson<Map<String, Any>>(responseBody, mapType)

        if (json["ok"] == true) {
            val result = json["result"] as? Map<*, *>
            return@withContext result?.get("request_id") as? String
        } else {
            throw Exception(json["error"]?.toString() ?: "Unknown error")
        }
    }

    suspend fun verify(code: String): Boolean = withContext(Dispatchers.IO) {
        val url = "${baseUrl}checkVerificationStatus"

        val currentRequestId = requestId ?: throw MissingRequestIdException()

        val params = mapOf(
            "request_id" to currentRequestId,
            "code" to code
        )

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            gson.toJson(params)
        )

        val request = Request.Builder()
            .url(url)
            .headers(headers())
            .post(body)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) throw IOException("Unexpected code ${response.code}")

        val responseBody = response.body?.string()
        Timber.i("telegram auth verify response: $responseBody")

        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val json = gson.fromJson<Map<String, Any>>(responseBody, mapType)

        if (json["ok"] == true) {
            return@withContext true
        } else {
            throw Exception(json["error"]?.toString() ?: "Unknown error")
        }
    }

    class MissingRequestIdException : Exception("Missing Request ID")
}