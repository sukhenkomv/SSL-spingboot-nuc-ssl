package com.example.ssl_check;

import java.time.Duration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@RestController
public class SslCheckController {

	private static final String[] URLS = {
			"https://take-lets-office.novotelecom.ru",
			"https://take-nuc-office.novotelecom.ru",
			"https://yandex.ru",
			"https://www.sberbank.ru/ru/person"
	};

	private final RestClient restClient = RestClient.builder()
			.requestFactory(clientHttpRequestFactory())
			.build();

	private static SimpleClientHttpRequestFactory clientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(Duration.ofSeconds(10));
		factory.setReadTimeout(Duration.ofSeconds(30));
		return factory;
	}

	@GetMapping("/ssl_check")
	public String sslCheck() {
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>SSL Check</title>")
				.append("<style>")
				.append("body{font-family:Arial,sans-serif;margin:20px;}")
				.append(".result{margin-bottom:20px;border:1px solid #ccc;padding:10px;}")
				.append(".error{color:red;}")
				.append("pre{background:#f5f5f5;padding:10px;overflow:auto;max-height:400px;white-space:pre-wrap;word-break:break-all;}")
				.append("</style></head><body><h1>SSL Check</h1>");

		for (String url : URLS) {
			html.append("<div class=\"result\"><h2>").append(escape(url)).append("</h2>");
			try {
				String body = restClient.get()
						.uri(url)
						.retrieve()
						.body(String.class);
				html.append("<p><b>Status:</b> 200 OK</p><h3>Response body:</h3><pre>")
						.append(escape(body))
						.append("</pre>");
			} catch (RestClientResponseException e) {
				html.append("<p><b>Status:</b> ").append(e.getStatusCode().value()).append(" ")
						.append(escape(e.getStatusText())).append("</p>")
						.append("<p class=\"error\">").append(escape(String.valueOf(e.getMessage()))).append("</p>")
						.append("<h3>Response body:</h3><pre>")
						.append(escape(e.getResponseBodyAsString()))
						.append("</pre>");
			} catch (Exception e) {
				html.append("<p><b>Status:</b> FAILED</p><p class=\"error\">")
						.append(escape(e.getClass().getSimpleName() + ": " + e.getMessage()))
						.append("</p>");
			}
			html.append("</div>");
		}

		html.append("</body></html>");
		return html.toString();
	}

	private static String escape(String s) {
		if (s == null) {
			return "";
		}
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
