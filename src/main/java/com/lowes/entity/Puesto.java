package com.lowes.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Puesto generated by hbm2java
 */
@Entity
@Table(name = "PUESTO", schema = "LWS584")
public class Puesto implements java.io.Serializable {

	private static final long serialVersionUID = -2594976736725416652L;

	private int idPuesto;
	private NivelAutoriza nivelAutoriza;
	private TipoLocacion tipoLocacion;
	private String descripcion;
	private short activo;
	private Date creacionFecha;
	private int creacionUsuario;
	private Date modificacionFecha;
	private Integer modificacionUsuario;
	
//	ID's de objetos
	private int idNivelAutoriza;
	private int idTipoLocacion;

	public Puesto() {
	}

	public Puesto(int idPuesto, TipoLocacion tipoLocacion, String descripcion,
			short activo, Date creacionFecha, int creacionUsuario) {
		this.idPuesto = idPuesto;
		this.tipoLocacion = tipoLocacion;
		this.descripcion = descripcion;
		this.activo = activo;
		this.creacionFecha = creacionFecha;
		this.creacionUsuario = creacionUsuario;
	}

	public Puesto(int idPuesto, NivelAutoriza nivelAutoriza, TipoLocacion tipoLocacion, String descripcion,
			short activo, Date creacionFecha, int creacionUsuario, Date modificacionFecha,
			Integer modificacionUsuario) {
		this.idPuesto = idPuesto;
		this.nivelAutoriza = nivelAutoriza;
		this.tipoLocacion = tipoLocacion;
		this.descripcion = descripcion;
		this.activo = activo;
		this.creacionFecha = creacionFecha;
		this.creacionUsuario = creacionUsuario;
		this.modificacionFecha = modificacionFecha;
		this.modificacionUsuario = modificacionUsuario;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_PUESTO", unique = true, nullable = false)
	public int getIdPuesto() {
		return this.idPuesto;
	}

	public void setIdPuesto(int idPuesto) {
		this.idPuesto = idPuesto;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_NIVEL_AUTORIZA")
	public NivelAutoriza getNivelAutoriza() {
		return this.nivelAutoriza;
	}

	public void setNivelAutoriza(NivelAutoriza nivelAutoriza) {
		this.nivelAutoriza = nivelAutoriza;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_TIPO_LOCACION", nullable = false)
	public TipoLocacion getTipoLocacion() {
		return this.tipoLocacion;
	}

	public void setTipoLocacion(TipoLocacion tipoLocacion) {
		this.tipoLocacion = tipoLocacion;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREACION_FECHA", nullable = false, length = 26)
	public Date getCreacionFecha() {
		return this.creacionFecha;
	}

	public void setCreacionFecha(Date creacionFecha) {
		this.creacionFecha = creacionFecha;
	}

	@Column(name = "CREACION_USUARIO", nullable = false)
	public int getCreacionUsuario() {
		return this.creacionUsuario;
	}

	public void setCreacionUsuario(int creacionUsuario) {
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

	@Transient
	public int getIdNivelAutoriza() {
		return idNivelAutoriza;
	}

	public void setIdNivelAutoriza(int idNivelAutoriza) {
		this.idNivelAutoriza = idNivelAutoriza;
	}

	@Transient
	public int getIdTipoLocacion() {
		return idTipoLocacion;
	}

	public void setIdTipoLocacion(int idTipoLocacion) {
		this.idTipoLocacion = idTipoLocacion;
	}

}