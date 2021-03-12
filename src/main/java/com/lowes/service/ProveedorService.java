/**
 * 
 */
package com.lowes.service;

import java.util.List;

import com.lowes.entity.Proveedor;
import com.lowes.entity.Usuario;

/**
 * @author miguelrg
 * @version 1.0
 */
public interface ProveedorService {
	public Integer createProveedor(Proveedor proveedor);

	public Proveedor updateProveedor(Proveedor proveedor);

	public void deleteProveedor(int id);

	public List<Proveedor> getAllProveedores();

	public Proveedor getProveedor(int id);

	public List<Proveedor> getAllProveedoresByProveedorRiesgo();

	public List<Proveedor> getProveedoresByTipo(int tipo);

	public List<Proveedor> getProveedorByNumero(Integer numeroProveedor);

	public boolean isProveedor(int id);

	public boolean isProveedor(Proveedor proveedor);

	public boolean isProveedor(Proveedor proveedor, Usuario usuario);

	public boolean isAsesor(int id);

	public boolean isAsesor(Proveedor proveedor);

	public boolean isAsesor(Proveedor proveedor, Usuario usuario);
}