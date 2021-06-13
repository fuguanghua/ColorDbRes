package me.fuguanghua.r2dbc.oracle.menus;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
public class Menu {
    @Id
    private Long id;
    private String name;
    private Long sort = 999L;
    private String path;
    private String component;
    private Integer type;
    private String permission;
    private String component_name;
    private String icon;
    private Boolean cache;
    private Boolean hidden;
    private Long pid;
    private Boolean i_frame;
    private Timestamp create_time;
    private String out_id;
    private String isdisabled;
}
