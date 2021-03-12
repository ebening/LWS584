/**
 * 
 */
package com.lowes.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.lowes.entity.ComprobacionAnticipo;
import com.lowes.entity.EncabezadoEbs;
import com.lowes.entity.EstadoSolicitud;
import com.lowes.entity.Notificacion;
import com.lowes.entity.Solicitud;
import com.lowes.entity.SolicitudFechaPagoEbs;
import com.lowes.service.ComprobacionAnticipoService;
import com.lowes.service.EmailService;
import com.lowes.service.EncabezadoEbsService;
import com.lowes.service.FacturaArchivoService;
import com.lowes.service.NotificacionService;
import com.lowes.service.ParametroService;
import com.lowes.service.SolicitudArchivoService;
import com.lowes.service.SolicitudFechaPagoEbsService;
import com.lowes.service.SolicitudManagerService;
import com.lowes.service.SolicitudService;
import com.lowes.util.CMConnection;
import com.lowes.util.Etiquetas;

/**
 * @author miguelr
 *
 */
@Controller
@Configuration
@EnableScheduling
public class NotificacionController {
	@Autowired
	private SolicitudService solicitudService;
	@Autowired
	private FacturaArchivoService facturaArchivoService;
	@Autowired
	private SolicitudArchivoService solicitudArchivoService;
	@Autowired
	private NotificacionService notificacionService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ParametroService parametroService;
	@Autowired
	private SolicitudManagerService solcitudManagerService;
	@Autowired
	private SolicitudFechaPagoEbsService solicitudFechaPagoEbsService;
	@Autowired
	private EncabezadoEbsService encabezadoEbsService;
	@Autowired
	private ComprobacionAnticipoService comprobacionAnticipoService;
	
	private static final Logger logger = Logger.getLogger(CMConnection.class);
	/**
	 * @author miguelr
	 * 
	 */
	@Scheduled(cron = "${cron.notificacion}")
	//@RequestMapping("testCronNEnvio")
	public void enviaNotificaciones(){
		logger.info("Notificaciones: Inicia envío notificaciones por anticipos pendientes.");
		enviaAnticipoPendiente(Integer.parseInt(parametroService.getParametroByName("idAntiguedadAnticipo").getValor()));
		enviaAnticipoViajePendiente(Integer.parseInt(parametroService.getParametroByName("idAntiguedadAnticipoGastoViaje").getValor()));
	}
	
	/*
	 * Revisa la tabla de SOLICITUD_FECHA_PAGO_EBS
	 * - Cambia el estado de solicitud a : Pagada
	 * - En el caso de solicitudes tipo Anticipo y Anticipo Viaje: Crea sus notificaciones de comprobacion.
	 * 
	 * */
	@Scheduled(cron = "${cron.notificacionesPagadas}")
	//@RequestMapping("testCronN")
	public void revisarPagadasYCrearNotificaciones(){
		
		List<SolicitudFechaPagoEbs> solicitudesPagadas = solicitudFechaPagoEbsService.getSolicitudFechaPagoEbsNoNotificadas();
		
		Integer idEstadoSolicitudPagada = Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudPagada").getValor());
		Integer idTipoSolicitudAnticipo = Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudAnticipo").getValor());
		Integer idTipoSolicitudAnticipoGastosViaje = Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudAnticipoGastosViaje").getValor());
		
		if(solicitudesPagadas != null && solicitudesPagadas.isEmpty() == false){
			// if existen solicitudes pagadas
			for(SolicitudFechaPagoEbs solicitudesPagada : solicitudesPagadas ){
				
				//* Actualizar el stadus de la solictud a pagada
				Solicitud sol  = new Solicitud();
				
			    //sol = getSolicitudFromEBSPagadas(solicitudesPagada);  metodo por concatenadod el id de la solicitud
			    sol = getSolicitudFromEBSPagadasByInvoiceNum(solicitudesPagada); //metodo por el invoice num  ebsEncabezado.invoicenum = pagadas.invoicenum (Id solicitud de encabezado)
			   
			    if(sol != null && sol.getIdSolicitud() > 0){
			    	  solicitudService.updateEstadoSolicitud(new EstadoSolicitud(idEstadoSolicitudPagada), sol.getIdSolicitud());
						//si el tipo de solicitud es tipo anticipo o anticipo de gastos de viaje, se crea la notificacion.
						if(sol.getTipoSolicitud().getIdTipoSolicitud() == idTipoSolicitudAnticipo 
								|| sol.getTipoSolicitud().getIdTipoSolicitud() == idTipoSolicitudAnticipoGastosViaje){
							solcitudManagerService.crearNotificacion(sol, solicitudesPagada.getFechaPago());
						}
						
				//Actualizar estado de 	notificada para todos los tipos de solicitud
				//servira como identificador para no volver a procesar nuevamente esa informacion de la tabla SOLICITUD_FECHA_PAGO_EBS
				 
				solicitudFechaPagoEbsService.updateSolicitudNotificada(Etiquetas.UNO, solicitudesPagada.getIdSolicitudFechaPagoEbs());		
				
						
			    }else{
			    	
					logger.info("Solicitud no encontrada en la tabla de solicitudes: no fue posible actualizar el estado a pagada");
					logger.info("Id solcitud pagada (SolicitudFechaPagoEbs): "+solicitudesPagada.getIdSolicitudFechaPagoEbs());
					
			    }//if-else
			    
			}//for
			
	}else{
		// if no existen solicitudes pagadas
		logger.info("El proceso automatico no encontro solicitudes pagadas nuevas en la Fecha: "+new Date());
	}
		
	}//revisarPagadasYCrearNotificaciones
	
