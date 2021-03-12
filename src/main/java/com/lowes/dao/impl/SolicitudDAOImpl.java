package com.lowes.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lowes.dao.SolicitudDAO;
import com.lowes.entity.EstadoSolicitud;
import com.lowes.entity.Solicitud;
import com.lowes.entity.SolicitudAutorizacion;
import com.lowes.service.ParametroService;
import com.lowes.util.Etiquetas;
import com.lowes.util.HibernateUtil;


@Repository
public class SolicitudDAOImpl implements SolicitudDAO {
	
	@Autowired
	private HibernateUtil hibernateUtil;
	
	@Autowired
	private ParametroService parametroService;

	@Override
	public Integer createSolicitud(Solicitud solicitud) {
		return  (Integer) hibernateUtil.create(solicitud);
	}

	@Override
	public Solicitud updateSolicitud(Solicitud solicitud) {
		return hibernateUtil.update(solicitud);
	}

	@Override
	public void deleteSolicitud(Integer idSolicitud) {
		Solicitud solicitud = new Solicitud();
		solicitud = hibernateUtil.fetchById(idSolicitud, Solicitud.class);
		hibernateUtil.delete(solicitud);
	}

	@Override
	public List<Solicitud> getAllSolicitud() {
		return hibernateUtil.fetchAll(Solicitud.class);
	}

	@Override
	public Solicitud getSolicitud(Integer idSolicitud) {
		return hibernateUtil.fetchById(idSolicitud, Solicitud.class);
	}

	@Override
	public Long getSolicitudCountByUsuarioEstatus(Integer idUsuario, Integer idEstadoSolicitud) {
			String queryString = "SELECT COUNT(1) FROM " + Solicitud.class.getName()
					+ " WHERE ID_USUARIO_SOLICITA = :idUsuario"
					+ " AND ID_ESTADO_SOLICITUD = :idEstadoSolicitud";
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("idUsuario", idUsuario.toString());
			parameters.put("idEstadoSolicitud", idEstadoSolicitud.toString());
			
			List<Long> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
			
			return listSolicitudes.get(0);
	}
	
	@Override
	public Long getSolicitudCountByUsuarioEstatusCajaChica(Integer idUsuario, Integer idEstadoSolicitud,  Integer idTipoSolCajaChica, Integer idTipoSolconXML, Integer idTipoSolSinXML, Integer idTipoSolReembolsos) {
		String queryString = "SELECT COUNT(1) FROM " + Solicitud.class.getName()
				+ " WHERE CREACION_USUARIO = :idUsuario"
				+ " AND ID_ESTADO_SOLICITUD = :idEstadoSolicitud"
		        + " AND (ID_TIPO_SOLICITUD = :cajaChica"
                + " OR ID_TIPO_SOLICITUD = :noMercanciasConXML"
                + " OR ID_TIPO_SOLICITUD = :noMercanciasSinXML"
                + " OR ID_TIPO_SOLICITUD = :reembolsos)"
                + " AND CREACION_USUARIO != ID_USUARIO_SOLICITA)";

		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idUsuario", idUsuario.toString());
		parameters.put("idEstadoSolicitud", idEstadoSolicitud.toString());
		parameters.put("cajaChica", idTipoSolCajaChica.toString());
		parameters.put("noMercanciasConXML", idTipoSolconXML.toString());
		parameters.put("noMercanciasSinXML", idTipoSolSinXML.toString());
		parameters.put("reembolsos", idTipoSolReembolsos.toString());
		
		List<Long> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		
		return listSolicitudes.get(0);
	}
	
	@Override
	public Long getSolicitudCountByUsuarioEstatusDoble(Integer idUsuario, Integer idEstadoSolicitud1, Integer idEstadoSolicitud2) {
			String queryString = "SELECT COUNT(1) FROM " + Solicitud.class.getName()
					+ " WHERE ID_USUARIO_SOLICITA = :idUsuario"
					+ " AND (ID_ESTADO_SOLICITUD = :idEstadoSolicitud1"
					+ " OR ID_ESTADO_SOLICITUD = :idEstadoSolicitud2)";
			
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("idUsuario", idUsuario.toString());
			parameters.put("idEstadoSolicitud1", idEstadoSolicitud1.toString());
			parameters.put("idEstadoSolicitud2", idEstadoSolicitud2.toString());
			
			List<Long> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
			
			return listSolicitudes.get(0);
	}
	
