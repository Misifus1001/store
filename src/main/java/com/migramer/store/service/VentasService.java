package com.migramer.store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.Carrito;
import com.migramer.store.entities.Producto;
import com.migramer.store.entities.Usuario;
import com.migramer.store.entities.Ventas;
import com.migramer.store.models.MessageResponse;
import com.migramer.store.repository.VentasRepository;

import jakarta.transaction.Transactional;

@Service
public class VentasService {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    @Lazy
    private VentasService self;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CarritoService carritoService;

    private final Logger logger = LoggerFactory.getLogger(VentasService.class);

    public MessageResponse generarVenta(String email) {
        venta(email);
        return new MessageResponse("Venta guardada exitosamente");
    }

    public void venta(String email) {
        self.executarGuardadoVenta(email);
    }

    @Async
    public void executarGuardadoVenta(String email) {

        try {
            Usuario usuario = usuarioService.getUsuarioByEmail(email);

            List<Carrito> carritoList = new ArrayList<>(usuario.getCarritoList());

            cerrarVenta(carritoList);
            carritoService.eliminarCarritoProductoList(carritoList);
        } catch (Exception e) {
            logger.error("ERROR: ", e);

        }
    }

    private void cerrarVenta(List<Carrito> carritoList) {

        for (Carrito carrito : carritoList) {
            guardarVenta(carrito.getProductoForCarrito(), carrito.getCantidad());
        }
    }

    @Transactional
    private void guardarVenta(Producto producto, Integer productosVendidos) {

        Integer stockNuevo = (producto.getStock() - productosVendidos);
        BigDecimal totalPagado = producto.getPrecio().multiply(BigDecimal.valueOf(productosVendidos));

        Ventas ventas = new Ventas();
        ventas.setFechaVenta(LocalDateTime.now());
        ventas.setCodigoBarrasProducto(producto.getCodigoBarras());
        ventas.setDescripcionProducto(producto.getDescripcion());
        ventas.setPrecioProducto(producto.getPrecio());
        ventas.setStockAnterior(producto.getStock());
        ventas.setStockNuevo(stockNuevo);
        ventas.setTotalPagado(totalPagado);
        ventasRepository.save(ventas);
    }

}