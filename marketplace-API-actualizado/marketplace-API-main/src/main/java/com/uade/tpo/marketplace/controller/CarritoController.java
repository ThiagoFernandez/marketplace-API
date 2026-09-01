package com.uade.tpo.marketplace.controller;

import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.service.CarritoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/user/{userId}")
    public Carrito getCart(@PathVariable Long userId) {
        return carritoService.getCarritoByUser(userId);
    }

    @PostMapping("/user/{userId}/add")
    public Carrito addItem(@PathVariable Long userId, @RequestParam Long productId, @RequestParam Integer cantidad) {
        return carritoService.addItem(userId, productId, cantidad);
    }

    @DeleteMapping("/user/{userId}/clear")
    public void clear(@PathVariable Long userId) {
        carritoService.vaciarCarrito(userId);
    }
}
