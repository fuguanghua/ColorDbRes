package me.fuguanghua.r2dbc.oracle.menus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Flux<Menu> getMenus() {
        return menuRepository.findAllMenus();
    }
}