	// este metodo identifica el id de solicitud.
	// indentificamos el id de solicitud que viene concatenado al final del String. Ejemplo: TE - 27102016 - 1375
	private Solicitud getSolicitudFromEBSPagadas(SolicitudFechaPagoEbs solicitudPagadaEBS){
		
		Solicitud solicitud = new Solicitud();
		
		//declaraciones 
		String invoiceNum = solicitudPagadaEBS.getInvoiceNum();
		invoiceNum = invoiceNum.trim().replaceAll("\\s+","");
		char[] invoiceNumArray = invoiceNum.toCharArray();
		StringBuilder idSolicitud = new StringBuilder();
		List<String> idSolicitudList = new ArrayList<>();
		LinkedList<String> idSol =  new LinkedList<>();
		
		// elementos del arreglo de caracteres se pasa a una Lista de Strings 
		for(int i = 0; i < invoiceNumArray.length ; i++){
	        idSolicitudList.add(String.valueOf(invoiceNumArray[i]));
		}
	    
		//usando guava se itera en reversa hasta encontrar el delimitador "-" 
		//se almacenan los items para conformar el id de solicitud
		for (String item : Lists.reverse(idSolicitudList))
		{
			if(item.equals("-")){
				break;
			}else{
				if(item.isEmpty() == false){
					idSolicitud.append(item);
					idSol.push(item);
				}
			}
		}
		
		
		//se voltea el stringBuilder de los items agregados ya que se agregaron de atras hacia adelante
		//y se busca la solicitud para regresarla en el metodo.
		String idSolicitudInt =  idSolicitud.reverse().toString();
		if(idSolicitud.toString() != null && NumberUtils.isNumber(idSolicitudInt)){
			solicitud = solicitudService.getSolicitud(Integer.parseInt(idSolicitudInt));
		}
		
		
		return solicitud;
	}
	
	
	
	private Solicitud getSolicitudFromEBSPagadasByInvoiceNum(SolicitudFechaPagoEbs solicitudPagadaEBS){
		List<EncabezadoEbs> solicitudesEnEbs =  encabezadoEbsService.getEncabezadoEbsByInvoiceNum(solicitudPagadaEBS.getInvoiceNum());
		Solicitud solicitud = new Solicitud();
		if(solicitudesEnEbs != null && solicitudesEnEbs.isEmpty() == false){
			solicitud = solicitudService.getSolicitud(solicitudesEnEbs.get(0).getSolicitudId());
		}
		return solicitud;
	}
	
	/*
	 * N = NUEVA
	 * P = PROCESADA (En proceso de envio de notificaciones, dentro de los tres envios)
	 * E = ENVIADA (ya se enviaron los 3 envios)
	 * W = WARNING (No se envio)
	 * A = APROBADA
	 * 
	 * */
	
