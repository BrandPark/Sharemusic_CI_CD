package com.brandpark.sharemusic.domain.albums;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor
@Entity
public class Album {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;


    @Builder
    public Album(String name) {
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }
}
