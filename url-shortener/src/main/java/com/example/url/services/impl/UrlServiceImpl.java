package com.example.url.services.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.url.domain.URL;
import com.example.url.random.UrlRandom;
import com.example.url.repositories.UrlRepository;
import com.example.url.services.UrlService;
import com.example.url.services.exceptions.UrlNotFoundException;

@Service
public class UrlServiceImpl implements UrlService {

	@Autowired
	private UrlRepository urlRepository;
	
	@Autowired
	private UrlRandom urlRandom;
	
	@Override
	public URL shortenUrl(String url) {
		if(urlRepository.existsByUrl(url)) {
			return urlRepository.findByUrl(url);
		}
		return save(url);
	}
	
	@Override
	public URL save(URL url) {
		return urlRepository.save(url);
	}
	
	@Override
	public URL save(String url) {
		String shortenUrl = prepareShortenUrl(url);
		URL newURL = new URL(null, url, shortenUrl, generateExpiration());
		return save(newURL);
	}
	
	@Override
	public List<URL> findAll() {
		var urls = urlRepository.findAll();
		validateUrlsExpiration(urls);
		return urls;
	}
	
	@Override
	public URL findByUrlShortener(String shortenedUrl) {
		validateUrlExpiration(urlRepository.findByUrlShortener(shortenedUrl));
		if(!urlRepository.existsByUrlShortener(shortenedUrl)) throw new UrlNotFoundException();
		return urlRepository.findByUrlShortener(shortenedUrl);
	}
	
	public String prepareShortenUrl(String url) {
		String urlShorten = "", randomUrl = "", http = "";
		do {
			if(url != null) {
				http = substringHttp(url);
				randomUrl = urlRandom.generateRandomUrl();
			}
			urlShorten = http + "xxx.com/" + randomUrl;
		} while(urlRepository.existsByUrlShortener(urlShorten));
		return urlShorten;
	}
	
	@Override
	public void validateUrlExpiration(URL url) {
		if(url == null) url = new URL(null, "", "", Instant.now().minusSeconds(600));
		if(Instant.now().isAfter(url.getUrlExpiration())) urlRepository.delete(url);
	}
	
	public void validateUrlsExpiration(List<URL> urls) {
		for(URL url : urls) {
			validateUrlExpiration(url);
		}
	}
	
	public Instant generateExpiration() {
		return Instant.now().plusSeconds(600);
	}

	private String substringHttp(String url) {
		String http = "";
		if(url.startsWith("https")) http = url.substring(0, 8);
		else if(url.startsWith("http"))http = url.substring(0, 7);
		return http;
	}
}
