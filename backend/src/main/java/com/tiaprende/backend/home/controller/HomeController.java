package com.tiaprende.backend.home.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tiaprende.backend.home.dto.HomeResponse;
import com.tiaprende.backend.home.service.HomeService;
import com.tiaprende.backend.login.session.AuthSession.SessionUser;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public HomeResponse carregar(@AuthenticationPrincipal SessionUser usuario) {
        return homeService.carregarHome(usuario);
    }
}
