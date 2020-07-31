package com.example.demo.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.model.entity.Cliente;
import com.example.demo.model.entity.Factura;
import com.example.demo.model.entity.Producto;

public interface IClienteService {
	public List<Cliente> findAll();
	
	public Page<Cliente> findAll(Pageable pageable);

	public Cliente save(Cliente cliente);

	public Cliente findOne(Long id);

	public void delete(Long id);
	
	public List<Producto>findByNombre(String term);
	
	public void saveFactura(Factura factura);
	
	public Producto findProductoById(Long id);
}
