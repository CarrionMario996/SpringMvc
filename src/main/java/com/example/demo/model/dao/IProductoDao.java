package com.example.demo.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.entity.Producto;

public interface IProductoDao extends CrudRepository<Producto, Long> {
	@Query("select p from Producto p where p.nombre like %?1%")
	public List<Producto>findByNombre(String term);
}
