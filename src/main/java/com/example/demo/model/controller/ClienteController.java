package com.example.demo.model.controller;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.entity.Cliente;
import com.example.demo.model.service.IClienteService;
import com.example.demo.model.service.IUploadFileService;
import com.example.demo.model.util.paginator.PageRender;

import ch.qos.logback.classic.Logger;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	@Autowired
	private IClienteService clienteService;
	@Autowired
	private IUploadFileService fileService;
	
    @Secured("ROLE_USER")
	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable("filename") String filename) {
		Resource recurso=null;
		try {
			recurso = fileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
    @Secured("ROLE_USER")
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
		Cliente cliente = clienteService.fecthByIdWithFacturas(id);//clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "cliente no existe en bd");
			return "redirect:/listar";
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Detalle cliente " + cliente.getNombre());
		return "ver";

	}

	@GetMapping({"/listar","/"})
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Map<String, Object> map) {
	
		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
		map.put("titulo", "Listado de clientes");
		map.put("clientes", clientes);
		map.put("page", pageRender);
		return "listar";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/form")
	public String crear(Map<String, Object> map) {
		Cliente cliente = new Cliente();
		map.put("titulo", "Formulario de cliente");
		map.put("cliente", cliente);
		return "form";
	}
	@Secured("ROLE_ADMIN")
	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, SessionStatus status, Map<String, Object> map,
			RedirectAttributes flash, @RequestParam("file") MultipartFile foto) {
		if (result.hasErrors()) {
			map.put("titulo", "formulario de cliente");
			return "form";
		}
		if (!foto.isEmpty()) {

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				fileService.delete(cliente.getFoto());
				}
			String uniqueFilename=null;
			try {
				uniqueFilename = fileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", "has subido correctamente '" + uniqueFilename + "'");
			cliente.setFoto(uniqueFilename);
		}

		String mensajeFlash = (cliente.getId() != null) ? "cliente editado con exito" : "cliente creado con exito";

		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar";
	}
	@Secured("ROLE_ADMIN")
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(name = "id") Long id, Model model, RedirectAttributes flash) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "el cliente no existe en la bd");
				return "redirect:/listar";
			}

		} else {
			flash.addFlashAttribute("error", "el cliente no puede ser cero");
			return "redirect:/listar";
		}
		model.addAttribute("titulo", "editando cliente");
		model.addAttribute("cliente", cliente);
		return "form";
	}
	@Secured("ROLE_ADMIN")
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(name = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("success", "cliente eliminado con exito");
			
				if (fileService.delete(cliente.getFoto())) {
					flash.addFlashAttribute("info", "foto" + cliente.getFoto() + "eliminada");
				}			

		}
		return "redirect:/listar";
	}
	
}
