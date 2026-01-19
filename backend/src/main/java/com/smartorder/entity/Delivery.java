package com.smartorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_delivery")
public class Delivery {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long merchantUserId;
    
    private String deliveryName;
    
    private String deliveryPhone;
    
    private Integer deliveryStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
