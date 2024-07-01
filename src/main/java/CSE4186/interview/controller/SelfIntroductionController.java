package CSE4186.interview.controller;

import CSE4186.interview.annotation.LoginUser;
import CSE4186.interview.controller.dto.SelfIntroductionDto;
import CSE4186.interview.entity.SelfIntroduction;
import CSE4186.interview.service.SelfIntroductionService;
import CSE4186.interview.utils.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 면접 기능 관련
@RestController
@RequiredArgsConstructor
@RequestMapping("/selfIntroduction")
@Tag(name = "SelfIntroduction", description = "SelfIntroduction API")
public class SelfIntroductionController {

    private final SelfIntroductionService selfIntroductionService;

    @GetMapping("/list")
    @Operation(summary = "Get selfIntroductions", description = "모든 자소서 조회")
    public ApiUtil.ApiSuccessResult<SelfIntroductionDto.SelfIntroductionListResponse> getSelfIntroductionList(
            @LoginUser User loginUser,
            @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Long userId = Long.valueOf(loginUser.getUsername());
        Page<SelfIntroduction> pageSelfIntroduction = selfIntroductionService.findAllSelfIntroductions(pageable, userId);
        List<SelfIntroductionDto.Response> selfIntroductionList = pageSelfIntroduction.stream().map(SelfIntroductionDto.Response::new)
                .toList();
        return ApiUtil.success(new SelfIntroductionDto.SelfIntroductionListResponse(selfIntroductionList, pageSelfIntroduction.getTotalPages()));
    }

    @PostMapping("/save")
    @Operation(summary = "Save selfIntroductions", description = "자소서 저장")
    public ApiUtil.ApiSuccessResult<Long> createSelfIntroductionList(@Valid @RequestBody SelfIntroductionDto.CreateRequest request, @AuthenticationPrincipal User loginUser){
        SelfIntroduction selfIntroduction = selfIntroductionService.save(request, loginUser.getUsername());
        return ApiUtil.success(selfIntroduction.getId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update selfIntroductions", description = "자소서 수정")
    public ApiUtil.ApiSuccessResult<String> updateSelfIntroduction(
            @Valid @RequestBody SelfIntroductionDto.UpdateRequest request,
            @PathVariable(name = "id") Long id
            ){
        selfIntroductionService.updateSelfIntroduction(request, id);
        return ApiUtil.success("자기소개서가 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete selfIntroductions", description = "자소서 삭제")
    public ApiUtil.ApiSuccessResult<String> deleteSelfIntroduction(
            @PathVariable(name = "id") Long id
    ){
        selfIntroductionService.deleteSelfIntroduction(id);
        return ApiUtil.success("삭제되었습니다.");
    }
}
