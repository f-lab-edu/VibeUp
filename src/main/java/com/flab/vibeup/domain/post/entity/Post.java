package com.flab.vibeup.domain.post.entity;

import com.flab.vibeup.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
@Builder
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MusicVendor vendor;

    @Column(nullable = false)
    private String musicUrl;

    private String caption;

    @ElementCollection
    @CollectionTable(name = "post_hashtags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Post(MusicVendor vendor, String musicUrl, String caption, User user) {
        this.vendor = vendor;
        this.musicUrl = musicUrl;
        this.caption = caption;
        this.user = user;
    }

    public void updateCaption(String newCaption) {
        this.caption = newCaption;
    }
}
