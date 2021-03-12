/**
 * 
 */
package com.lowes.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TipoNotificacion generated by hbm2java
 */
@Entity
@Table(name = "TIPO_NOTIFICACION")
public class TipoNotificacion implements java.io.Serializable {

	private int idTipoNotificacion;
	private String descripcion;
	private short activo;

	public TipoNotificacion() {
	}

	public TipoNotificacion(int idTipoNotificacion, String descripcion, short activo) {
		this.idTipoNotificacion = idTipoNotificacion;
		this.descripcion = descripcion;
		this.activo = activo;
	}

	public TipoNotificacion(int idTipoNotificacion, String descripcion, short activo, Set notificacions) {
		this.idTipoNotificacion = idTipoNotificacion;
		this.descripcion = descripcion;
		this.activo = activo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_TIPO_NOTIFICACION", unique = true, nullable = false)
	public int getIdTipoNotificacion() {
		return this.idTipoNotificacion;
	}

	public void setIdTipoNotificacion(int idTipoNotificacion) {
		this.idTipoNotificacion = idTipoNotificacion;
	}

	@Column(name = "DESCRIPCION", nullable = false, length = 100)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "ACTIVO", nullable = false)
	public short getActivo() {
		return this.activo;
	}

	public void setActivo(short activo) {
		this.activo = activo;
	}
}
