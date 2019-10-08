package com.ymmihw.core.java9;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.junit.Test;

/**
 * Created by adam.
 */
public class HttpClientTest {

  @Test
  public void shouldReturnSampleDataContentWhenConnectViaSystemProxy()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(BodyPublishers.ofString("Sample body")).build();

    HttpResponse<String> response = HttpClient.newBuilder().proxy(ProxySelector.getDefault())
        .build().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.body(), containsString("Sample body"));
  }

  @Test
  public void shouldNotFollowRedirectWhenSetToDefaultNever()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com"))
        .version(HttpClient.Version.HTTP_1_1).GET().build();
    HttpResponse<String> response =
        HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_MOVED_PERM));
    assertThat(response.body(), containsString("https://stackoverflow.com/"));
  }

  @Test
  public void shouldFollowRedirectWhenSetToAlways()
      throws IOException, InterruptedException, URISyntaxException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://stackoverflow.com"))
        .version(HttpClient.Version.HTTP_1_1).GET().build();
    HttpResponse<String> response = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.ALWAYS).build().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response.request().uri().toString(), equalTo("https://stackoverflow.com/"));
  }

  @Test
  public void shouldReturnOKStatusForAuthenticatedAccess()
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/basic-auth")).GET().build();
    HttpResponse<String> response = HttpClient.newBuilder().authenticator(new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("postman", "password".toCharArray());
      }
    }).build().send(request, BodyHandlers.ofString());

    assertThat(response.statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldSendRequestAsync()
      throws URISyntaxException, InterruptedException, ExecutionException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/post"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(BodyPublishers.ofString("Sample body")).build();
    CompletableFuture<HttpResponse<String>> response =
        HttpClient.newBuilder().build().sendAsync(request, BodyHandlers.ofString());

    assertThat(response.get().statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldUseJustTwoThreadWhenProcessingSendAsyncRequest()
      throws URISyntaxException, InterruptedException, ExecutionException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    CompletableFuture<HttpResponse<String>> response1 = HttpClient.newBuilder()
        .executor(executorService).build().sendAsync(request, BodyHandlers.ofString());

    CompletableFuture<HttpResponse<String>> response2 = HttpClient.newBuilder()
        .executor(executorService).build().sendAsync(request, BodyHandlers.ofString());

    CompletableFuture<HttpResponse<String>> response3 = HttpClient.newBuilder()
        .executor(executorService).build().sendAsync(request, BodyHandlers.ofString());

    CompletableFuture.allOf(response1, response2, response3).join();

    assertThat(response1.get().statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response2.get().statusCode(), equalTo(HttpURLConnection.HTTP_OK));
    assertThat(response3.get().statusCode(), equalTo(HttpURLConnection.HTTP_OK));
  }

  @Test
  public void shouldNotStoreCookieWhenPolicyAcceptNone()
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();

    HttpClient httpClient = HttpClient.newBuilder()
        .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_NONE)).build();

    httpClient.send(request, BodyHandlers.ofString());
    CookieManager cookieManager = (CookieManager) httpClient.cookieHandler().get();
    assertThat(cookieManager.getCookieStore().getCookies(), empty());
  }

  @Test
  public void shouldStoreCookieWhenPolicyAcceptAll()
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(new URI("https://postman-echo.com/get")).GET().build();

    HttpClient httpClient = HttpClient.newBuilder()
        .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();

    httpClient.send(request, BodyHandlers.ofString());
    CookieManager cookieManager = (CookieManager) httpClient.cookieHandler().get();
    assertThat(cookieManager.getCookieStore().getCookies(), not(empty()));
  }

  @Test
  public void shouldProcessMultipleRequestViaStream()
      throws URISyntaxException, ExecutionException, InterruptedException {
    List<URI> targets = Arrays.asList(new URI("https://postman-echo.com/get?foo1=bar1"),
        new URI("https://postman-echo.com/get?foo2=bar2"));

    HttpClient client = HttpClient.newHttpClient();

    List<CompletableFuture<String>> futures = targets.stream()
        .map(target -> client
            .sendAsync(HttpRequest.newBuilder(target).GET().build(), BodyHandlers.ofString())
            .thenApply(response -> response.body()))
        .collect(Collectors.toList());

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    if (futures.get(0).get().contains("foo1")) {
      assertThat(futures.get(0).get(), containsString("bar1"));
      assertThat(futures.get(1).get(), containsString("bar2"));
    } else {
      assertThat(futures.get(1).get(), containsString("bar2"));
      assertThat(futures.get(1).get(), containsString("bar1"));
    }

  }

}
