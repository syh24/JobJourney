package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.BoardRequestDto;
import CSE4186.interview.controller.dto.BoardResponseDto;
import CSE4186.interview.entity.Board;
import CSE4186.interview.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public List<BoardResponseDto> getAllBoards() {
        return boardService.findAllBoards()
                .stream().map(board -> new BoardResponseDto(board.getId(), board.getTitle(), board.getTitle()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Board addBoard(@RequestBody BoardRequestDto request) {
        return boardService.addBoard(request);
    }
}
