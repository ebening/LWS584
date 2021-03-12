package com.lowes.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowes.entity.EstadoAutorizacion;
import com.lowes.entity.EstadoSolicitud;
import com.lowes.entity.Solicitud;
import com.lowes.entity.SolicitudAutorizacion;
import com.lowes.entity.TipoCriterio;
import com.lowes.entity.Usuario;
import com.lowes.service.CriterioService;
import com.lowes.service.EstadoAutorizacionService;
import com.lowes.service.EstadoSolicitudService;
import com.lowes.service.ParametroService;
import com.lowes.service.SolicitudAutorizacionService;
import com.lowes.service.TipoCriterioService;
import com.lowes.service.UsuarioService;
import com.lowes.util.Etiquetas;
import com.lowes.service.EmailService;

@Configuration
@Service
@Transactional
public class CriterioCajaChicaImpl implements CriterioService {

	private static final Logger logger = Logger.getLogger(CriterioSolicitanteImpl.class);

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstadoSolicitudService estadoSolicitudService;

	@Autowired
	private EstadoAutorizacionService estadoAutorizacionService;

	@Autowired
	private TipoCriterioService tipoCriterioService;

	@Autowired
	private SolicitudAutorizacionService solicitudAutorizacionService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private EmailService emailService;
	
	private String ultimoAutorizador;
	private Usuario usuarioDAC;

