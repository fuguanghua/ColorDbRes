package me.fuguanghua.r2dbc.oracle.menus;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MenuRepository extends ReactiveCrudRepository<Menu, Long> {

    @Query("SELECT * FROM Menu")
    Flux<Menu> findAllMenus();

}
