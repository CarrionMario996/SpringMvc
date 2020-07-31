package com.example.demo.model.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.entity.Cliente;
import com.example.demo.model.entity.Factura;
import com.example.demo.model.entity.ItemFactura;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.service.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable("clienteId") Long clienteId, Map<String, Object> map, RedirectAttributes flash) {
		Cliente cliente = clienteService.findOne(clienteId);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe");
			return "redirect:/listar";
		}
		Factura factura = new Factura();
		factura.setCliente(cliente);
		map.put("factura", factura);
		map.put("titulo", "crear factura");
		return "factura/form";
	}
	
	@GetMapping(value="/cargar-productos/{term}",produces= {"application/json"})
	public @ResponseBody List<Producto>cargarProducto(@PathVariable("term")String term) {
		
		return clienteService.findByNombre(term);
	}
	@PostMapping("/form")
	public String guardar(Factura factura,@RequestParam(name="item_id[]",required = false)Long[]itemId,@RequestParam(name="cantidad[]",required = false)Integer[]cantidad,RedirectAttributes flash,SessionStatus status) {
		for(int i=0;i< itemId.length;i++) {
			Producto producto=clienteService.findProductoById(itemId[i]);
			ItemFactura linea= new ItemFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			factura.addItemFactura(linea);
		}
		clienteService.saveFactura(factura);
		status.setComplete();
		flash.addFlashAttribute("success","factura creado con exito");
		return "redirect:/ver/"+factura.getCliente().getId();
	}
}
