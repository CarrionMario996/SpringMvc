package com.example.demo.view.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.demo.model.entity.Cliente;
@XmlRootElement(name="clientes")
public class ClienteList {
	@XmlElement(name="cliente")
	public List<Cliente> clientes;

	public ClienteList(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public ClienteList() {

	}

	public List<Cliente> getClientes() {
		return clientes;
	}
	

}
