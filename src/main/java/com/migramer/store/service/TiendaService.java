package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.Tienda;
import com.migramer.store.exceptions.ResourceNotFoundException;
import com.migramer.store.models.PaginacionResponse;
import com.migramer.store.models.TiendaDto;
import com.migramer.store.repository.TiendaRepository;

import jakarta.transaction.Transactional;

@Service
public class TiendaService {

    @Autowired
    private TiendaRepository tiendaRepository;

    private final Logger logger = LoggerFactory.getLogger(TiendaService.class);

    @Transactional
    public TiendaDto save(TiendaDto tiendaDto) {

        logger.info("Entrando: save()");
        logger.info("TiendaDto: {}", tiendaDto);
        Tienda tienda = new Tienda();
        tienda.setUuid(UUID.randomUUID().toString());
        tienda.setEstatus(true);
        tienda.setFechaCreacion(LocalDateTime.now());
        tienda.setNombre(tiendaDto.getNombre());
        tienda.setUbicacion(tiendaDto.getUbicacion());

        tiendaRepository.save(tienda);

        logger.info("Saliendo: save()");

        return tiendaEntityToTiendaDto(tienda);
    }

    public PaginacionResponse getTiendaByPage(Integer page, Integer size) {

        try {
            PaginacionResponse paginacionResponse = new PaginacionResponse();

            Pageable pageable = PageRequest.of(page, size);

            Page<Tienda> tiendaPageList = tiendaRepository.findAll(pageable);

            Page<TiendaDto> tiendaDtoPage = tiendaPageToTiendaDtoPage(tiendaPageList);

            paginacionResponse.setItems(tiendaDtoPage.getContent());
            paginacionResponse.setTotalItems(tiendaDtoPage.getTotalElements());
            paginacionResponse.setTotalPages(tiendaDtoPage.getTotalPages());
            paginacionResponse.setCurrentPage(tiendaDtoPage.getNumber());
            paginacionResponse.setPreviousPage(
                    tiendaDtoPage.getNumber() > 0 ? tiendaDtoPage.getNumber() - 1 : tiendaDtoPage.getNumber());
            paginacionResponse.setNextPage(
                    tiendaDtoPage.getNumber() + 1 < tiendaDtoPage.getTotalPages() ? tiendaDtoPage.getNumber() + 1
                            : tiendaDtoPage.getNumber());

            return paginacionResponse;
        } catch (Exception e) {
            logger.error("Ocurrió un error: {}", e);
            throw new RuntimeException(e);
        }
    }

    public TiendaDto getTiendaById(Integer id) {

        Tienda tienda = findTiendaById(id);
        TiendaDto tiendaDto = tiendaEntityToTiendaDto(tienda);
        return tiendaDto;
    }

    private TiendaDto tiendaEntityToTiendaDto(Tienda tienda) {
        TiendaDto tiendaDto = new TiendaDto();
        tiendaDto.setNombre(tienda.getNombre());
        tiendaDto.setUbicacion(tienda.getUbicacion());

        return tiendaDto;
    }

    public Tienda findTiendaById(Integer id) {
        Tienda tienda = tiendaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tienda", id));
        return tienda;
    }

    public Tienda getTiendaEntityByUUID(String uuidTienda) {

        logger.info("Entrando: getTiendaEntityByUUID()");

        Tienda tienda = tiendaRepository.findByUuid(uuidTienda);
        logger.info("Saliendo: getTiendaEntityByUUID()");

        if (tienda == null) {
            throw new RuntimeException("Tienda inexistente");
        }

        return tienda;
    }

    private Page<TiendaDto> tiendaPageToTiendaDtoPage(Page<Tienda> tiendaPage) {
        return tiendaPage.map(producto -> tiendaEntityToTiendaDto(producto));
    }

}