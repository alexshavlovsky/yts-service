--
-- PostgreSQL database dump
-- created with pg_dump -U postgres -s yts_db

-- Dumped from database version 10.16
-- Dumped by pg_dump version 10.16

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: channels; Type: TABLE; Schema: public; Owner: user1
--

CREATE TABLE public.channels (
    channel_id character varying(255) NOT NULL,
    created_date timestamp without time zone NOT NULL,
    last_updated_date timestamp without time zone,
    channel_vanity_name character varying(255),
    subscriber_count bigint,
    title character varying(255),
    video_count integer
);


ALTER TABLE public.channels OWNER TO user1;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: user1
--

CREATE TABLE public.comments (
    comment_id character varying(255) NOT NULL,
    created_date timestamp without time zone NOT NULL,
    last_updated_date timestamp without time zone,
    author_text character varying(255),
    channel_id character varying(255),
    like_count integer NOT NULL,
    published_time_text character varying(255),
    reply_count integer NOT NULL,
    text text,
    parent_id character varying(255),
    video_id character varying(255)
);


ALTER TABLE public.comments OWNER TO user1;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: user1
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO user1;

--
-- Name: videos; Type: TABLE; Schema: public; Owner: user1
--

CREATE TABLE public.videos (
    video_id character varying(255) NOT NULL,
    created_date timestamp without time zone NOT NULL,
    last_updated_date timestamp without time zone,
    published_time_text character varying(255),
    title character varying(255),
    view_count_text integer NOT NULL,
    channel_id character varying(255)
);


ALTER TABLE public.videos OWNER TO user1;

--
-- Name: worker_log; Type: TABLE; Schema: public; Owner: user1
--

CREATE TABLE public.worker_log (
    id bigint NOT NULL,
    context_id character varying(255),
    finished_date timestamp without time zone,
    message text,
    started_date timestamp without time zone,
    status character varying(255)
);


ALTER TABLE public.worker_log OWNER TO user1;

--
-- Name: channels channels_pkey; Type: CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pkey PRIMARY KEY (channel_id);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (comment_id);


--
-- Name: videos videos_pkey; Type: CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT videos_pkey PRIMARY KEY (video_id);


--
-- Name: worker_log worker_log_pkey; Type: CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.worker_log
    ADD CONSTRAINT worker_log_pkey PRIMARY KEY (id);


--
-- Name: videos fkbaube8rnq6coeqb22rt2fv8hh; Type: FK CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.videos
    ADD CONSTRAINT fkbaube8rnq6coeqb22rt2fv8hh FOREIGN KEY (channel_id) REFERENCES public.channels(channel_id);


--
-- Name: comments fkesqgvcfwlscgco0dqkdnvw8l3; Type: FK CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fkesqgvcfwlscgco0dqkdnvw8l3 FOREIGN KEY (video_id) REFERENCES public.videos(video_id);


--
-- Name: comments fklri30okf66phtcgbe5pok7cc0; Type: FK CONSTRAINT; Schema: public; Owner: user1
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fklri30okf66phtcgbe5pok7cc0 FOREIGN KEY (parent_id) REFERENCES public.comments(comment_id);


--
-- PostgreSQL database dump complete
--

