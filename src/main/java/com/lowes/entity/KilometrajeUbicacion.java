package com.lowes.entity;

import java.util.Date;

//Generated 29/02/2016 06:07:45 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
* KilometrajeUbicacion generated by hbm2java
*/
@Entity
@Table(name = "KILOMETRAJE_UBICACION", uniqueConstraints = @UniqueConstraint(columnNames = "DESCRIPCION") )
public class KilometrajeUbicacion implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int idUbicacion;
	private String descripcion;
	private short activo;
	private Date creacionFecha;
	private Integer creacionUsuario;
	private Date modificacionFecha;
	private Integer modificacionUsuario;

	public KilometrajeUbicacion() {
	}

	public KilometrajeUbicacion(int idUbicacion) {
		this.idUbicacion = idUbicacion;
	}
	
	public KilometrajeUbicacion(int idUbicacion, String descripcion) {
		this.idUbicacion = idUbicacion;
		this.descripcion = descripcion;
	}
	
	public KilometrajeUbicacion(int idUbicacion, String descripcion, short activo, Date creacionFecha, Integer creacionUsuario,
			Date modificacionFecha, Integer modificacionUsuario) {
		this.idUbicacion = idUbicacion;
		this.descripcion = descripcion;
		this.activo = activo;
		this.creacionFecha = creacionFecha;
		this.creacionUsuario = creacionUsuario;
		this.modificacionFecha = modificacionFecha;
		this.modificacionUsuario = modificacionUsuario;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_UBICACION", unique = true, nullable = false)
	public int getIdUbicacion() {
		return this.idUbicacion;
	}

	public void setIdUbicacion(int idUbicacion) {
		this.idUbicacion = idUbicacion;
	}

	@Column(name = "DESCRIPCION", unique = true, nullable = false, length = 100)
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREACION_FECHA", nullable = false, length = 26)
	public Date getCreacionFecha() {
		return this.creacionFecha;
	}

	public void setCreacionFecha(Date creacionFecha) {
		this.creacionFecha = creacionFecha;
	}

	@Column(name = "CREACION_USUARIO", nullable = false)
	public Integer getCreacionUsuario() {
		return this.creacionUsuario;
	}

	public void setCreacionUsuario(Integer creacionUsuario) {
		this.creacionUsuario = creacionUsuario;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFICACION_FECHA", length = 26)
	public Date getModificacionFecha() {
		return this.modificacionFecha;
	}

	public void setModificacionFecha(Date modificacionFecha) {
		this.modificacionFecha = modificacionFecha;
	}

	@Column(name = "MODIFICACION_USUARIO")
	public Integer getModificacionUsuario() {
		return this.modificacionUsuario;
	}

	public void setModificacionUsuario(Integer modificacionUsuario) {
		this.modificacionUsuario = modificacionUsuario;
	}
	
}