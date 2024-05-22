package CSE4186.interview.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseTimeEntity {


    @Builder
    public Post(Long id, String title, String content, User user, JobField jobField) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.jobField = jobField;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ColumnDefault("0")
    @Column(name = "view_count",nullable = false)
    private Integer viewCount = 0;

    @ColumnDefault("0")
    @Column(name = "like_count",nullable = false)
    private Integer likeCount = 0;

    @ColumnDefault("0")
    @Column(name = "dislike_count",nullable = false)
    private Integer dislikeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_field_id")
    private JobField jobField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<PostVideo> postVideo = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Dislike> dislikes = new ArrayList<>();

    public void updatePost(String title, String content, JobField jobField) {
        this.title = title;
        this.content = content;
        this.jobField = jobField;
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public void addLikeCount() { this.likeCount += 1; }

    public void subLikeCount() { this.likeCount -= 1; }

    public void addDislikeCount() { this.dislikeCount += 1; }

    public void subDislikeCount() { this.dislikeCount -= 1; }
}
