package CSE4186.interview.controller.dto;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link CSE4186.interview.entity.Board}
 */
@ToString
@NoArgsConstructor
@Builder
@Getter
public class BoardRequestDto implements Serializable {
    private String title;
    private String content;
    private Long userId;

    public BoardRequestDto(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}