	@Override
	public boolean crearSolicitud(Solicitud solicitud, Usuario usuario) {
		
		// Obtener �ltimo autorizador
		String autorizador = "";
		this.setUltimoAutorizador(null);
		if (solicitudAutorizacionService.getLastSolicitudAutorizacion(solicitud.getIdSolicitud()) != null
				&& solicitudAutorizacionService.getLastSolicitudAutorizacion(solicitud.getIdSolicitud())
						.getUsuarioByIdUsuarioAutoriza() != null) {
			Usuario u = solicitudAutorizacionService.getLastSolicitudAutorizacion(solicitud.getIdSolicitud())
					.getUsuarioByIdUsuarioAutoriza();
			autorizador = u.getNombre() + " " + u.getApellidoPaterno() + " " + u.getApellidoMaterno();
			this.setUltimoAutorizador(autorizador);
		}
				
		// Validar si existen usuarios con puesto de AP.

		Integer puestoDACParam = Integer.parseInt(parametroService.getParametroByName("puestoAutorizacionDAC").getValor());
		Integer tipoLocacionCSC = Integer.parseInt(parametroService.getParametroByName("tipoLocacionCSC").getValor());

		List<Usuario> usuariosDAC = usuarioService.getUsuariosByPuesto(puestoDACParam);

		if (usuariosDAC.isEmpty() == false && solicitud.getUsuarioByIdUsuarioSolicita().getLocacion().getTipoLocacion().getIdTipoLocacion() == tipoLocacionCSC) {
//			if (false) {
			
			//Marcar paso anterior como "no actual"
			List<SolicitudAutorizacion> solicitudAutorizacionAnterior = solicitudAutorizacionService.getAllSolicitudAutorizacionActivaBySolicitud(solicitud.getIdSolicitud());
			
			for(SolicitudAutorizacion sa : solicitudAutorizacionAnterior){
				sa.setUltimoMovimiento(Etiquetas.CERO_S);
				solicitudAutorizacionService.updateSolicitudAutorizacion(sa);
			}

			Usuario usuarioDAC = usuariosDAC.get(0);
			this.setUsuarioDAC(usuarioDAC);
			
			logger.info("Asignando la solicitud: " + solicitud.getIdSolicitud() + " al usuario: "
					+ usuarioDAC.getIdUsuario());
			
            /* SI YA AUTORIZO PREVIAMENTE ESTE USUARIO, YA NO SE ENVIA A VALIDAR AL USUARIO: INTERNAMENTE SE AUTORIZA */
			if(yaAutorizo(solicitud, usuarioDAC)){
				crearSolicitudConfirmada(solicitud, usuarioDAC, solicitud.getUsuarioByIdUsuario());
				return true;
			}else{
				
				/*SI EL AUTORIZADOR ES EL USUARIO QUE REGISTRO LA SOLICITUD TAMBIEN SE OMITE (SE CONFIRMA AUTOMATICAMENTE)*/
				if(autorizadorEsSolicitanteCreador(usuarioDAC, solicitud)){
					crearSolicitudConfirmada(solicitud, usuarioDAC, solicitud.getUsuarioByIdUsuario());
					return true;
				}else{
					
					/*
					 * SI ESTE USUARIO NO A AUTORIZADO PREVIAMENTE Y NO ES EL CREADOR DE LA SOLICITUD SE ENVIA
					 * AL USUARIO PARA SU AUTORIZACI�N
					 */
					
					SolicitudAutorizacion solicitudAutorizacion = new SolicitudAutorizacion(Etiquetas.CERO,
							estadoAutorizacionService.getEstadoAutorizacion(Etiquetas.ESTADO_AUTORIZACION_POR_REVISAR), //1
							estadoSolicitudService.getEstadoSolicitud(Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor())), //2
							solicitud, 
							tipoCriterioService.getTipoCriterio(Etiquetas.TIPO_CRITERIO_CAJA_CHICA), //7
							usuarioDAC, 
							usuario, 
							Etiquetas.UNO_S,
							Etiquetas.UNO_S, 
							new Date(), 
							usuario.getIdUsuario());

					solicitudAutorizacionService.createSolicitudAutorizacion(solicitudAutorizacion);
					return true;
				}
			}
		
		} else {
			logger.info("El criterio de Caja chica no se cumple.");
			return false;
		}
	}

	@Override
	public boolean validarPasoCompleto(Solicitud solicitud, Usuario usuario) {
		
		 List<SolicitudAutorizacion> autorizaron = new ArrayList<>();
         List<SolicitudAutorizacion> autorizaronTodos = solicitudAutorizacionService.getAllSolicitudAutorizacionBySolicitud(solicitud.getIdSolicitud());
 		
        Integer puestoDACParam = Integer.parseInt(parametroService.getParametroByName("puestoAutorizacionDAC").getValor());
 		Integer tipoLocacionCSC = Integer.parseInt(parametroService.getParametroByName("tipoLocacionCSC").getValor());

 		List<Usuario> usuariosDAC = usuarioService.getUsuariosByPuesto(puestoDACParam);
         if (usuariosDAC.isEmpty() == false && solicitud.getUsuarioByIdUsuarioSolicita().getLocacion().getTipoLocacion().getIdTipoLocacion() == tipoLocacionCSC) {
        		//recorre toda la lista de autorizadores en solicitud autorizacion
  			if (autorizaronTodos != null && autorizaronTodos.size() > Etiquetas.CERO) {
  				for (SolicitudAutorizacion autorizo : autorizaronTodos) {
  					//si encuentra un rechazado limpia la lista
  					if (autorizo.getEstadoAutorizacion().getIdEstadoAutorizacion() == Etiquetas.ESTADO_AUTORIZACION_RECHAZADO) {
  						autorizaron.clear();
  					} else {
  						//de lo contrario lo agrega
  						if(autorizo.getTipoCriterio().getIdTipoCriterio() == Etiquetas.TIPO_CRITERIO_CAJA_CHICA && autorizo.getEstadoAutorizacion().getIdEstadoAutorizacion() == Etiquetas.ESTADO_AUTORIZACION_AUTORIZADO
  								&& (autorizo.getRechazado() == null || autorizo.getRechazado() != 1))
  							//de lo contrario lo agrega
  							autorizaron.add(autorizo);
  					}
  				}
  				
  			}
  			
  			if(autorizaron != null && autorizaron.size() > 0){
  				return true;
  			}else{
  				return false;
  			}
 		 }else{
 			// no se cumple. 
 			return true;
 		}
	
	}

	@Override
	public void enviarCorreo(Solicitud solicitud) {
		logger.info("enviar correo CriterioCajaChica");
		// Notificaci�n email
		
		if(solicitud.getEstadoSolicitud().getIdEstadoSolicitud() == Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudRechazada").getValor())){
			this.setUltimoAutorizador(null);
		}

		// Verificar en base de datos si el env�o de notificaciones est� activo
		Integer envioCorreo = Integer
				.parseInt(parametroService.getParametroByName("emailEnviarNotificacion").getValor());

		if (envioCorreo == Etiquetas.UNO) {
			try {
				logger.info("Enviando correo a: " + this.getUsuarioDAC().getNumeroNombreCompletoUsuario()
						+ " - Solicitud: " + solicitud.getIdSolicitud());
				emailService.enviarCorreo(Integer.parseInt(parametroService.getParametroByName("idCriterioCajaChica").getValor()), this.getUltimoAutorizador(), 
					this.getUsuarioDAC().getCorreoElectronico(), solicitud);
				
			} catch (Exception e) {
				logger.info("El mensaje no pudo ser enviado");
			}
		}
	}
	
	public String getUltimoAutorizador() {
		return ultimoAutorizador;
	}

	public void setUltimoAutorizador(String ultimoAutorizador) {
		this.ultimoAutorizador = ultimoAutorizador;
	}

	public Usuario getUsuarioDAC() {
		return usuarioDAC;
	}

	public void setUsuarioDAC(Usuario usuarioDAC) {
		this.usuarioDAC = usuarioDAC;
	}
	
	
	@Override
	public boolean crearSolicitudConfirmada(Solicitud solicitud, Usuario usuarioAutoriza, Usuario usuarioSolicita) {

		SolicitudAutorizacion solicitudAutorizacion = new SolicitudAutorizacion();
		
		//setters para la autorizacion automatica.
		solicitudAutorizacion.setSolicitud(solicitud);
		solicitudAutorizacion.setEstadoSolicitud(new EstadoSolicitud(Integer.parseInt(parametroService.getParametroByName("idEstadoSolicitudEnAutorizacion").getValor())));
		solicitudAutorizacion.setTipoCriterio(new TipoCriterio(Etiquetas.TIPO_CRITERIO_CAJA_CHICA));
		solicitudAutorizacion.setEstadoAutorizacion(new EstadoAutorizacion(Etiquetas.ESTADO_AUTORIZACION_AUTORIZADO));
		solicitudAutorizacion.setUsuarioByIdUsuarioEnvia(usuarioSolicita);
		solicitudAutorizacion.setUsuarioByIdUsuarioAutoriza(usuarioAutoriza);
		solicitudAutorizacion.setUltimoMovimiento(Etiquetas.CERO_S);  // revisar comportamiento de ultimo movimiento
		solicitudAutorizacion.setFechaAutoriza(new Date());
		
		/*campos de control de tabla*/
		
		solicitudAutorizacion.setActivo(Etiquetas.UNO_S);
		solicitudAutorizacion.setCreacionFecha(new Date());
		solicitudAutorizacion.setCreacionUsuario(usuarioService.getUsuarioSesion().getIdUsuario());
		solicitudAutorizacion.setVisible(Etiquetas.CERO_S);
		
		Integer idSolAut = solicitudAutorizacionService.createSolicitudAutorizacion(solicitudAutorizacion);
		
		if(idSolAut != null && idSolAut > 0){
			return true;
		}else{
			return false;
		}
		
	}
	@Override
	public boolean yaAutorizo(Solicitud solicitud, Usuario usuario) {
		
		Integer autorizacionesPorUsuario = solicitudAutorizacionService.getCountAllByUsuarioAutorizo(solicitud.getIdSolicitud(), usuario.getIdUsuario());
		
		if(autorizacionesPorUsuario > 0){
			return true;
		}else{
			return false;
		}
		
	}
	
	@Override
	public boolean autorizadorEsSolicitanteCreador(Usuario autorizador, Solicitud sol) {
		return sol.getUsuarioByIdUsuario().getIdUsuario() == autorizador.getIdUsuario();
	}
	
}