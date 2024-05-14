package cn.com.qjun.cardboard.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MaterialVo implements Serializable {

    private String category;
    private String createBy;
    private LocalDateTime createTime;
    private Integer id;
    private String name;
    private String updateBy;
    private LocalDateTime updateTime;

}
