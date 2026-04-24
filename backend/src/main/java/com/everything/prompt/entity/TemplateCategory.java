package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("template_category")
public class TemplateCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String code;
    private String icon;
    private String description;
    private Integer sortOrder;
    private Integer isFree;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
