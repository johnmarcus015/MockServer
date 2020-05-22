package br.com.ztx.mockserver

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest

class MockDispatcher private constructor() {

    class Builder : QueueDispatcher() {

        private lateinit var routes: HashMap<String, MockResponse>

        override fun dispatch(request: RecordedRequest): MockResponse {
            routes?.let {
                for (route in it.keys) {
                    val routeResponse = it[route]
                    return when (cleanPathParams(request.path!!)) {
                        route -> routeResponse!!
                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }
            return MockResponse().setResponseCode(404)
        }

        fun routes(routes: HashMap<String, MockResponse>) = apply {
            this.routes = routes
        }

        fun build(): QueueDispatcher {
            return this
        }

        private fun cleanPathParams(requestPath: String): String {
            val separatorForParams = "?"
            return if (requestPath.contains(separatorForParams)) {
                val paramsIdentifier = requestPath.indexOf(separatorForParams)
                requestPath.substring(0, paramsIdentifier)
            } else {
                requestPath
            }
        }
    }
}