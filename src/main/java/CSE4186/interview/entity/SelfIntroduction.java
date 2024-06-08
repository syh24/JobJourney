package CSE4186.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SelfIntroduction extends BaseTimeEntity {

    @Builder
    public SelfIntroduction(String title, User user) {
        this.title = title;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "self_introduction_id")
    private Long id;

    private String title;

    @OneToMany(mappedBy = "selfIntroduction", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<SelfIntroductionDetail> selfIntroductionDetailList = new ArrayList<>();

    @OneToMany(mappedBy = "selfIntroduction", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Question> quetions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void changeTitle(String title) {
        this.title = title;
    }
}
