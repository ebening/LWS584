package com.lowes.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * FormaPago generated by hbm2java
 */
@Entity
@Table(name = "FORMA_PAGO")
public class FormaPago implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idFormaPago;
	private String descripcion;
	private String descripcionEbs;

	public FormaPago() {
	}
	
	public FormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	
	public FormaPago(int idFormaPago, String descripcion) {
		this.idFormaPago = idFormaPago;
		this.descripcion = descripcion;
	}

	public FormaPago(int idFormaPago, String descripcion, String descripcionEbs) {
		this.idFormaPago = idFormaPago;
		this.descripcion = descripcion;
		this.descripcionEbs = descripcionEbs;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_FORMA_PAGO", unique = true, nullable = false)
	public int getIdFormaPago() {
		return this.idFormaPago;
	}

	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}

	@Column(name = "DESCRIPCION", nullable = false, length = 100)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column(name = "DESCRIPCION_EBS", length = 20)
	public String getDescripcionEbs() {
		return this.descripcionEbs;
	}

	public void setDescripcionEbs(String descripcionEbs) {
		this.descripcionEbs = descripcionEbs;
	}

}