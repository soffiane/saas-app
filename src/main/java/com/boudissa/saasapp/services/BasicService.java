package com.boudissa.saasapp.services;

import org.springframework.data.domain.Page;

public interface BasicService<I, O> {
    void create(final I request);

    void update(final String id, final I request);

    void delete(final String id);

    Page<O> findAll(final int page, final int size);//pagination

    O findById(final String id);
}
