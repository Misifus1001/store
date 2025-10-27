package com.migramer.store.models;

import lombok.Data;

@Data
public class PaginacionResponse {

    private Object items;
    private Integer currentPage;
    private Integer nextPage;
    private Integer previousPage;
    private Long totalItems;
    private Integer totalPages;

}