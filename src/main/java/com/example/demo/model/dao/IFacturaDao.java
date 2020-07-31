package com.example.demo.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long>{

}
