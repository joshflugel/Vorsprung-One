package com.josh25.vorsprungone.data.network

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

class MockNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (request.url().encodedPath() == "/rovermission/init") {
            createMockResponse(request, """
                {
                    "topRightCorner": {
                        "x": 5,
                        "y": 5
                    },
                    "roverPosition": {
                        "x": 1,
                        "y": 2
                    },
                    "roverDirection": "N",
                    "movements": "LMLMLMLMM"
                }
            """.trimIndent())
        } else {
            Response.Builder()
                .code(404)
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message("Not Found")
                .body(ResponseBody.create(MediaType.get("text/plain"), "Not Found"))
                .build()
        }
    }

    private fun createMockResponse(request: Request, body: String): Response {
        return Response.Builder()
            .code(200)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(ResponseBody.create(MediaType.get("application/json"), body))
            .build()
    }
}