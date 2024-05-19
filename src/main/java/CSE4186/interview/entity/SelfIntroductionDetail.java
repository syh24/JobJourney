package CSE4186.interview.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfIntroductionDetail {

    @Builder
    public SelfIntroductionDetail(String title, String content, String type, SelfIntroduction selfIntroduction) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.selfIntroduction = selfIntroduction;
        this.selfIntroduction.getSelfIntroductionDetailList().add(this);
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "self_introduction_id")
    private SelfIntroduction selfIntroduction;
}
