package com.example.url.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.url.domain.URL;

public interface UrlRepository extends JpaRepository<URL, Long>{

	boolean existsByUrl(String url);
	
	boolean existsByUrlShortener(String shortenedUrl);
	
	URL findByUrlShortener(String shortenedUrl);
	
	URL findByUrl(String url);
}
