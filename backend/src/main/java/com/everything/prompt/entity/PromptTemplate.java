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

    /**
     * 效果示例 - 展示使用此提示词能得到什么结果
     */
    private String exampleResult;

    /**
     * 适用场景
     */
    private String scenario;

    /**
     * 使用说明
     */
    private String usageGuide;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
