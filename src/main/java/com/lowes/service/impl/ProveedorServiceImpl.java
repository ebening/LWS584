/**
 * 
 */
package com.lowes.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowes.dao.ProveedorDAO;
import com.lowes.entity.Proveedor;
import com.lowes.entity.Usuario;
import com.lowes.service.ProveedorService;
import com.lowes.service.UsuarioService;

/**
 * @author miguelrg
 * @version 1.0
 */
@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

	public ProveedorServiceImpl() {
		System.out.println("ProveedorServiceImplConstruct()");
	}

	@Autowired
	private ProveedorDAO proveedorDAO;
	@Autowired
	private UsuarioService usuarioService;

	@Override
	public Integer createProveedor(Proveedor proveedor) {
		return proveedorDAO.createProveedor(proveedor);
	}

	@Override
	public Proveedor updateProveedor(Proveedor proveedor) {
		return proveedorDAO.updateProveedor(proveedor);
	}

	@Override
	public void deleteProveedor(int id) {
		proveedorDAO.deleteProveedor(id);
	}

	@Override
	public List<Proveedor> getAllProveedores() {
		return proveedorDAO.getAllProveedores();
	}

	@Override
	public Proveedor getProveedor(int id) {
		return proveedorDAO.getProveedor(id);
	}

	@Override
	public List<Proveedor> getAllProveedoresByProveedorRiesgo() {
		return proveedorDAO.getAllProveedoresByProveedorRiesgo();
	}

	@Override
	public List<Proveedor> getProveedoresByTipo(int tipo) {
		return proveedorDAO.getProveedoresByTipo(tipo);
	}

	@Override
	public List<Proveedor> getProveedorByNumero(Integer numeroProveedor) {
		return proveedorDAO.getProveedorByNumero(numeroProveedor);
	}

	public boolean isProveedor(int id) {
		return isProveedor(getProveedor(id));
	}

	public boolean isProveedor(Proveedor proveedor) {
		return proveedor!=null && isProveedor(proveedor, usuarioService.getUsuarioByProveedor(proveedor.getNumeroProveedor()));
	}

	public boolean isProveedor(Proveedor proveedor, Usuario usuario) {
		return usuario == null || (proveedor.getNumeroProveedor() == usuario.getNumeroProveedor());
	}

	public boolean isAsesor(int id) {
		return !isProveedor(id);
	}
	public boolean isAsesor(Proveedor proveedor) {
		return !isProveedor(proveedor);
	}
	
	public boolean isAsesor(Proveedor proveedor, Usuario usuario) {
		return !isProveedor(proveedor, usuario);
	}
}
