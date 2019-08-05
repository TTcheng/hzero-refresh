package me.wcc.http.entity;

import lombok.*;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/4 下午4:25
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ResponseEntity {
    private Integer statusCode;
    private Boolean success;
    private String body;
    private String errorMessage;
}
