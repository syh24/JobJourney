package CSE4186.interview.service;

import CSE4186.interview.controller.dto.BoardRequestDto;
import CSE4186.interview.entity.Board;
import CSE4186.interview.entity.User;
import CSE4186.interview.repository.BoardRepository;
import CSE4186.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    public Board addBoard(BoardRequestDto request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("no user"));
        return boardRepository.save(Board.builder()
                        .user(findUser)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build()
        );
    }
}
