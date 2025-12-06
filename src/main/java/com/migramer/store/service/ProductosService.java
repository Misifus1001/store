package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.migramer.store.entities.Producto;
import com.migramer.store.entities.Tienda;
import com.migramer.store.exceptions.BusinessException;
import com.migramer.store.exceptions.ResourceNotFoundException;
import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.ProductoDto;
import com.migramer.store.providers.imageprovider.components.UploadImageComponent;
import com.migramer.store.providers.webhook.WebHookService;
import com.migramer.store.providers.webhook.model.NameNotification;
import com.migramer.store.repository.ProductoRepository;

@Service
public class ProductosService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TiendaService tiendaService;

    @Autowired
    @Lazy
    private ProductosService self;

    @Autowired
    private WebHookService webHookService;

    @Autowired
    private UploadImageComponent uploadImageComponent;

    private final Logger logger = LoggerFactory.getLogger(ProductosService.class);

    public ProductoDto saveProductoDto(ProductoDto productoDto, String uuidTienda) {
        Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

        validarProductoExistente(productoDto.getCodigoBarras(),true, tienda);

        String uuidName = UUID.randomUUID().toString() + ".jpeg";
        productoDto.setUrlImagen(uuidName);
        Producto producto = save(productoDto, tienda);
        notificateTiendaChangeProducts(uuidTienda);
        saveImage(productoDto.getBase64Image(), uuidName);
        return productoToProductoDto(producto);
    }

    @Transactional
    private Producto save(ProductoDto productoDto, Tienda tienda) {

        logger.info("Entrando: save()");
        Producto producto = new Producto();
        producto.setCodigoBarras(productoDto.getCodigoBarras());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setEstatus(true);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setPrecio(productoDto.getPrecio());
        producto.setStock(productoDto.getStock());
        producto.setUrlImagen(productoDto.getUrlImagen());
        producto.setTiendaForProducto(tienda);
        producto.setUrlImagen(productoDto.getUrlImagen());
        productoRepository.save(producto);

        logger.info("Saliendo: save()");

        return producto;
    }

    public ProductoDto getProductByBarcodeAndUuidTienda(String uuidTienda, String barcode) {

        Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

        Producto productoFind = findByCodigoBarrasAndEstatusAndTiendaForProducto(barcode,true, tienda);

        ProductoDto productoDto = productoToProductoDto(productoFind);

        return productoDto;
    }

    public Producto findByCodigoBarrasAndEstatusAndTiendaForProducto(String barcode,Boolean estatus, Tienda tienda) {
        return productoRepository.findTop1ByCodigoBarrasAndEstatusAndTiendaForProducto(barcode,estatus, tienda)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", barcode));
    }
    

    private void validarProductoExistente(String barcode,Boolean estatus, Tienda tienda){

        Producto producto = findByCodigoBarrasAndEstatusAndTiendaForProducto(barcode, estatus, tienda);

        if (producto != null) {
            throw new BusinessException("Ya existe un producto con ese código de barras");
        }

    }

    public void saveImage(String base64, String fileName) {
        self.executeSaveImage(base64, fileName);
    }

    @Async
    public void executeSaveImage(String base64, String fileName) {
        uploadImageComponent.uploadImage(base64, fileName);
    }

    public void notificateTiendaChangeProducts(String uuidTienda) {
        self.executeCotificateTiendaChangeProducts(uuidTienda);
    }

    @Async
    public void executeCotificateTiendaChangeProducts(String uuidTienda) {
        webHookService.sendNotificationChanges(NameNotification.PRODUCTOS, uuidTienda);
    }

    public PaginacionResponse getProductsByPageAndTienda(String uuidTienda, Integer page, Integer size) {

        try {

            Tienda tienda = tiendaService.getTiendaEntityByUUID(uuidTienda);

            PaginacionResponse paginacionResponse = new PaginacionResponse();

            Pageable pageable = PageRequest.of(page, size);

            Page<Producto> productoPageList = productoRepository.findAllByTiendaForProducto(tienda, pageable);

            Page<ProductoDto> productoDtoPage = productoPageProductoDtoPage(productoPageList);

            paginacionResponse.setData(productoDtoPage.getContent());
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

    public List<Producto> productosConMenorStock(Tienda tienda, Boolean estatus){
        return productoRepository.findTop5ByTiendaForProductoAndEstatus(tienda, estatus);
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
        productoDto.setUrlImagen(buildURL(producto.getUrlImagen()));
        // productoDto.setUrlImagen(producto.getUrlImagen());
        // productoDto.setUuidTienda(producto.getTiendaForProducto().getUuid());

        return productoDto;
    }

    private Page<ProductoDto> productoPageProductoDtoPage(Page<Producto> productoPage) {

        return productoPage.map(producto -> productoToProductoDto(producto));
    }

    private String buildURL(String baseURL) {
        String url = "https://store-1-9jzc.onrender.com" + "/products/images/" + baseURL;
        // String url = "http://localhost:8080" + "/products/images/" + baseURL;
        return url;
    }
}