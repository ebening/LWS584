package com.lowes.controller;

import java.util.HashMap;
import java.util.Map;

import mx.adinfi.mail.sender.EmailSender;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.lowes.entity.Parametro;
import com.lowes.entity.Usuario;
import com.lowes.service.ParametroService;
import com.lowes.service.SolicitudService;
import com.lowes.service.UsuarioService;
import com.lowes.service.VwSolicitudResumenAutorizacionService;
import com.lowes.util.Etiquetas;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class DashboardController {

	private static final Logger logger = Logger.getLogger(DashboardController.class);

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private SolicitudService solicitudService;

	@Autowired
	private VwSolicitudResumenAutorizacionService vwSolicitudResumenAutorizacionService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private EmailSender emailSender;

	public DashboardController() {
		logger.info("DashboardController()");
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView dashboard() {
		long time = System.currentTimeMillis();
		logger.info("Cargando dashboard");
		
		// Carga del usuario en sesion
		Usuario usuario = usuarioService.getUsuarioSesion();
		logger.info("Usuario: " + usuario.getCuenta());

		Parametro puestoAP = parametroService.getParametroByName("puestoAutorizacionAP");
		Parametro puestoAP2 = parametroService.getParametroByName("puestoValidacionAP");
		Parametro puestoConfirmacionAP = parametroService.getParametroByName("puestoConfirmacionAP");

		Map<String, Long> dashboardValues = new HashMap<>();
		
		/* CONTEO DE SOLICITUDES NUEVAS */
		Long solicitudesNuevas = 0l;
		solicitudesNuevas = solicitudesNuevas + solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(),Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudCapturada").getValor()));
		/* EXEPCION DE CAJA CHICA POR USUARIO CREACION. */
		solicitudesNuevas = solicitudesNuevas
				+ solicitudService.getSolicitudCountByUsuarioEstatusCajaChica(usuario.getIdUsuario(),
						Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudCapturada").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudCajaChica").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudNoMercanciasXML").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudNoMercanciasSinXML").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudReembolsos").getValor()));

		dashboardValues.put("capturadas",solicitudesNuevas);

		/* SOLICITUDES ENVIADAS A AUTORIZACIÓN Y AUTORIZADAS */
		Long solicitudesEnAutorizacionYAutorizadas = 0l;
        solicitudesEnAutorizacionYAutorizadas = solicitudesEnAutorizacionYAutorizadas + solicitudService.getSolicitudCountByUsuarioEstatusDoble(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor()), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor()));
		/* EXEPCION DE CAJA CHICA POR USUARIO CREACION. */
        solicitudesEnAutorizacionYAutorizadas = solicitudesEnAutorizacionYAutorizadas + solicitudService.getSolicitudCountByUsuarioEstatusDobleCajaChica(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor()), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor()));
		dashboardValues.put("autorizacion", solicitudesEnAutorizacionYAutorizadas);
		
		/* ?? OBSOLETO POR MODIFICACION DE DASHBOARD: SE UNIO AUTORIZADAS Y RECHAZADAS.*/
		dashboardValues.put("autorizadas", solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor())));
		
		/* SOLICITUDES RECHAZADAS */
        Long solicitudesRechazadas = 0l;
        solicitudesRechazadas = solicitudesRechazadas +	solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudRechazada").getValor()));
		/* EXEPCION DE CAJA CHICA POR USUARIO CREACION. */
		solicitudesRechazadas = solicitudesRechazadas
				+ solicitudService.getSolicitudCountByUsuarioEstatusCajaChica(usuario.getIdUsuario(),
						Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudRechazada").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudCajaChica").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudNoMercanciasXML").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudNoMercanciasSinXML").getValor()),
						Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudReembolsos").getValor()));

        dashboardValues.put("rechazadas",solicitudesRechazadas);
		
		/* COMPROBACIONES */
		/* COMPROBACION ANTICIPO */
		Long comprobacionAnticipo = 0l;
		comprobacionAnticipo = comprobacionAnticipo +	solicitudService.getSolicitudCountByAnticipoPentiente(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudAnticipo").getValor()),  Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudPagada").getValor()));
		dashboardValues.put("comprobacionAnticipo",comprobacionAnticipo);
		
		/* COMPROBACION ANTICIPO VIAJE */
		Long comprobacionAnticipoViaje = 0l;
		comprobacionAnticipoViaje = comprobacionAnticipoViaje +	solicitudService.getSolicitudCountByAnticipoPentiente(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idTipoSolicitudAnticipoGastosViaje").getValor()),  Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudPagada").getValor()));
		dashboardValues.put("comprobacionAnticipoViaje",comprobacionAnticipoViaje);
		
		/* COMPROBACION AMEX */
		Long comprobacionAmex = 0l;
		dashboardValues.put("comprobacionAmex",comprobacionAmex);
		
		/* FIN COMPROBACIONES */
		dashboardValues.put("validadas", solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudValidada").getValor())));
		dashboardValues.put("autorizar", vwSolicitudResumenAutorizacionService.getCountSolicitudAutorizacionByUsuario(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor())));
		dashboardValues.put("validar", vwSolicitudResumenAutorizacionService.getCountSolicitudAutorizacionByUsuario(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor())));
		dashboardValues.put("confirmar", vwSolicitudResumenAutorizacionService.getCountSolicitudAutorizacionByUsuario(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudValidada").getValor())));
		dashboardValues.put("pendientesConfirmar", vwSolicitudResumenAutorizacionService.getCountSolicitudAutorizacionByUsuario(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudConfirmada").getValor())));
		dashboardValues.put("esSolicitante",usuario.getEsSolicitante() == Etiquetas.UNO ? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		dashboardValues.put("esAutorizador",usuario.getEsAutorizador() == Etiquetas.UNO ? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		dashboardValues.put("esAP", usuario.getPuesto().getIdPuesto() == Integer.parseInt(puestoAP.getValor())? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		dashboardValues.put("esAP2", usuario.getPuesto().getIdPuesto() == Integer.parseInt(puestoAP2.getValor())? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		dashboardValues.put("puestoConfirmacionAP", usuario.getPuesto().getIdPuesto() == Integer.parseInt(puestoConfirmacionAP.getValor())? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		
		
		dashboardValues.put("ID_ESTADO_SOLICITUD_CAPTURADA", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudCapturada").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_POR_AUTORIZAR", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_AUTORIZADA", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_RECHAZADA", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudRechazada").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_VALIDADA", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudValidada").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_PAGADA", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudPagada").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_CANCELADA", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudCancelada").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_POR_CONFIRMAR", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudConfirmada").getValor()));
		dashboardValues.put("ID_ESTADO_SOLICITUD_MULTIPLE", Long.parseLong(parametroService.getParametroByName("idEstadoSolicitudMultiple").getValor()));
		
		
		/*ID's de tipos de solicitud para logica de listado en la informacion de la busqueda.*/
		dashboardValues.put("SOLICITUD_NO_MERCANCIAS_CON_XML", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudNoMercanciasXML").getValor()));
		dashboardValues.put("SOLICITUD_NO_MERCANCIAS_SIN_XML", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudNoMercanciasSinXML").getValor()));
		dashboardValues.put("SOLICITUD_REEMBOLSOS", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudReembolsos").getValor()));
		dashboardValues.put("SOLICITUD_CAJA_CHICA", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudCajaChica").getValor()));
		dashboardValues.put("SOLICITUD_KILOMETRAJE", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudKilometraje").getValor()));
		dashboardValues.put("SOLICITUD_ANTICIPO", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudAnticipo").getValor()));
		dashboardValues.put("SOLICITUD_COMPROBACION_ANTICIPO", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudComprobacionAnticipo").getValor()));
		dashboardValues.put("SOLICITUD_ANTICIPO_GASTOS_VIAJE", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudAnticipoGastosViaje").getValor()));
		dashboardValues.put("SOLICITUD_COMPROBACION_ANTICIPO_GASTOS_VIAJE", Long.parseLong(parametroService.getParametroByName("idTipoSolicitudComprobacionAnticipoViaje").getValor()));
		
		
		
		logger.info("Dashboad cargado en: "+(System.currentTimeMillis()-time)+" ms");
		return new ModelAndView("dashboard", dashboardValues);
	}

	@RequestMapping(value = "/testemail", method = RequestMethod.GET)
	public ModelAndView sender() {

		try {
			emailSender.sendMail("josues@adinfi.com",
					"<html xmlns=\"http://www.w3.org/1999/xhtml\"> <body style=\"margin: 0; padding: 0; background-color: #F2F2F2;\"> HOLA MUNDO </body> </html>",
					"Subject configurable");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Cargando dashboard");

		// Carga del usuario en sesion
		Usuario usuario = usuarioService.getUsuarioSesion();

		logger.info("Usuario: " + usuario.getCuenta());

		Parametro puestoAP = parametroService.getParametroByName("puestoAutorizacionAP");

		Map<String, Long> dashboardValues = new HashMap<>();
		dashboardValues.put("capturadas", solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudCapturada").getValor())));
		dashboardValues.put("autorizacion", solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(),	Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor())));
		dashboardValues.put("rechazadas", solicitudService.getSolicitudCountByUsuarioEstatus(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudRechazada").getValor())));
		dashboardValues.put("autorizar", vwSolicitudResumenAutorizacionService.getCountSolicitudAutorizacionByUsuario(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor())));
		dashboardValues.put("validar", vwSolicitudResumenAutorizacionService.getCountSolicitudAutorizacionByUsuario(usuario.getIdUsuario(), Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacionFase2").getValor())));
		dashboardValues.put("esSolicitante", usuario.getEsSolicitante() == Etiquetas.UNO ? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		dashboardValues.put("esAutorizador", usuario.getEsAutorizador() == Etiquetas.UNO ? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		dashboardValues.put("esAP", usuario.getPuesto().getIdPuesto() == Integer.parseInt(puestoAP.getValor()) ? Etiquetas.UNO.longValue() : Etiquetas.CERO.longValue());
		
		

		return new ModelAndView("dashboard", dashboardValues);

	}
}
