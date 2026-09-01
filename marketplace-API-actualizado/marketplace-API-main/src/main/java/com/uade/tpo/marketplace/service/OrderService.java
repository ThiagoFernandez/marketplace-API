package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Carrito;
import com.uade.tpo.marketplace.entity.ItemCarrito;
import com.uade.tpo.marketplace.entity.ItemOrder;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CarritoService carritoService;

    public OrderService(OrderRepository orderRepository, CarritoService carritoService) {
        this.orderRepository = orderRepository;
        this.carritoService = carritoService;
    }

    @Transactional
    public Order checkout(Long userId) {
        Carrito carrito = carritoService.getCarritoByUser(userId);
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Order order = Order.builder()
                .user(carrito.getUser())
                .fecha(LocalDateTime.now())
                .estado("CONFIRMADA")
                .build();

        double total = 0.0;
        for (ItemCarrito ic : carrito.getItems()) {
            ItemOrder io = ItemOrder.builder()
                    .order(order)
                    .product(ic.getProduct())
                    .cantidad(ic.getCantidad())
                    .precioUnitario(ic.getProduct().getPrecio())
                    .build();
            order.getItems().add(io);
            total += ic.getCantidad() * ic.getProduct().getPrecio();
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        carritoService.vaciarCarrito(userId);
        return savedOrder;
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