	@Override
	public Long getSolicitudCountByUsuarioEstatusDobleCajaChica(Integer idUsuario, Integer idEstadoSolicitud1,
			Integer idEstadoSolicitud2) {
		
		String queryString = "SELECT COUNT(1) FROM " + Solicitud.class.getName()
				+ " WHERE CREACION_USUARIO = :idUsuario"
				+ " AND (ID_ESTADO_SOLICITUD = :idEstadoSolicitud1"
				+ " OR ID_ESTADO_SOLICITUD = :idEstadoSolicitud2)"
		        + " AND (ID_TIPO_SOLICITUD = :cajaChica "
                + " OR ID_TIPO_SOLICITUD   = :noMercanciasConXML  "
                + " OR ID_TIPO_SOLICITUD   = :noMercanciasSinXML "
                + " OR ID_TIPO_SOLICITUD   = :reembolsos) "
                + " AND (CREACION_USUARIO != ID_USUARIO_SOLICITA) ";

		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idUsuario", idUsuario.toString());
		parameters.put("idEstadoSolicitud1", idEstadoSolicitud1.toString());
		parameters.put("idEstadoSolicitud2", idEstadoSolicitud2.toString());
		parameters.put("cajaChica", parametroService.getParametroByName("idTipoSolicitudCajaChica").getValor());
		parameters.put("noMercanciasConXML", parametroService.getParametroByName("idTipoSolicitudNoMercanciasXML").getValor());
		parameters.put("noMercanciasSinXML", parametroService.getParametroByName("idTipoSolicitudNoMercanciasSinXML").getValor());
		parameters.put("reembolsos", parametroService.getParametroByName("idTipoSolicitudReembolsos").getValor());
		
		List<Long> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		
		return listSolicitudes.get(0);
		
	}

	@Override
	public List<Solicitud> getAllSolicitudByStatus(Integer idEstadoSolicitud) {
		String queryString = "FROM " + Solicitud.class.getName()
				+ " WHERE ID_ESTADO_SOLICITUD = :idEstadoSolicitud AND (ENVIADO_CM IS NULL OR ENVIADO_CM = 0)";
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEstadoSolicitud", idEstadoSolicitud.toString());
		
		List<Solicitud> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		return listSolicitudes;
	}

	@Override
	public List<Solicitud> getAnticiposPendientes(Integer idUsuario, Integer idProveedor, Integer idEstadoSolicitud) {
		String queryString = "FROM " + Solicitud.class.getName()
				+ " WHERE ID_USUARIO_SOLICITA = :idUsuario AND ID_PROVEEDOR = :idProveedor AND ID_ESTADO_SOLICITUD = :idEstadoSolicitud AND COMPROBADA = 0";
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idUsuario", String.valueOf(idUsuario));
		parameters.put("idProveedor", String.valueOf(idProveedor));
		parameters.put("idEstadoSolicitud", String.valueOf(idEstadoSolicitud));
		
		List<Solicitud> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		return listSolicitudes;
	}

	@Override
	public Long getSolicitudCountByAnticipoPentiente(Integer idUsuario, Integer idTipoSolicitud,
			Integer idEstadoSolicitud) {
		String queryString = "SELECT COUNT(1) FROM " + Solicitud.class.getName()
				+ " WHERE CREACION_USUARIO = :idUsuario"
				+ " AND ID_TIPO_SOLICITUD = :idTipoSolicitud"
				+ " AND ID_ESTADO_SOLICITUD = :idEstadoSolicitud"
		        + " AND (COMPROBADA = 0 OR COMPROBADA IS NULL)";
//                + " AND (CREACION_USUARIO != ID_USUARIO_SOLICITA)";
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idUsuario", idUsuario.toString());
		parameters.put("idTipoSolicitud", idTipoSolicitud.toString());
		parameters.put("idEstadoSolicitud", idEstadoSolicitud.toString());
		
		List<Long> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		
		return listSolicitudes.get(0);
	}

	@Override
	public List<Solicitud> getAnticiposMultiplesByEstatus(Integer idUsuario, Integer idProveedor, Integer idEstadoSolicitudPagada, Integer idEstadoSolicitudMultiple) {
		String queryString = "FROM " + Solicitud.class.getName() + " WHERE ID_USUARIO_SOLICITA = :idUsuario AND ID_PROVEEDOR = :idProveedor AND (ID_ESTADO_SOLICITUD = :idEstadoSolicitud ";
		if (idEstadoSolicitudMultiple != null) {
			queryString +=  " OR ID_ESTADO_SOLICITUD = :idEstadoSolicitudMultiple ";
		}
		queryString +=  ") AND COMPROBADA = 0";

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idUsuario", String.valueOf(idUsuario));
		parameters.put("idProveedor", String.valueOf(idProveedor));
		parameters.put("idEstadoSolicitud", String.valueOf(idEstadoSolicitudPagada));
		if (idEstadoSolicitudMultiple != null) {
			parameters.put("idEstadoSolicitudMultiple", String.valueOf(idEstadoSolicitudMultiple));
		}
		System.out.println("queryString :" +queryString);
		List<Solicitud> listSolicitudes = hibernateUtil.fetchAllHql(queryString.toString(), parameters);
		return listSolicitudes;
	}

