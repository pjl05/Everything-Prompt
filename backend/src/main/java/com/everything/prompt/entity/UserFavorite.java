package com.everything.prompt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
public class UserFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long templateId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
