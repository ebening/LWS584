/**
 * 
 */
package com.lowes.entity;

/**
 * @author Adinfi
 *
 */
//default package
//Generated 17-mar-2016 18:17:30 by Hibernate Tools 4.3.1.Final

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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
* Notificacion generated by hbm2java
*/
@Entity
@Table(name = "NOTIFICACION", schema = "LWS584")
public class Notificacion implements java.io.Serializable {

	private int idNotificacion;
	private Solicitud solicitud;
	private TipoNotificacion tipoNotificacion;
	private Usuario usuario;
	private Date fecha;
	private int numNotificacion;
	private String estatus;
	private Date proximoEnvio;

	public Notificacion() {
	}

	public Notificacion(int idNotificacion, Solicitud solicitud, TipoNotificacion tipoNotificacion, Usuario usuario,
			Date fecha, int numNotificacion, Date proximoEnvio) {
		this.idNotificacion = idNotificacion;
		this.solicitud = solicitud;
		this.tipoNotificacion = tipoNotificacion;
		this.usuario = usuario;
		this.fecha = fecha;
		this.numNotificacion = numNotificacion;
		this.proximoEnvio = proximoEnvio;
	}

	public Notificacion(int idNotificacion, Solicitud solicitud, TipoNotificacion tipoNotificacion, Usuario usuario,
			Date fecha, int numNotificacion, String estatus, Date proximoEnvio) {
		this.idNotificacion = idNotificacion;
		this.solicitud = solicitud;
		this.tipoNotificacion = tipoNotificacion;
		this.usuario = usuario;
		this.fecha = fecha;
		this.numNotificacion = numNotificacion;
		this.estatus = estatus;
		this.proximoEnvio = proximoEnvio;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_NOTIFICACION", unique = true, nullable = false)
	public int getIdNotificacion() {
		return this.idNotificacion;
	}

	public void setIdNotificacion(int idNotificacion) {
		this.idNotificacion = idNotificacion;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_SOLICITUD", nullable = false)
	public Solicitud getSolicitud() {
		return this.solicitud;
	}

	public void setSolicitud(Solicitud solicitud) {
		this.solicitud = solicitud;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_TIPO_NOTIFICACION", nullable = false)
	public TipoNotificacion getTipoNotificacion() {
		return this.tipoNotificacion;
	}

	public void setTipoNotificacion(TipoNotificacion tipoNotificacion) {
		this.tipoNotificacion = tipoNotificacion;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToOne
	@JoinColumn(name = "ID_USUARIO_DESTINO", nullable = false)
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "FECHA", length = 10)
	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column(name = "NUM_NOTIFICACION", nullable = false)
	public int getNumNotificacion() {
		return this.numNotificacion;
	}

	public void setNumNotificacion(int numNotificacion) {
		this.numNotificacion = numNotificacion;
	}

	@Column(name = "ESTATUS", length = 10)
	public String getEstatus() {
		return this.estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "PROXIMO_ENVIO", length = 10)
	public Date getProximoEnvio() {
		return proximoEnvio;
	}

	public void setProximoEnvio(Date proximoEnvio) {
		this.proximoEnvio = proximoEnvio;
	}

}
