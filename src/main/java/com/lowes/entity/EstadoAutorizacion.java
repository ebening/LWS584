package com.lowes.entity;
// Generated 28/12/2015 05:50:04 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EstadoAutorizacion generated by hbm2java
 */
@Entity
@Table(name = "ESTADO_AUTORIZACION", schema = "LWS584")
public class EstadoAutorizacion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idEstadoAutorizacion;
	private String estadoAutorizacion;

	public EstadoAutorizacion() {
	}
	
	public EstadoAutorizacion(int idEstadoAutorizacion) {
		this.idEstadoAutorizacion = idEstadoAutorizacion;
	}

	public EstadoAutorizacion(int idEstadoAutorizacion, String estadoAutorizacion) {
		this.idEstadoAutorizacion = idEstadoAutorizacion;
		this.estadoAutorizacion = estadoAutorizacion;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	@Column(name = "ID_ESTADO_AUTORIZACION", unique = true, nullable = false)
	public int getIdEstadoAutorizacion() {
		return this.idEstadoAutorizacion;
	}

	public void setIdEstadoAutorizacion(int idEstadoAutorizacion) {
		this.idEstadoAutorizacion = idEstadoAutorizacion;
	}

	@Column(name = "ESTADO_AUTORIZACION", nullable = false, length = 100)
	public String getEstadoAutorizacion() {
		return this.estadoAutorizacion;
	}

	public void setEstadoAutorizacion(String estadoAutorizacion) {
		this.estadoAutorizacion = estadoAutorizacion;
	}

}