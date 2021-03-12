package com.lowes.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lowes.dto.BusquedaSolicitudDTO;
import com.lowes.dto.ProveedorAsesorDTO;
import com.lowes.entity.Compania;
import com.lowes.entity.ComprobacionDeposito;
import com.lowes.entity.EstadoSolicitud;
import com.lowes.entity.Locacion;
import com.lowes.entity.Moneda;
import com.lowes.entity.Proveedor;
import com.lowes.entity.Solicitud;
import com.lowes.entity.SolicitudAutorizacion;
import com.lowes.entity.TipoSolicitud;
import com.lowes.entity.Usuario;
import com.lowes.service.BusquedaSolicitudService;
import com.lowes.service.ComprobacionDepositoService;
import com.lowes.service.EstadoSolicitudService;
import com.lowes.service.MonedaService;
import com.lowes.service.ParametroService;
import com.lowes.service.ProveedorService;
import com.lowes.service.SolicitudAutorizacionService;
import com.lowes.service.TipoSolicitudService;
import com.lowes.service.UsuarioService;
import com.lowes.util.Etiquetas;
import com.lowes.util.Utilerias;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class BusquedaSolicitudController {

	private static final Logger logger = Logger.getLogger(BusquedaSolicitudController.class);

	@Autowired
	private BusquedaSolicitudService busquedaSolicitudService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TipoSolicitudService tipoSolicitudService;

	@Autowired
	private EstadoSolicitudService estadoSolicitudService;

	@Autowired
	private MonedaService monedaService;
	
	@Autowired
	private ParametroService parametroService;
	
	@Autowired
	private SolicitudAutorizacionService solicitudAutorizacionService;

	@Autowired
	private ComprobacionDepositoService comprobacionDepositoService;
	
	@Autowired
	private ProveedorService proveedorService;
	
	public BusquedaSolicitudController() {
		logger.info("BusquedaSolicitudController()");
	}

	@RequestMapping("/busquedaSolicitud")
	public ModelAndView busquedaSolicitud(HttpSession session) {
		
		BusquedaSolicitudDTO filtros = new BusquedaSolicitudDTO();

		Usuario usuarioActual = usuarioService.getUsuarioSesion();
		Integer puestoAP = Integer.parseInt(parametroService.getParametroByName("puestoAutorizacionAP").getValor());
		Integer puestoConfirmacionAP = Integer.parseInt(parametroService.getParametroByName("puestoConfirmacionAP").getValor());
		Integer monedaPesos = Integer.parseInt(parametroService.getParametroByName("idPesos").getValor());
		Boolean fechaDefault = true;
		
		// enviarCombos
		List<Compania> companiaList = busquedaSolicitudService.getCompanias();
		
		List<TipoSolicitud> tipoSolicitudList = tipoSolicitudService.getAllTipoSolicitudOrder("descripcion");
		List<EstadoSolicitud> estadoSolicitudList = estadoSolicitudService.getAllEstadoSolicitudOrder("estadoSolicitud");
		List<Moneda> monedaList = monedaService.getAllMoneda();
		List<Locacion> locacionList = busquedaSolicitudService.getLocaciones();
		List<Usuario> solicitantesList = usuarioService.getUsuariosSolicitantes();
		List<Usuario> autorizadoresList = usuarioService.getUsuariosAutorizadores();

		List<Solicitud> solicitudesBusqueda = new ArrayList<>();
		
		// agregar listas al modelo
		HashMap<String, Object> modelo = new HashMap<>();
		modelo.put("busquedaSolicitudDTO", filtros);
		
		
		/*ID's de tipos de solicitud para logica de listado en la informacion de la busqueda.*/
		modelo.put("SOLICITUD_NO_MERCANCIAS_CON_XML", parametroService.getParametroByName("idTipoSolicitudNoMercanciasXML").getValor());
		modelo.put("SOLICITUD_NO_MERCANCIAS_SIN_XML", parametroService.getParametroByName("idTipoSolicitudNoMercanciasSinXML").getValor());
		modelo.put("SOLICITUD_REEMBOLSOS", parametroService.getParametroByName("idTipoSolicitudReembolsos").getValor());
		modelo.put("SOLICITUD_CAJA_CHICA", parametroService.getParametroByName("idTipoSolicitudCajaChica").getValor());
		modelo.put("SOLICITUD_KILOMETRAJE", parametroService.getParametroByName("idTipoSolicitudKilometraje").getValor());
		modelo.put("SOLICITUD_ANTICIPO", parametroService.getParametroByName("idTipoSolicitudAnticipo").getValor());
		modelo.put("SOLICITUD_COMPROBACION_ANTICIPO", parametroService.getParametroByName("idTipoSolicitudComprobacionAnticipo").getValor());
		modelo.put("SOLICITUD_ANTICIPO_GASTOS_VIAJE", parametroService.getParametroByName("idTipoSolicitudAnticipoGastosViaje").getValor());
		modelo.put("SOLICITUD_COMPROBACION_ANTICIPO_GASTOS_VIAJE", parametroService.getParametroByName("idTipoSolicitudComprobacionAnticipoViaje").getValor());
		
		/* set de datos para el form de la busqueda */
		modelo.put("companiaList", companiaList);
		modelo.put("tipoSolicitudList", tipoSolicitudList);
		modelo.put("estadoSolicitudList", estadoSolicitudList);
		modelo.put("monedaList", monedaList);
		modelo.put("locacionList", locacionList);
		modelo.put("solicitantesList", solicitantesList);
		modelo.put("autorizadoresList", autorizadoresList);
		modelo.put("solicitudesBusqueda", solicitudesBusqueda);
		modelo.put("usuarioActual", usuarioActual);
		modelo.put("puestoAP", puestoAP);
		modelo.put("puestoConfirmacionAP", puestoConfirmacionAP);
		modelo.put("monedaPesos", monedaPesos);
		modelo.put("tieneRegistros", "primera");
		modelo.put("numeroProveedorAsesor", "-1");
		modelo.put("fechaDefault", fechaDefault);
		if (session.getAttribute("firstSolicitud") == null) {
			modelo.put("first", true);
		}
		else {modelo.put("first", false);}
		if (session.getAttribute("filtrosSolicitud") != null) {
			modelo.put("filtros", session.getAttribute("filtrosSolicitud"));
		}
		
		// logger.info(solicitudesBusqueda);

		// enviar el modelo completo
		return new ModelAndView("busquedaSolicitud", modelo);
	}

	@RequestMapping(value = "busquedaSolicitudBuscar", method = RequestMethod.POST)
	public ModelAndView busquedaSolicitud(@ModelAttribute("BusquedaSolicitudDTO") BusquedaSolicitudDTO filtros, HttpServletRequest request, HttpSession session) {
		
		Usuario usuario = usuarioService.getUsuarioSesion();
		Integer puestoAP = Integer.parseInt(parametroService.getParametroByName("puestoAutorizacionAP").getValor());
		Integer puestoConfirmacionAP = Integer.parseInt(parametroService.getParametroByName("puestoConfirmacionAP").getValor());
		Boolean fechaDefault = false;
		session.setAttribute("filtrosSolicitud",filtros);
		session.setAttribute("firstSolicitud",false);
		
		// Realizar b�squeda
		List<Solicitud> sols = busquedaSolicitudService.getSolicitudesBusqueda(filtros, usuario, puestoAP, puestoConfirmacionAP);
		List<Solicitud> solicitudesBusqueda = new ArrayList<Solicitud>();
		List<ComprobacionDeposito> cds;
		boolean add = true;
		BigDecimal cero = new BigDecimal(0);
		ComprobacionDeposito cd;
		for(Solicitud s : sols){
			if(s.getUsuarioByIdUsuarioSolicita() != null && s.getUsuarioByIdUsuarioSolicita().getNumeroProveedor() != null){
				List<Proveedor> proveedores = proveedorService.getProveedorByNumero(s.getUsuarioByIdUsuarioSolicita().getNumeroProveedor());
				if(!proveedores.isEmpty())
					s.setProveedor(proveedores.get(0));
			}
			cds = comprobacionDepositoService.getAllComprobacionDepositoBySolicitud(s.getIdSolicitud());
			if(cds!=null && !cds.isEmpty()){
				cd = cds.get(0);
				s.setComprobacion(cd);
//				add = cd.getFechaDeposito().compareTo(filtros.getFechaInicialPagAnticipo())>=0 && cd.getFechaDeposito().compareTo(filtros.getFechaFinalPagAnticipo())<=0;
//				if(add && filtros.getCuentaConDeposito()){
					add = cd.getMontoDeposito().compareTo(cero)>0;
//				}
//			}
			if(add){
				solicitudesBusqueda.add(s);	
			}
			} else {
				solicitudesBusqueda.add(s);	
			}
		}
		// Se establece cuales son visibles y cuales no
		solicitudesBusqueda = actualizaPermisos(solicitudesBusqueda);
		
		// Verificar si tiene registros de Solicitudes
		Boolean tieneRegistros = false;
		if (solicitudesBusqueda != null && solicitudesBusqueda.size() > Etiquetas.CERO) {
			tieneRegistros = true;
		}
		
		// enviarCombos
		List<Compania> companiaList = busquedaSolicitudService.getCompanias();
		
		List<TipoSolicitud> tipoSolicitudList = tipoSolicitudService.getAllTipoSolicitudOrder("descripcion");
		List<EstadoSolicitud> estadoSolicitudList = estadoSolicitudService.getAllEstadoSolicitudOrder("estadoSolicitud");
		List<Moneda> monedaList = monedaService.getAllMoneda();
		List<Locacion> locacionList = busquedaSolicitudService.getLocaciones();
		List<Usuario> solicitantesList = usuarioService.getUsuariosSolicitantes();
		List<Usuario> autorizadoresList = usuarioService.getUsuariosAutorizadores();
		
		


		// agregar listas al modelo
		HashMap<String, Object> modelo = new HashMap<>();
		
		
		/*ID's de tipos de solicitud para logica de listado en la informacion de la busqueda.*/
		modelo.put("SOLICITUD_NO_MERCANCIAS_CON_XML", parametroService.getParametroByName("idTipoSolicitudNoMercanciasXML").getValor());
		modelo.put("SOLICITUD_NO_MERCANCIAS_SIN_XML", parametroService.getParametroByName("idTipoSolicitudNoMercanciasSinXML").getValor());
		modelo.put("SOLICITUD_REEMBOLSOS", parametroService.getParametroByName("idTipoSolicitudReembolsos").getValor());
		modelo.put("SOLICITUD_CAJA_CHICA", parametroService.getParametroByName("idTipoSolicitudCajaChica").getValor());
		modelo.put("SOLICITUD_KILOMETRAJE", parametroService.getParametroByName("idTipoSolicitudKilometraje").getValor());
		modelo.put("SOLICITUD_ANTICIPO", parametroService.getParametroByName("idTipoSolicitudAnticipo").getValor());
		modelo.put("SOLICITUD_COMPROBACION_ANTICIPO", parametroService.getParametroByName("idTipoSolicitudComprobacionAnticipo").getValor());
		modelo.put("SOLICITUD_ANTICIPO_GASTOS_VIAJE", parametroService.getParametroByName("idTipoSolicitudAnticipoGastosViaje").getValor());
		modelo.put("SOLICITUD_COMPROBACION_ANTICIPO_GASTOS_VIAJE", parametroService.getParametroByName("idTipoSolicitudComprobacionAnticipoViaje").getValor());
		
		modelo.put("busquedaSolicitudDTO", filtros);
		modelo.put("companiaList", companiaList);
		modelo.put("tipoSolicitudList", tipoSolicitudList);
		modelo.put("estadoSolicitudList", estadoSolicitudList);
		modelo.put("monedaList", monedaList);
		modelo.put("locacionList", locacionList);
		modelo.put("solicitantesList", solicitantesList);
		modelo.put("autorizadoresList", autorizadoresList);
		modelo.put("solicitudesBusqueda", solicitudesBusqueda);
		modelo.put("usuarioActual", usuario);
		modelo.put("puestoAP", puestoAP);
		modelo.put("puestoConfirmacionAP", puestoConfirmacionAP);
		modelo.put("monedaPesos",filtros.getIdMonedaFiltro());
		modelo.put("tieneRegistros", tieneRegistros);
		modelo.put("fechaDefault", fechaDefault);
		modelo.put("solicitante", usuario.getIdUsuario());
		modelo.put("numeroProveedorAsesor", filtros.getIdProveedorFiltro());
		modelo.put("first", false);
		if (session.getAttribute("filtrosSolicitud") != null) {
			modelo.put("filtros", session.getAttribute("filtrosSolicitud"));
		}
		// logger.info(solicitudesBusqueda);

		// enviar el modelo completo
		return new ModelAndView("busquedaSolicitud", modelo);
	}

	private List<Solicitud> actualizaPermisos(List<Solicitud> solicitudesBusqueda) {
		Integer puestoAP = Integer.parseInt(parametroService.getParametroByName("puestoAutorizacionAP").getValor());
		Integer puestoConfirmacionAP = Integer.parseInt(parametroService.getParametroByName("puestoConfirmacionAP").getValor());
		for(Solicitud solicitud : solicitudesBusqueda){
			List<SolicitudAutorizacion> autorizadores = solicitudAutorizacionService.getAllSolicitudAutorizacionBySolicitud(solicitud.getIdSolicitud());
  		    // resolver el acceso a la solicitud actual.
  		    Integer acceso = Utilerias.validaAcceso(solicitud, solicitud.getTipoSolicitud().getIdTipoSolicitud(),usuarioService.getUsuarioSesion(), autorizadores, puestoAP, puestoConfirmacionAP);
  		    if(acceso == Etiquetas.NO_VISUALIZAR){
  			    solicitud.setUrlVisible(false);
  		    }else if(acceso == Etiquetas.VISUALIZAR || acceso == Etiquetas.VISUALIZAR_Y_EDITAR){
  		    	solicitud.setUrlVisible(true);
  		    }
		}
		return solicitudesBusqueda;
	}
	
	
	private List<ProveedorAsesorDTO> loadProveedorList(List<Proveedor> proveedores ){
		List<ProveedorAsesorDTO> listaActual = new ArrayList<ProveedorAsesorDTO>();
		for(Proveedor provedor : proveedores){
    		ProveedorAsesorDTO proveedorAsesor = new ProveedorAsesorDTO();
    		proveedorAsesor.setDescripcion(provedor.getDescripcion());
    		proveedorAsesor.setDescripcionCompuesta(provedor.getNumeroDescripcionProveedor());
    		proveedorAsesor.setIdProveedorAsesor(provedor.getIdProveedor());
    		proveedorAsesor.setNumeroIdentificador(provedor.getNumeroProveedor());
    		proveedorAsesor.setNumeroDescripcionProveedorAsesor(provedor.getNumeroDescripcionProveedor());
    		proveedorAsesor.setTipo(" (Proveedor)");
    		listaActual.add(proveedorAsesor);
    	}
		return listaActual;
	}
    
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    
    
	@RequestMapping(value = "/getProveedoresBusquedaSolicitud", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody ResponseEntity<String> getRFC(HttpSession session,	HttpServletRequest request, HttpServletResponse response, Integer idProveedorAsesor) {
		String json = null;
		List<Proveedor> proveedorList = new ArrayList<>();
		String muestraTodosProveedoresBusqueda = parametroService.getParametroByName("muestraTodosProveedoresBusqueda").getValor();
		List<ProveedorAsesorDTO> proveedorAsesorList = new ArrayList<ProveedorAsesorDTO>();
		if (Integer.parseInt(muestraTodosProveedoresBusqueda) == Etiquetas.UNO) {
			proveedorList = busquedaSolicitudService.getProveedores();
		} else {
			proveedorList = busquedaSolicitudService.getProveedoresTodos();
		}
		proveedorAsesorList.addAll(loadProveedorList(proveedorList));
		
		ArrayList<String[]> lista = new ArrayList<String[]>();
		for (ProveedorAsesorDTO proveedorAsesor : proveedorAsesorList) {
			String[] record = new String[]{proveedorAsesor.getNumeroDescripcionProveedorAsesor(), proveedorAsesor.getNumeroIdentificador()+""};
			lista.add(record);
		}
		Integer caracteres = Integer.parseInt(parametroService.getParametroByName("autocomplete.proveedores.caracteres").getValor());
		Integer lineas = Integer.parseInt(parametroService.getParametroByName("autocomplete.proveedores.lineas").getValor());
		
		Map<String, Object> datos = new HashMap<String, Object>();
		datos.put("lineas", lineas);
		datos.put("caracteres", caracteres);
		datos.put("lista", lista);

		ObjectMapper map = new ObjectMapper();
		try {
			json = map.writeValueAsString(datos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
	}
}