	@Override
	public List<Solicitud> getAnticiposMultiplesByEstatusAComprobar(Integer idUsuario, Integer idProveedor, Integer idEstadoSolicitudPagada, Integer idEstadoSolicitudMultiple, Integer idSolicitud) {
		String queryString = "SELECT * FROM SOLICITUD WHERE ID_USUARIO_SOLICITA = "+idUsuario+" AND ID_PROVEEDOR = "+idProveedor+" AND (ID_ESTADO_SOLICITUD = "+idEstadoSolicitudPagada;
		if (idEstadoSolicitudMultiple != null) {
			queryString +=  " OR ID_ESTADO_SOLICITUD = "+idEstadoSolicitudMultiple;
		}
		queryString +=  ") AND COMPROBADA = 0 AND ID_SOLICITUD NOT IN ("
				+ "SELECT ID_SOLICITUD_MULTIPLE FROM COMPROBACION_ANTICIPO_MULTIPLE WHERE ID_SOLICITUD NOT IN ("
				+ "	SELECT "+idSolicitud+" FROM SYSIBM.SYSDUMMY1 "
				+ "UNION SELECT ID_SOLICITUD_ANTICIPO FROM COMPROBACION_ANTICIPO WHERE ID_SOLICITUD_COMPROBACION = "+idSolicitud+" "
				+ "UNION SELECT ID_SOLICITUD_COMPROBACION FROM COMPROBACION_ANTICIPO WHERE ID_SOLICITUD_ANTICIPO = "+idSolicitud+")) ";
		System.out.println("queryString :" +queryString);
		List<Solicitud> listSolicitudes = hibernateUtil.fetchAll(queryString.toString(), Solicitud.class);
		return listSolicitudes;
	}

	@Override
	public List<Solicitud> getAnticiposPendientesPorComprobar(Integer idUsuario, Integer idProveedor, Integer idEstadoCreado, Integer idEstadoCancelado, Integer idEstadoComprobado) {
		String queryString = "FROM " + Solicitud.class.getName()
				+ " WHERE ID_USUARIO_SOLICITA = :idUsuario AND ID_PROVEEDOR = :idProveedor AND ID_ESTADO_SOLICITUD <> :idEstadoCreado AND ID_ESTADO_SOLICITUD <> :idEstadoCancelado AND ID_ESTADO_SOLICITUD <> :idEstadoComprobado";// AND COMPROBADA = 0
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idUsuario", String.valueOf(idUsuario));
		parameters.put("idProveedor", String.valueOf(idProveedor));
		parameters.put("idEstadoCreado", String.valueOf(idEstadoCreado));
		parameters.put("idEstadoCancelado", String.valueOf(idEstadoCancelado));
		parameters.put("idEstadoComprobado", String.valueOf(idEstadoComprobado));
		
		List<Solicitud> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		return listSolicitudes;
	}
	@Override
	public List<Solicitud> getSolicitudesByProveedor(Integer idProveedor) {
		String queryString = "FROM " + Solicitud.class.getName()
				+ " WHERE ID_PROVEEDOR = :idProveedor";
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idProveedor", String.valueOf(idProveedor));
		
		List<Solicitud> listSolicitudes = hibernateUtil.fetchAllHql(queryString, parameters);
		return listSolicitudes;
	}

	@Override
	public void updateEstadoSolicitud(EstadoSolicitud estado, Integer idSolicitud) {
		
		String queryString = "UPDATE " +Solicitud.class.getName() + " SET ID_ESTADO_SOLICITUD = :idEstadoSolicitud"
				+ "	WHERE ID_SOLICITUD = :idSolicitud";
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEstadoSolicitud", String.valueOf(estado.getIdEstadoSolicitud()));
		parameters.put("idSolicitud", String.valueOf(idSolicitud));
		
		hibernateUtil.fetchDeleteAndUpdateQuerys(queryString, parameters);
		
	}
}
