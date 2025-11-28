package com.migramer.store.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.Carrito;
import com.migramer.store.entities.Producto;
import com.migramer.store.entities.Tienda;
import com.migramer.store.models.AddToCarritoRequest;
import com.migramer.store.models.MessageResponse;
import com.migramer.store.repository.CarritoRepository;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductosService productosService;

    @Autowired
    private TiendaService tiendaService;

    @Autowired
    @Lazy
    private CarritoService self;

    private final Logger logger = LoggerFactory.getLogger(CarritoService.class);

    public MessageResponse addProductToShoppingCart(AddToCarritoRequest addToCarritoRequest) {
        addProductToCart(addToCarritoRequest);
        return new MessageResponse("Producto agregado al carrito exitosamente");
    }

    public void addProductToCart(AddToCarritoRequest addToCarritoRequest) {
        self.executeAddProductToCart(addToCarritoRequest);
    }

    @Async
    public void executeAddProductToCart(AddToCarritoRequest addToCarritoRequest) {

        try {

            Tienda tienda = tiendaService.getTiendaEntityByUUID(addToCarritoRequest.getUuidTienda());

            Producto producto = productosService
                    .findByCodigoBarrasAndEstatusAndTiendaForProducto(addToCarritoRequest.getCodigoBarras(), true,
                            tienda);

            logger.info(producto.getDescripcion());
            saveShoppingCart(addToCarritoRequest.getCantidad(), producto);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }

    }

    public void saveShoppingCart(Integer cantidad, Producto producto) {

        Carrito carrito = new Carrito();
        carrito.setCantidad(cantidad);
        carrito.setEstatus(true);
        carrito.setProductoForCarrito(producto);
        carrito.setFechaCreacion(LocalDateTime.now());
        carritoRepository.save(carrito);

    }

}