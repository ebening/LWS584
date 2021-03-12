package com.lowes.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lowes.dto.BusquedaFechasIdDTO;
import com.lowes.dto.VwSolicitudResumenAutorizacionDTO;
import com.lowes.entity.Parametro;
import com.lowes.entity.Proveedor;
import com.lowes.entity.Solicitud;
import com.lowes.entity.SolicitudAutorizacion;
import com.lowes.entity.Usuario;
import com.lowes.entity.VwSolicitudResumenAutorizacion;
import com.lowes.service.EmailService;
import com.lowes.service.ParametroService;
import com.lowes.service.SolicitudAutorizacionService;
import com.lowes.service.SolicitudManagerService;
import com.lowes.service.SolicitudService;
import com.lowes.service.UsuarioService;
import com.lowes.service.VwSolicitudResumenAutorizacionService;
import com.lowes.util.Etiquetas;
import com.lowes.util.Utilerias;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class SolicitudResumenAutorizacionController {

	private static final Logger logger = Logger.getLogger(DashboardController.class);
	Etiquetas etiquetas = new Etiquetas("es");
	
	@Value("${initBinder.autoGrowCollectionLimit}")
	private int autoGrowCollectionLimit;
	
	@Autowired
	private VwSolicitudResumenAutorizacionService vwSolicitudResumenAutorizacionService;

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ParametroService parametroService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private	SolicitudManagerService solicitudManagerService;
	
	@Autowired
	private SolicitudService solicitudService;
	
	@Autowired
	private SolicitudAutorizacionService solicitudAutorizacionService;

	
	@InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setAutoGrowCollectionLimit(autoGrowCollectionLimit);
    }
	
	// Al cargar la p�gina
	@RequestMapping(value = "solicitudResumenAutorizacion", method = RequestMethod.GET)
	public ModelAndView cargaSolicitudResumenAutorizacion(@RequestParam Integer idEstadoSolicitud) {
		logger.info("Cargando solicitudResumenAutorizacion");

		Usuario usuario = usuarioService.getUsuarioSesion();

		List<VwSolicitudResumenAutorizacion> vwSolicitudResumenAutorizacion = vwSolicitudResumenAutorizacionService
				.getAllVwSolicitudResumenAutorizacionByUsuarioEstatusSolicitud(usuario.getIdUsuario(),
						idEstadoSolicitud, null, null);
		
		VwSolicitudResumenAutorizacionDTO wSolicitudResumenAutorizacionDTO = new VwSolicitudResumenAutorizacionDTO();
		wSolicitudResumenAutorizacionDTO.setVwSolicitudResumenAutorizaciones(vwSolicitudResumenAutorizacion);
		
		if (wSolicitudResumenAutorizacionDTO != null){
			for (VwSolicitudResumenAutorizacion vsra : wSolicitudResumenAutorizacionDTO.getVwSolicitudResumenAutorizaciones()){
				if (vsra.getIdEstadoAutorizacion() == Etiquetas.ESTADO_AUTORIZACION_AUTORIZADO){
					vsra.setAutorizar(true);
				}
			}
		}
		
		Map<String, Object> sendItems = new HashMap<>();
		sendItems.put("vwSolicitudResumenAutorizacionDTO", wSolicitudResumenAutorizacionDTO);
		sendItems.put("busquedaFechasIdDTO", new BusquedaFechasIdDTO());
		sendItems.put("idEstadoSolicitud", idEstadoSolicitud);
		sendItems.put("mensajeSuccess", null);
		
		sendItems.put("ID_ESTADO_SOLICITUD_CAPTURADA", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudCapturada").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_POR_AUTORIZAR", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_AUTORIZADA", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_RECHAZADA", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudRechazada").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_VALIDADA", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudValidada").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_PAGADA", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudPagada").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_CANCELADA", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudCancelada").getValor()));
		sendItems.put("ID_ESTADO_SOLICITUD_MULTIPLE", Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudMultiple").getValor()));
		
		
		return new ModelAndView("solicitudResumenAutorizacion", sendItems);
	}
	
	
    // Al filtrar por fecha
	@RequestMapping(value = "iniciarBusquedaAutorizacion", method = RequestMethod.POST)
	public ModelAndView busquedaPorFecha(@ModelAttribute("BusquedaFechasIdDTO") BusquedaFechasIdDTO busquedaFechasIdDTO,
			HttpServletRequest request) {
		logger.info("Cargando iniciarBusqueda");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fechaInicial = null;
		String fechaFinal = null;

		if (busquedaFechasIdDTO.getFechaFinal() != null) {
			fechaInicial = df.format(busquedaFechasIdDTO.getFechaInicial());
		}

		if (busquedaFechasIdDTO.getFechaFinal() != null) {
			fechaFinal = df.format(busquedaFechasIdDTO.getFechaFinal());
		}

		Usuario usuario = usuarioService.getUsuarioSesion();

		List<VwSolicitudResumenAutorizacion> vwSolicitudResumenAutorizacion = vwSolicitudResumenAutorizacionService
				.getAllVwSolicitudResumenAutorizacionByUsuarioEstatusSolicitud(usuario.getIdUsuario(),
						busquedaFechasIdDTO.getIdBusqueda(), fechaInicial, fechaFinal);
		
		VwSolicitudResumenAutorizacionDTO wSolicitudResumenAutorizacionDTO = new VwSolicitudResumenAutorizacionDTO();
		wSolicitudResumenAutorizacionDTO.setVwSolicitudResumenAutorizaciones(vwSolicitudResumenAutorizacion);
		
		if (wSolicitudResumenAutorizacionDTO != null){
			for (VwSolicitudResumenAutorizacion vsra : wSolicitudResumenAutorizacionDTO.getVwSolicitudResumenAutorizaciones()){
				if (vsra.getIdEstadoAutorizacion() == Etiquetas.ESTADO_AUTORIZACION_AUTORIZADO){
					vsra.setAutorizar(true);
				}
			}
		}
		
		Map<String, Object> sendItems = new HashMap<>();

		sendItems.put("vwSolicitudResumenAutorizacionDTO", wSolicitudResumenAutorizacionDTO);
		sendItems.put("busquedaFechasIdDTO", busquedaFechasIdDTO);
		sendItems.put("idEstadoSolicitud", busquedaFechasIdDTO.getIdBusqueda());
		sendItems.put("mensajeSuccess", null);

		return new ModelAndView("solicitudResumenAutorizacion", sendItems);
	}
	
	@RequestMapping(value = "autorizarSolicitudes", method = RequestMethod.POST)
	public ModelAndView autorizarSolicitudes(@ModelAttribute("VwSolicitudResumenAutorizacionDTO") VwSolicitudResumenAutorizacionDTO vwSolicitudResumenAutorizacionDTO,
			HttpServletRequest request) {
		logger.info("Cargando autorizarSolicitudes");
		
		//Obtenci�n de par�metros previa
		Integer idBusqueda = vwSolicitudResumenAutorizacionDTO.getVwSolicitudResumenAutorizaciones().get(0).getIdEstadoSolicitud();
		Usuario usuario = usuarioService.getUsuarioSesion();
		
		//Variable de mensajes
		StringBuilder mensajeSalida = new StringBuilder();
		
		//Procesamiento de solicitudes con cambio
		for(VwSolicitudResumenAutorizacion vsra : vwSolicitudResumenAutorizacionDTO.getVwSolicitudResumenAutorizaciones()){
			if(vsra.isRechazar()){
				//Env�o a solicitud manager para rechazo.
				Solicitud solicitud = solicitudService.getSolicitud(vsra.getIdSolicitud());
				Map<Boolean, String> resultadoLocal = solicitudManagerService.rechazarSolicitud(solicitud, usuario, vsra.getMotivoRechazo());
				if(resultadoLocal.containsKey(true)){
					mensajeSalida.append(solicitud.getIdSolicitud());
					mensajeSalida.append(" : ");
					mensajeSalida.append(resultadoLocal.get(true));
					mensajeSalida.append("<br>");
					Integer envioCorreo = Integer.parseInt(parametroService.getParametroByName("emailEnviarNotificacion").getValor());
					if (envioCorreo == Etiquetas.UNO) {
						try {
							emailService.enviarCorreoRechazo(usuarioService.getUsuarioSesion(), solicitud, vsra.getMotivoRechazo());
						} catch (Exception e) {
							logger.info("El mensaje no pudo ser enviado");
						}
					}
				} else {
					mensajeSalida.append(solicitud.getIdSolicitud());
					mensajeSalida.append(" : ");
					mensajeSalida.append(resultadoLocal.get(false));
					mensajeSalida.append("<br>");
				}
			} else if (vsra.isAutorizar()){
				//Env�o a solicitud manager para autorizaci�n
				Solicitud solicitud = solicitudService.getSolicitud(vsra.getIdSolicitud());
				Map<Boolean, String> resultadoLocal = solicitudManagerService.autorizarSolicitud(solicitud, usuario);
				if(resultadoLocal.containsKey(true)){
					mensajeSalida.append(solicitud.getIdSolicitud());
					mensajeSalida.append(" : ");
					mensajeSalida.append(resultadoLocal.get(true));
					mensajeSalida.append("<br>");
				} else {
					mensajeSalida.append(solicitud.getIdSolicitud());
					mensajeSalida.append(" : ");
					mensajeSalida.append(resultadoLocal.get(false));
					mensajeSalida.append("<br>");
					
				}
			}
		}
		
		//Obtenci�n de resultados despu�s del proceso
		Map<String, Object> sendItems = new HashMap<>();
		
		List<VwSolicitudResumenAutorizacion> vwSolicitudResumenAutorizacion = vwSolicitudResumenAutorizacionService
				.getAllVwSolicitudResumenAutorizacionByUsuarioEstatusSolicitud(usuario.getIdUsuario(), idBusqueda, null, null);
		
		vwSolicitudResumenAutorizacionDTO.setVwSolicitudResumenAutorizaciones(vwSolicitudResumenAutorizacion);
		
		if (vwSolicitudResumenAutorizacionDTO != null){
			for (VwSolicitudResumenAutorizacion vsra : vwSolicitudResumenAutorizacionDTO.getVwSolicitudResumenAutorizaciones()){
				if (vsra.getIdEstadoAutorizacion() == Etiquetas.ESTADO_AUTORIZACION_AUTORIZADO){
					vsra.setAutorizar(true);
				}
			}
		}
		
		sendItems.put("vwSolicitudResumenAutorizacionDTO", vwSolicitudResumenAutorizacionDTO);
		sendItems.put("busquedaFechasIdDTO", new BusquedaFechasIdDTO());
		sendItems.put("idEstadoSolicitud", idBusqueda);
		sendItems.put("mensajeSuccess", mensajeSalida.toString());

		return new ModelAndView("solicitudResumenAutorizacion", sendItems);
	}
	
	
	

	@RequestMapping(value = "/validarSolicitudesAsignadas", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody ResponseEntity<String> validarSolicitudesAsignadas(HttpSession session, @RequestParam String solicitudes,
				HttpServletRequest request, HttpServletResponse response) {
		String json = null;
		HashMap<String, String> result = new HashMap<String, String>();
		List<Integer> solicitudesAsignadas = new ArrayList<Integer>();
		for(String idSolicitudStr : solicitudes.split(",")){
			if (idSolicitudStr != null && !idSolicitudStr.equals("")) {
				if(solicitudCorrespondeAlAutorizador(Integer.parseInt(idSolicitudStr)) == false){
                    solicitudesAsignadas.add(Integer.parseInt(idSolicitudStr));
				}
			}
		}
		
         
		if(!solicitudesAsignadas.isEmpty()){
			Parametro parametroMensaje = parametroService.getParametroByName("solicitudAsignada");
			result.put("resultado","true");
			result.put("mensaje", (parametroMensaje!=null?parametroMensaje.getValor():"La solicitud ya no te pertenece") + 
					": "+StringUtils.join(solicitudesAsignadas.toArray(), ","));
		}else{
			result.put("resultado", "false");
		}

		ObjectMapper map = new ObjectMapper();
		if (!result.isEmpty()) {
			try {
				json = map.writeValueAsString(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
	}
	
	private Boolean solicitudCorrespondeAlAutorizador(Integer idSolicitud) {
		List<Integer> solicitudesAsignadas = new ArrayList<Integer>();
		List<SolicitudAutorizacion> solicitudAutorizacionList = null;
		
		if (idSolicitud > Etiquetas.CERO) {
			solicitudAutorizacionList = solicitudAutorizacionService.getAllSolicitudAutorizacionBySolicitud((idSolicitud));
		}
		
//		for (SolicitudAutorizacion solicitudAutorizacion : solicitudAutorizacionList) {
//			if(solicitudAutorizacion.getUltimoMovimiento() == (short) 1){
//				Usuario usuario = usuarioService.getUsuarioSesion();
//				if(solicitudAutorizacion.getUsuarioByIdUsuarioAutoriza().getIdUsuario() != usuario.getIdUsuario() || solicitudAutorizacion.getFechaAutoriza() != null){ //si la solicitud tiene fecha de autorizacion ya no pertenece al usuario logueado, ya fue procesado
//					solicitudesAsignadas.add(solicitudAutorizacion.getSolicitud().getIdSolicitud());
//				}
//			}
//		}
		
		/* PARA OBTENER LA SOL_AUTORIZACION A VALIDAR SE REVISA EL ESTADO DE LA LA AUTORIZACION DEBE SER 1 (Por Autorizar) 
		 * Y LA FECHA DE AUTORIZACION DEBE SER NULA */
		Integer porAutorizar = Integer.parseInt(parametroService.getParametroByName("idEstadoAutorizacionPorAutorizar").getValor());
		Usuario usuario = usuarioService.getUsuarioSesion();
		Boolean esAutorizadorActual = false;
		for (SolicitudAutorizacion solicitudAutorizacion : solicitudAutorizacionList) {
			
		  if(solicitudAutorizacion.getEstadoAutorizacion().getIdEstadoAutorizacion() == porAutorizar && solicitudAutorizacion.getFechaAutoriza() == null){
			//si el usuario en sesion es diferente del usuario autorizador actuale ntonces ya fue procesada y alguien mas es el autorizador.
			if(solicitudAutorizacion.getUsuarioByIdUsuarioAutoriza().getIdUsuario() == usuario.getIdUsuario()){
				esAutorizadorActual = true;
				break;
			}
		  }
		}
		return esAutorizadorActual;
	}

}
