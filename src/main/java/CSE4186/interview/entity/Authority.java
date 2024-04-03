package CSE4186.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
//authority 제거하고 enum으로 role 생성
public class Authority implements Serializable {
    @Id
    @Column(name="authority")
    private String authority;
}