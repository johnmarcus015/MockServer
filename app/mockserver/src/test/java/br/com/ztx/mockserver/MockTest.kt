package br.com.ztx.mockserver

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MockTest {

    private lateinit var server: MockWebServer

    @Before
    fun `run before each test`() {

        val routes: HashMap<String, MockResponse> = HashMap()
        routes["/error1"] = MockResponse().setResponseCode(301).setBody("{ Not authorized }")
        routes["/everything"] = MockResponse().setResponseCode(200).setBody("{ Connection ok }")
        routes["/error2"] = MockResponse().setResponseCode(500).setBody("{ Server error }")

        val dispatcher = MockDispatcher.Builder()
            .routes(routes)
            .build()

        server = MockServer.Builder()
            .dispatcher(dispatcher)
            .port(3000)
            .build()
    }

    @After
    fun `run after each test`() {
        server.shutdown()
    }

    @Test
    fun `test new server mock implementation`() {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val request = Request.Builder().url(server.url("/").toString()).build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            println(response.body)
        }
    }
}

