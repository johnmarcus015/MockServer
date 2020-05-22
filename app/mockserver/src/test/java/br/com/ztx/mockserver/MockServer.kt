package br.com.ztx.mockserver

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import org.junit.AfterClass
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.net.InetAddress

@RunWith(MockitoJUnitRunner::class)
class MockServer private constructor(
    val port: Int?,
    val host: String?
) {

    data class Builder(
        var port: Int? = 8080,
        var host: String? = "/"
    ) {

        private var mocks: ArrayList<MockResponse> = ArrayList()
        private var dispatcher: QueueDispatcher = QueueDispatcher()

        fun host(host: String) = apply {
            this.host = host
        }

        fun port(port: Int) = apply {
            this.port = port
        }

        fun mocks(vararg responses: MockResponse) = apply {
            this.mocks.addAll(responses)
        }

        fun dispatcher(dispatcher: QueueDispatcher) = apply {
            this.dispatcher = dispatcher
        }

        fun build(): MockWebServer {
            server = MockWebServer()
            mocks?.let { mockResponses ->
                mockResponses.forEach { server.enqueue(it) }
            }
            dispatcher?.let {
                server.dispatcher = it
            }
            server.start(InetAddress.getLocalHost(), port!!)
            server.url(host!!)
            return server
        }

        companion object {

            private lateinit var server: MockWebServer

            @AfterClass
            @JvmStatic
            fun shudown() {
                println("SERVER SHUTDOWN!")
                server.shutdown()
            }
        }
    }
}
