package CSE4186.interview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Builder
    public User(String name, String email, String password){
        this.name=name;
        this.email=email;
        this.password=password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="member_authority",
        joinColumns={@JoinColumn(name="member_id",referencedColumnName = "user_id")},
        inverseJoinColumns={@JoinColumn(name="authority_name",referencedColumnName = "authority")}
    )
    private Set<Authority> authoritySet = new HashSet<>();

    public User(String name, String email, String password, Authority authority){
        this.name=name;
        this.email=email;
        this.password=password;
        authoritySet.add(authority);
    }

    public User(String name, String email, Authority authority){
        this.name=name;
        this.email=email;
        this.password="";
        authoritySet.add(authority);
    }


}
