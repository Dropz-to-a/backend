// src/main/java/com/jobmanager/job_manager/dto/auth/RegisterRequest.java
package com.jobmanager.job_manager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterRequest {
    @Schema(example = "alvin")
    private String username;     // 고유

    @Schema(example = "alvin@example.com", description = "선택(미입력 가능)")
    private String email;        // 선택 (NULL 허용)

    @Schema(example = "010-1234-5678", description = "선택(미입력 가능)")
    private String phone;        // 선택

    @Schema(example = "1234", description = "평문 입력 → 서버에서 BCrypt 해시 저장")
    private String password;     // 평문 입력 -> bcrypt 저장

    @Schema(
            description = """
            역할 코드(문자열 권장 / 숫자도 허용):
            - 1 = ROLE_USER
            - 2 = ROLE_COMPANY
            - 3 = ROLE_ADMIN
            """,
            example = "ROLE_COMPANY"
    )
    private String roleCode;     // 문자열 또는 숫자(id) 모두 허용
}
