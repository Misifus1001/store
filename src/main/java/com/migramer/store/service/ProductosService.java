package com.migramer.store.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public PaginacionResponse getProductsByPageAndTienda(String uuidTienda, Integer page, Integer size) {

        try {

            Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

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
        productoDto.setStock(producto.getStock());
        productoDto.setUrlImagen(producto.getUrlImagen());

        return productoDto;
    }

    private Page<ProductoDto> productoPageProductoDtoPage(Page<Producto> productoPage) {

        return productoPage.map(producto -> productoToProductoDto(producto));
    }

}