package com.migramer.store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.entities.Producto;
import com.migramer.store.entities.Tienda;
import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.ProductoDto;
import com.migramer.store.repository.ProductoRepository;

@Service
public class ProductosService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TiendaService tiendaService;

    private final Logger logger = LoggerFactory.getLogger(ProductosService.class);

    // @EventListener(ApplicationReadyEvent.class)
    public void probarGuardado() {
        List<ProductoDto> productoDtoList = List.of(
                new ProductoDto(1, "7501000123456", "Coca-Cola 600ml", new BigDecimal("18.50"), 10,
                        "https://example.com/img/coca600.jpg", true, LocalDateTime.now()),

                new ProductoDto(2, "7502000345678", "Sabritas Adobadas 45g", new BigDecimal("14.00"),10,
                        "https://example.com/img/sabritas_adobadas.jpg", true, LocalDateTime.now()),

                new ProductoDto(3, "7503000567890", "Pan Bimbo Blanco 680g", new BigDecimal("38.90"),10,
                        "https://example.com/img/pan_bimbo.jpg", true, LocalDateTime.now()),

                new ProductoDto(4, "7504000789012", "Leche Lala Entera 1L", new BigDecimal("28.00"),10,
                        "https://example.com/img/leche_lala.jpg", true, LocalDateTime.now()),

                new ProductoDto(5, "7505000901234", "Cereal Zucaritas 300g", new BigDecimal("49.90"),10,
                        "https://example.com/img/zucaritas.jpg", true, LocalDateTime.now()),

                new ProductoDto(6, "7506000123457", "Atún Dolores en Agua 140g", new BigDecimal("23.50"),10,
                        "https://example.com/img/atun_dolores.jpg", true, LocalDateTime.now()),

                new ProductoDto(7, "7507000234568", "Aceite Nutrioli 1L", new BigDecimal("52.00"),10,
                        "https://example.com/img/aceite_nutrioli.jpg", true, LocalDateTime.now()),

                new ProductoDto(8, "7508000345679", "Galletas María Gamesa 170g", new BigDecimal("12.50"),10,
                        "https://example.com/img/galletas_maria.jpg", true, LocalDateTime.now()),

                new ProductoDto(9, "7509000456780", "Arroz Verde Valle 1kg", new BigDecimal("36.00"),10,
                        "https://example.com/img/arroz_verdevalle.jpg", true, LocalDateTime.now()),

                new ProductoDto(10, "7510000567891", "Frijoles La Sierra Refritos 430g", new BigDecimal("21.00"),10,
                        "https://example.com/img/frijoles_lasierra.jpg", true, LocalDateTime.now()));
        for (ProductoDto productoDto : productoDtoList) {
            saveProductoDto(productoDto, 2);
        }

    }

    public PaginacionResponse probarVisualizacion(Integer idTienda, Integer page, Integer size){
        return getProductsByPageAndTienda(idTienda, page, size);

    }

    public ProductoDto saveProductoDto(ProductoDto productoDto, Integer idTienda) {
        Tienda tienda = tiendaService.getTiendaEntityById(idTienda);
        Producto producto = save(productoDto, tienda);
        return productoToProductoDto(producto);
    }

    @Transactional
    private Producto save(ProductoDto productoDto, Tienda tienda) {

        logger.info("Entrando: save()");
        logger.info("productoDto: {}",productoDto);
        Producto producto = new Producto();
        producto.setCodigoBarras(productoDto.getCodigoBarras());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setEstatus(productoDto.getEstatus());
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setPrecio(productoDto.getPrecio());
        producto.setStock(productoDto.getStock());
        producto.setUrlImagen(productoDto.getUrlImagen());
        producto.setTiendaForProducto(tienda);

        productoRepository.save(producto);

        logger.info("Saliendo: save()");


        return producto;
    }

    public PaginacionResponse getProductsByPageAndTienda(Integer idTienda, Integer page, Integer size) {

        try {

            Tienda tienda = tiendaService.getTiendaEntityById(idTienda);

            PaginacionResponse paginacionResponse = new PaginacionResponse();

            Pageable pageable = PageRequest.of(page, size);

            Page<Producto> productoPageList = productoRepository.findAllByTiendaForProducto(tienda, pageable);

            Page<ProductoDto> productoDtoPage = productoPageProductoDtoPage(productoPageList);

            paginacionResponse.setItems(productoDtoPage.getContent());
            paginacionResponse.setTotalItems(productoDtoPage.getTotalElements());
            paginacionResponse.setTotalPages(productoDtoPage.getTotalPages());
            paginacionResponse.setCurrentPage(productoDtoPage.getNumber());
            paginacionResponse.setPreviousPage(
                    productoDtoPage.getNumber() > 0 ? productoDtoPage.getNumber() - 1 : productoDtoPage.getNumber());
            paginacionResponse.setNextPage(
                    productoDtoPage.getNumber() + 1 < productoDtoPage.getTotalPages() ? productoDtoPage.getNumber() + 1
                            : productoDtoPage.getNumber());

            return paginacionResponse;
        } catch (Exception e) {
            logger.error("Ocurrió un error: {}", e);
            throw new RuntimeException(e);
        }
    }

    private ProductoDto productoToProductoDto(Producto producto) {

        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(producto.getId());
        productoDto.setCodigoBarras(producto.getCodigoBarras());
        productoDto.setDescripcion(producto.getDescripcion());
        productoDto.setEstatus(producto.getEstatus());
        productoDto.setFechaCreacion(producto.getFechaCreacion());
        productoDto.setPrecio(producto.getPrecio());
        productoDto.setUrlImagen(producto.getUrlImagen());

        return productoDto;
    }

    private Page<ProductoDto> productoPageProductoDtoPage(Page<Producto> productoPage) {

        return productoPage.map(producto -> productoToProductoDto(producto));
    }

}