package com.migramer.store.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.migramer.store.entities.Tienda;
import com.migramer.store.models.TiendaDto;
import com.migramer.store.repository.TiendaRepository;

import jakarta.transaction.Transactional;

@Service
public class TiendaService {

    @Autowired
    private TiendaRepository tiendaRepository;

    private final Logger logger = LoggerFactory.getLogger(TiendaService.class);

    private TiendaDto tiendaEntityToTiendaDto(Tienda tienda){
        TiendaDto tiendaDto = new TiendaDto();
        tiendaDto.setNombre(tienda.getNombre());
        tiendaDto.setUbicacion(tienda.getUbicacion());

        return tiendaDto;

    }

    @Transactional
    public TiendaDto save(TiendaDto tiendaDto){

        logger.info("Entrando: save()");
        logger.info("TiendaDto: {}",tiendaDto);
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

    public List<TiendaDto> getTiendasDto(){

        List<TiendaDto> tiendaDtoList = new ArrayList<>();

        List<Tienda> tiendaList = tiendaRepository.findAll();

        for (Tienda tienda : tiendaList) {

            TiendaDto tiendaDto = tiendaEntityToTiendaDto(tienda);
            tiendaDtoList.add(tiendaDto);
        }

        return tiendaDtoList;
    }

    public TiendaDto getTiendaById(Integer id){

        TiendaDto tiendaDto = new TiendaDto();
        Optional<Tienda> tienda = tiendaRepository.findById(id);

        if (tienda.isPresent()) {
            tiendaDto = tiendaEntityToTiendaDto(tienda.get());
        }

        return tiendaDto;
    }

    public Tienda getTiendaEntityById(Integer id){

        logger.info("Entrando: getTiendaEntityById()");

        Optional<Tienda> tienda = tiendaRepository.findById(id);

        logger.info("Saliendo: getTiendaEntityById()");

        return tienda.get();
    }

    public Tienda getTiendaEntityByUUID(String uuidTienda){

        logger.info("Entrando: getTiendaEntityByUUID()");

        Tienda tienda = tiendaRepository.findByUuid(uuidTienda);
        logger.info("Saliendo: getTiendaEntityByUUID()");

        if (tienda == null) {
            throw new RuntimeException("Tienda inexistente");
        }

        return tienda;
    }

}