package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_call_log")
public class AiCallLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String callType;
    private String inputContent;
    private String outputContent;
    private String model;
    private Integer tokensUsed;
    private BigDecimal cost;
    private String ipAddress;
    private Integer status;
    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
