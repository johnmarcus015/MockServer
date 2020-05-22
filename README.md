# MockServer
Library created for easily mock a server in android unit tests
## How to use
### Config routes of the mock server
```kotlin 
val routes: HashMap<String, MockResponse> = HashMap()
routes["/error1"] = MockResponse().setResponseCode(301).setBody("{ Not authorized }")
routes["/everything"] = MockResponse().setResponseCode(200).setBody("{ Connection ok }")
routes["/error2"] = MockResponse().setResponseCode(500).setBody("{ Server error }")

val dispatcher = MockDispatcher.Builder()
    .routes(routes)
    .build()
```
### Init the server with routes
```kotlin
server = MockServer.Builder()
    .dispatcher(dispatcher)
    .port(3000)
    .build()
```
### Example of a unit test with MockServer
```kotlin
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
```
## Getting Started 
1. Add the JitPack repository to your build file
```shell
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
2. Add the dependency 
```shell
dependencies {
    implementation 'com.github.johnmarcus015:MockServer'
}
```
