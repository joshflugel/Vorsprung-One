package com.josh25.vorsprungone.data.network

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

/*
{
    "topRightCorner": {
        "x": 12,
        "y": 12
    },
    "roverPosition": {
        "x": 1,
        "y": 2
    },
    "roverDirection": "N",
    "movements": "LMLMLMLMM"
}
*/
class MockNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (request.url().encodedPath() == "/rovermission/init") {
            createMockResponse(request, """
                {
                    "topRightCorner": {
                        "x": 8,
                        "y": 8
                    },
                    "roverPosition": {
                        "x": 2,
                        "y": 2
                    },
                    "roverDirection": "N",
                    "movements": "MMLMMRMRMMMMRMMMLMRMRMRM"
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