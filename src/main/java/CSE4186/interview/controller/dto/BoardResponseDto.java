package CSE4186.interview.controller.dto;

import CSE4186.interview.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Builder
@Getter
public class BoardResponseDto {
    private Long BoardId;
    private String title;
    private String content;

    public BoardResponseDto(Long boardId, String title, String content) {
        BoardId = boardId;
        this.title = title;
        this.content = content;
    }
}