	/**
	 * @author miguelr
	 * @param tipoNotificacion
	 * ENVÍA NOTIFICACIONES DIARIAS PROGRAMADAS DE ANTICIPOS CON RETRASO DE ACUERDO A LA TABLA DE NOTIFICACIONES.
	 */
	public void enviaAnticipoPendiente(int tipoNotificacion){
		
		//La notificacion del anticipo es para el dia de hoy?
		List<Notificacion> lstNotificacion = notificacionService.getNotificacionesPendientesByTipo(tipoNotificacion);
		for(Notificacion not : lstNotificacion){
			
			//Si está pendiente o ya a sido comprobada?
			if(not.getSolicitud().getComprobada() == Etiquetas.CERO){
				
				// en que numero de notificacion va?
				int nextParam = not.getNumNotificacion() + 1;
				not.setNumNotificacion(nextParam);
				
				//La solicitud a sido notificada ya las 3 veces?
				if(nextParam < Etiquetas.TRES){
					
					//Se envio la notificacion?
					if(emailService.enviarCorreoRetraso(not.getUsuario().getCorreoElectronico(), not)){
						
					int antiguedad = Integer.parseInt(parametroService.getParametroByName("anticipoDiasAntiguedad" + (nextParam + 1)).getValor());
					Calendar proximoEnvio = Calendar.getInstance();
					proximoEnvio.setTime(not.getProximoEnvio()); // Configuramos la fecha que se recibe
					proximoEnvio.add(Calendar.DAY_OF_YEAR, antiguedad);  // numero de días a añadir, o restar en caso de días<0
					not.setProximoEnvio(proximoEnvio.getTime());
					// estado P procesada ya estan en proceso de envio 
					not.setEstatus("P");
					
					}else{
						
						// estado w = warning no se envio la notificacion
						not.setEstatus("W");
						notificacionService.updateNotificacion(not);
						logger.info("Notificaciones: La notificación para la solicitud "+ not.getSolicitud().getIdSolicitud() +" no pudo ser enviada.");
						
					}
					
				}else{
					// estoado e = enviada ya se enviaron las 3 notificaciones
					not.setEstatus("E");
					notificacionService.updateNotificacion(not);					
				}
				
			}
			//estado A = Si está comprobada
			else if(not.getSolicitud().getComprobada() == Etiquetas.UNO){
				not.setEstatus("A");
				notificacionService.updateNotificacion(not);
			}
			//Si error
			else if(not.getSolicitud().getComprobada() == null){
				not.setEstatus("W");
				notificacionService.updateNotificacion(not);
				logger.info("Notificaciones: La solicitud no tiene estatus de comprobación.");
			}
		}
		logger.info("Notificaciones: " + lstNotificacion.size() + " notificaciones por anticipos pendientes enviadas.");
	}
	
	/**
	 * @author erikag
	 * @param tipoNotificacion
	 * ENVÍA NOTIFICACIONES DIARIAS PROGRAMADAS DE ANTICIPOS DE GASTOS DE VIAJE CON RETRASO DE ACUERDO A LA TABLA DE NOTIFICACIONES.
	 */
	public void enviaAnticipoViajePendiente(int tipoNotificacion){
		
	//La notificacion del anticipo es para el dia de hoy?
	List<Notificacion> lstNotificacion = notificacionService.getNotificacionesPendientesByTipo(tipoNotificacion);
		for(Notificacion not : lstNotificacion){
			
			//Si está pendiente o ya a sido comprobada?
			if(not.getSolicitud().getComprobada() == Etiquetas.CERO){
				
				int nextParam = not.getNumNotificacion() + 1;
				not.setNumNotificacion(nextParam);
				
				//La solicitud a sido notificada ya las 3 veces?
				if(nextParam < Etiquetas.TRES){
					
					//enviar notificacion / Se envio la notificacion?
					if(emailService.enviarCorreoRetraso(not.getUsuario().getCorreoElectronico(), not)){
						
					int antiguedad = Integer.parseInt(parametroService.getParametroByName("anticipoDiasAntiguedad" + (nextParam + 1)).getValor());
					Calendar proximoEnvio = Calendar.getInstance();
					proximoEnvio.setTime(not.getProximoEnvio()); // Configuramos la fecha que se recibe
					proximoEnvio.add(Calendar.DAY_OF_YEAR, antiguedad);  // numero de días a añadir, o restar en caso de días<0
					not.setProximoEnvio(proximoEnvio.getTime());
					// estado P procesada ya estan en proceso de envio 
					not.setEstatus("P");
					
					}else{
						
						// estado w = warning no se envio la notificacion
						not.setEstatus("W");
						notificacionService.updateNotificacion(not);
						logger.info("Notificaciones: La notificación para la solicitud "+ not.getSolicitud().getIdSolicitud() +" no pudo ser enviada.");
						
					}
					
				}else{
					// estoado e = enviada ya se enviaron las 3 notificaciones
					not.setEstatus("E");
					notificacionService.updateNotificacion(not);					
				}
				
			}
			//estado A = Si está comprobada
			else if(not.getSolicitud().getComprobada() == Etiquetas.UNO){
				not.setEstatus("A");
				notificacionService.updateNotificacion(not);
			}
			//Si error
			else if(not.getSolicitud().getComprobada() == null){
				not.setEstatus("W");
				notificacionService.updateNotificacion(not);
				logger.info("Notificaciones: La solicitud no tiene estatus de comprobación.");
			}
		}
		logger.info("Notificaciones: " + lstNotificacion.size() + " notificaciones por anticipos pendientes enviadas.");
	}
	
	
	private boolean isEnProcesoDeComprobacion(Integer idSolicitud){
		
		boolean estaEnProceso = false;
		
		Integer idEstadoEnAutorizacion = 1;
		
		
		ComprobacionAnticipo comprobacion = comprobacionAnticipoService.getComprobacionByAnticipo(idSolicitud);
		if(comprobacion != null && comprobacion.getSolicitudByIdSolicitudAnticipo() != null && comprobacion.getSolicitudByIdSolicitudAnticipo().getIdSolicitud()>0){
			
		}
	
		return false;
	}
	
	
}
