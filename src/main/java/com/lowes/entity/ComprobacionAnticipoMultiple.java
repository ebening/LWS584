package com.lowes.entity;
// Generated 23/03/2016 12:31:20 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * ComprobacionAnticipo generated by hbm2java
 */
@Entity
@Table(name = "COMPROBACION_ANTICIPO_MULTIPLE", schema = "LWS584")
public class ComprobacionAnticipoMultiple implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idComprobacionAnticipoMultiple;
	private Solicitud idSolicitud;
	private Solicitud idSolicitudMultiple;

	public ComprobacionAnticipoMultiple() {
	}

	public ComprobacionAnticipoMultiple(int idComprobacionAnticipoMultiple, Solicitud idSolicitud,
			Solicitud idSolicitudMultiple) {
		this.idComprobacionAnticipoMultiple = idComprobacionAnticipoMultiple;
		this.idSolicitud = idSolicitud;
		this.idSolicitudMultiple = idSolicitudMultiple;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	@Column(name = "ID_COMPROBACION_ANTICIPO_MULTIPLE", unique = true, nullable = false)
	public int getIdComprobacionAnticipoMultiple() {
		return idComprobacionAnticipoMultiple;
	}

	public void setIdComprobacionAnticipoMultiple(int idComprobacionAnticipoMultiple) {
		this.idComprobacionAnticipoMultiple = idComprobacionAnticipoMultiple;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITUD", nullable = false)
	public Solicitud getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Solicitud idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITUD_MULTIPLE", nullable = false)
	public Solicitud getIdSolicitudMultiple() {
		return idSolicitudMultiple;
	}

	public void setIdSolicitudMultiple(Solicitud idSolicitudMultiple) {
		this.idSolicitudMultiple = idSolicitudMultiple;
	}

}
