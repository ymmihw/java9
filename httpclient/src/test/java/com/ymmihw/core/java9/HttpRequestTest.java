package com.ymmihw.core.java9;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Created by adam.
 */
public class HttpRequestTest {

  @Test
  public void shouldReturnStatusOKWhenSendGetRequest()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldUseHttp2WhenWebsiteUsesHttp2()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://stackoverflow.com"))
        .version(HttpClient.Version.HTTP_2).GET().build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.version(), equalTo(HttpClient.Version.HTTP_2));
  }

  @Test
  public void shouldFallbackToHttp1_1WhenWebsiteDoesNotUseHttp2()
      throws IOException, InterruptedException, URISyntaxException, NoSuchAlgorithmException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get"))
        .version(HttpClient.Version.HTTP_2).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.version(), equalTo(HttpClient.Version.HTTP_2));
  }

  @Test
  public void shouldReturnStatusOKWhenSendGetRequestWithDummyHeaders()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get"))
        .headers("key1", "value1", "key2", "value2").GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldReturnStatusOKWhenSendGetRequestTimeoutSet()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get"))
        .timeout(Duration.of(10, SECONDS)).GET().build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldReturnNoContentWhenPostWithNoBody()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .POST(BodyPublishers.noBody()).build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldReturnSampleDataContentWhenPostWithBodyText()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(BodyPublishers.ofString("Sample request body")).build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.body(), containsString("Sample request body"));
  }

  @Test
  public void shouldReturnSampleDataContentWhenPostWithInputStream()
      throws IOException, InterruptedException, URISyntaxException {
    byte[] sampleData = "Sample request body".getBytes();
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(BodyPublishers.ofInputStream(() -> new ByteArrayInputStream(sampleData))).build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.body(), containsString("Sample request body"));
  }

  @Test
  public void shouldReturnSampleDataContentWhenPostWithByteArrayProcessorStream()
      throws IOException, InterruptedException, URISyntaxException {
    byte[] sampleData = "Sample request body".getBytes();
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(BodyPublishers.ofByteArray(sampleData)).build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.body(), containsString("Sample request body"));
  }

  @Test
  public void shouldReturnSampleDataContentWhenPostWithFileProcessorStream()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(BodyPublishers.ofFile(Paths.get("src/test/resources/sample.txt"))).build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.body(), containsString("Sample file content"));
  }

}
