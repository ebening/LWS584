package com.lowes.entity;

//default package

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
* SolicitudFechaPagoEbs generated by hbm2java
*/
@Entity
@Table(name = "SOLICITUD_FECHA_PAGO_EBS")
public class SolicitudFechaPagoEbs implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idSolicitudFechaPagoEbs;
	private String invoiceNum;
	private String vendorNum;
	private Date fechaPago;
	private short actualizada;
	private short notificada;

	public SolicitudFechaPagoEbs() {
	}

	public SolicitudFechaPagoEbs(int idSolicitudFechaPagoEbs) {
		this.idSolicitudFechaPagoEbs = idSolicitudFechaPagoEbs;
	}
	
	public SolicitudFechaPagoEbs(int idSolicitudFechaPagoEbs, String invoiceNum, 
			String vendorNum, Date fechaPago, short actualizada, short notificada) {
		this.idSolicitudFechaPagoEbs = idSolicitudFechaPagoEbs;
		this.invoiceNum = invoiceNum;
		this.vendorNum = vendorNum;
		this.fechaPago = fechaPago;
		this.actualizada = actualizada;
		this.notificada = notificada;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	@Column(name = "ID_SOLICITUD_FECHA_PAGO_EBS", unique = true, nullable = false)
	public int getIdSolicitudFechaPagoEbs() {
		return this.idSolicitudFechaPagoEbs;
	}

	public void setIdSolicitudFechaPagoEbs(int idSolicitudFechaPagoEbs) {
		this.idSolicitudFechaPagoEbs = idSolicitudFechaPagoEbs;
	}

	@Column(name = "INVOICE_NUM", nullable = false, length = 50)
	public String getInvoiceNum() {
		return this.invoiceNum;
	}

	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}

	@Column(name = "VENDOR_NUM", nullable = false, length = 30)
	public String getVendorNum() {
		return this.vendorNum;
	}

	public void setVendorNum(String vendorNum) {
		this.vendorNum = vendorNum;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "FECHA_PAGO", nullable = false, length = 10)
	public Date getFechaPago() {
		return this.fechaPago;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	@Column(name = "ACTUALIZADA", nullable = false)
	public short getActualizada() {
		return this.actualizada;
	}

	public void setActualizada(short actualizada) {
		this.actualizada = actualizada;
	}
	
	@Column(name = "NOTIFICADA", nullable = false)
	public short getNotificada() {
		return notificada;
	}

	public void setNotificada(short notificada) {
		this.notificada = notificada;
	}
	
	

}