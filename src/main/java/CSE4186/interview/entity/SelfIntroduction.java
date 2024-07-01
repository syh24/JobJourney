package CSE4186.interview.entity;

import CSE4186.interview.controller.dto.SelfIntroductionDto;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SelfIntroduction extends BaseTimeEntity {
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

    public SelfIntroductionDto.Response toSelfIntroductionResponse() {
        return SelfIntroductionDto.Response.builder()
                .id(this.id)
                .title(this.title)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public void changeTitle(String title) {
        this.title = title;
    }
}
