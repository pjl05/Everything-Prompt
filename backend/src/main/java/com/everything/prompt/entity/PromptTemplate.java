package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("prompt_template")
public class PromptTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String description;
    private String content;
    private Long categoryId;
    private String tags;
    private Integer isPremium;
    private Integer isOfficial;
    private Integer usageCount;
    private Double rating;
    private Integer status;
    private Long createUserId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
