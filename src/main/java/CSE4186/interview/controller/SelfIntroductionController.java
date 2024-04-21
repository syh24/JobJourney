package CSE4186.interview.controller;

import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.service.SelfIntroductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// 면접 기능 관련
@RestController
@RequiredArgsConstructor
@RequestMapping("/selfIntroduction")
@Tag(name = "SelfIntroduction", description = "SelfIntroduction API")
public class SelfIntroductionController {

    private final SelfIntroductionService selfIntroductionService;

    @GetMapping("/List")
    @Operation(summary = "Get selfIntroductions", description = "모든 자소서를 조회")
    public ResponseEntity<List<SelfIntroductionDto.Response>> getSelfIntroductionList(@PathVariable(name = "id") Long id){
        List<SelfIntroductionDto.Response> response=selfIntroductionService.findAllSelfIntroductions(id)
                .stream().map(SelfIntroductionDto.Response::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Save")
    @Operation(summary = "Save selfIntroductions", description = "자소서를 저장")
    public ResponseEntity<Long> saveSelfIntroductionList(SelfIntroductionDto.Request request){
        selfIntroductionService.save(request.getId(), request.getContent());
        return ResponseEntity.ok(request.getId());
    }
}
