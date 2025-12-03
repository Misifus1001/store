package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.Carrito;
import com.migramer.store.entities.Producto;
import com.migramer.store.entities.Tienda;
import com.migramer.store.entities.Usuario;
import com.migramer.store.models.AddToCarritoRequest;
import com.migramer.store.models.CarritoDto;
import com.migramer.store.models.MessageResponse;
import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.repository.CarritoRepository;

import jakarta.transaction.Transactional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductosService productosService;

    @Autowired
    private TiendaService tiendaService;


    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    @Lazy
    private CarritoService self;

    private final Logger logger = LoggerFactory.getLogger(CarritoService.class);

    public MessageResponse addProductToShoppingCart(AddToCarritoRequest addToCarritoRequest, Usuario usuario,
            String uuidTienda) {
        addProductToCart(addToCarritoRequest, usuario, uuidTienda);
        return new MessageResponse("Producto agregado al carrito exitosamente");
    }

    public void addProductToCart(AddToCarritoRequest addToCarritoRequest, Usuario usuario, String uuidTienda) {
        self.executeAddProductToCart(addToCarritoRequest, usuario, uuidTienda);
    }

    @Async
    public void executeAddProductToCart(AddToCarritoRequest addToCarritoRequest, Usuario usuario, String uuidTienda) {

        try {

            Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

            Producto producto = productosService
                    .findByCodigoBarrasAndEstatusAndTiendaForProducto(addToCarritoRequest.getCodigoBarras(), true,
                            tienda);

            logger.info(producto.getDescripcion());
            saveShoppingCart(addToCarritoRequest.getCantidad(), producto, usuario);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }

    }

    public PaginacionResponse getCarritoComprasByUser(String email, Integer page, Integer size) {

        try {

            Usuario usuario = usuarioService.getUsuarioByEmail(email);

            PaginacionResponse paginacionResponse = new PaginacionResponse();

            Pageable pageable = PageRequest.of(page, size);

            Page<Carrito> carritoPageList = carritoRepository.findAllByUsuarioForCarrito(usuario, pageable);

            Page<CarritoDto> carritoDtoPage = productoPageProductoDtoPage(carritoPageList);

            paginacionResponse.setItems(carritoDtoPage.getContent());
            paginacionResponse.setTotalItems(carritoDtoPage.getTotalElements());
            paginacionResponse.setTotalPages(carritoDtoPage.getTotalPages());
            paginacionResponse.setCurrentPage(carritoDtoPage.getNumber());
            paginacionResponse.setPreviousPage(
                    carritoDtoPage.getNumber() > 0 ? carritoDtoPage.getNumber() - 1 : carritoDtoPage.getNumber());
            paginacionResponse.setNextPage(
                    carritoDtoPage.getNumber() + 1 < carritoDtoPage.getTotalPages() ? carritoDtoPage.getNumber() + 1
                            : carritoDtoPage.getNumber());

            return paginacionResponse;
        } catch (Exception e) {
            logger.error("Ocurrió un error: {}", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<Carrito> findByProductoForCarritoAndUsuarioForCarrito(Producto producto, Usuario usuario) {
        return carritoRepository.findTop1ByProductoForCarritoAndUsuarioForCarrito(producto, usuario);
    }
    
    private Page<CarritoDto> productoPageProductoDtoPage(Page<Carrito> carritoPage) {
        return carritoPage.map(carrito -> carritoToCarritoDto(carrito));
    }

    private CarritoDto carritoToCarritoDto(Carrito carrito){
        CarritoDto carritoDto = new CarritoDto();

        Producto producto = carrito.getProductoForCarrito();
        carritoDto.setId(carrito.getId());
        carritoDto.setCantidad(carrito.getCantidad());
        carritoDto.setCodigoBarras(producto.getCodigoBarras());
        carritoDto.setDescripcion(producto.getDescripcion());
        carritoDto.setPrecio(producto.getPrecio());
        carritoDto.setStock(producto.getStock());
        carritoDto.setUrlImagen(buildURL(producto.getUrlImagen()));
        return carritoDto;

    }
    private String buildURL(String baseURL) {
        String url = "https://unkneaded-deepeningly-sandra.ngrok-free.dev" + "/products/images/" + baseURL;
        // String url = "http://localhost:8080" + "/products/images/" + baseURL;
        return url;
    }

    @Transactional
    public void saveShoppingCart(Integer cantidad, Producto producto, Usuario usuario) {

        Optional<Carrito> carrOptional = findByProductoForCarritoAndUsuarioForCarrito(producto, usuario);

        if (carrOptional.isPresent()) {

            logger.info("YA HAY UN PRODUCTO AGREGADO");
            return;
        }

        Carrito carrito = new Carrito();
        carrito.setCantidad(cantidad);
        carrito.setEstatus(true);
        carrito.setProductoForCarrito(producto);
        carrito.setFechaCreacion(LocalDateTime.now());
        carrito.setUsuarioForCarrito(usuario);
        carritoRepository.save(carrito);
    }

}