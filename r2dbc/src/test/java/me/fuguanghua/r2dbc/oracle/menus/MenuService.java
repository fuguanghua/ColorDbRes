package me.fuguanghua.r2dbc.oracle.menus;

import reactor.core.publisher.Flux;

public interface MenuService {
    Flux<Menu> getMenus();
}
