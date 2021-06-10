# Youtube Scraper SpringBoot Web App

This is a SpringBoot Web App for Youtube Scraper project. This repo includes:
- Youtube Scraper core, domain model, runner factory, runner scheduler
- SpringBoot persistence adapter
- Full text search adapters and initializers for H2 and PostgreSQL databases
- REST API interface
- Prebuilt Angular frontend client

Links to related repositories:  
core library: [Youtube Data Scraper Java Library](https://github.com/alexshavlovsky/youtube-scraper.git).  
frontend client: [Youtube Scraper Web App Angular Client](https://github.com/alexshavlovsky/yts-client.git).

## Build and run instructions

Build and run SpringBoot application using Maven. Open in the browser: `localhost:8080`.

## Technology Stack

Component          | Technology
---                | ---
Runtime            | Java 11, SpringBoot 2.4.4
Database support   | H2 and PostgreSQL native full text search
Mappers            | Jackson, Hibernate Validator, [ModelMapper](https://github.com/modelmapper/modelmapper)
Http client 	   | java.net.http.HttpClient, Brotli decoder

## Screenshots

<p align="center">
  <img src="screenshots/01_channel_table.png?raw=true"/>
</p>

<p align="center">
  <img src="screenshots/02_channel_summary.png?raw=true"/>
</p>

<p align="center">
  <img src="screenshots/03_channel_videos.png?raw=true"/>
</p>

<p align="center">
  <img src="screenshots/04_video_table.png?raw=true"/>
</p>

<p align="center">
  <img src="screenshots/05_comment_search.png?raw=true"/>
</p>

<p align="center">
  <img src="screenshots/06_author_summary.png?raw=true"/>
</p>
