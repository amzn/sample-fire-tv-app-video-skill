/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import androidx.annotation.NonNull;

import java.io.Serializable;

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 */
public class Movie implements Serializable {
    static final long serialVersionUID = 727566175075960653L;
    private long id;
    private String title;
    private String movie_id;
    private String description;
    private String bgImageUrl;
    private String cardImageUrl;
    private String videoUrl;
    private String studio;
    private Integer ordinal;

    public Movie() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMovieId() {
        return movie_id;
    }

    public void setMovieId(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public Integer getOrdinal() { return ordinal; }

    public void setOrdinal(Integer ordinal) { this.ordinal = ordinal; }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", movie_id='" + movie_id + '\'' +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundImageUrl='" + bgImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", ordinal='" + ordinal + '\'' +
                '}';
    }

    @NonNull
    @Override
    protected Object clone() {
        final Movie clonedMovie = new Movie();
        clonedMovie.setId(this.getId());
        clonedMovie.setMovieId(this.getMovieId());
        clonedMovie.setTitle(this.getTitle());
        clonedMovie.setDescription(this.getDescription());
        clonedMovie.setStudio(this.getStudio());
        clonedMovie.setCardImageUrl(this.getCardImageUrl());
        clonedMovie.setBackgroundImageUrl(this.getBackgroundImageUrl());
        clonedMovie.setVideoUrl(this.getVideoUrl());
        clonedMovie.setOrdinal(this.getOrdinal());
        return clonedMovie;
    }
}
