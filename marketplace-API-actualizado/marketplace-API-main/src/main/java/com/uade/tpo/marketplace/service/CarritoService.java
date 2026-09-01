package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.ItemCarrito;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.repository.CarritoRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CarritoService(CarritoRepository carritoRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.carritoRepository = carritoRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Carrito getCarritoByUser(Long userId) {
        return carritoRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));
                    Carrito nuevoCarrito = Carrito.builder().user(user).items(new ArrayList<>()).build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    @Transactional
    public Carrito addItem(Long userId, Long productId, Integer cantidad) {
        Carrito carrito = getCarritoByUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

        ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            ItemCarrito item = ItemCarrito.builder()
                    .carrito(carrito)
                    .product(product)
                    .cantidad(cantidad)
                    .build();
            carrito.getItems().add(item);
        }
        return carritoRepository.save(carrito);
    }

    @Transactional
    public void vaciarCarrito(Long userId) {
        Carrito carrito = getCarritoByUser(userId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }
}
