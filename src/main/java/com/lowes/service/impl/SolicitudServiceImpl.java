package com.lowes.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowes.dao.SolicitudDAO;
import com.lowes.entity.EstadoSolicitud;
import com.lowes.entity.Solicitud;
import com.lowes.service.SolicitudService;

@Service
@Transactional
public class SolicitudServiceImpl implements SolicitudService {

	@Autowired
	private SolicitudDAO solicitudDAO;
		
	@Override
	public Integer createSolicitud(Solicitud solicitud) {
		return solicitudDAO.createSolicitud(solicitud);
	}

	@Override
	public Solicitud updateSolicitud(Solicitud solicitud) {
		return solicitudDAO.updateSolicitud(solicitud);
	}

	@Override
	public void deleteSolicitud(Integer idSolicitud) {
		solicitudDAO.deleteSolicitud(idSolicitud);
	}

	@Override
	public List<Solicitud> getAllSolicitud() {
		return solicitudDAO.getAllSolicitud();
	}

	@Override
	public Solicitud getSolicitud(Integer idSolicitud) {
		return solicitudDAO.getSolicitud(idSolicitud);
	}

	@Override
	public Long getSolicitudCountByUsuarioEstatus(Integer idUsuario, Integer idEstadoSolicitud) {
		return solicitudDAO.getSolicitudCountByUsuarioEstatus(idUsuario, idEstadoSolicitud);
	}
	
	@Override
	public Long getSolicitudCountByUsuarioEstatusCajaChica(Integer idUsuario, Integer idEstadoSolicitud, Integer idTipoSolCajaChica, Integer idTipoSolconXML, Integer idTipoSolSinXML,Integer idTipoSolReembolsos) {
		return solicitudDAO.getSolicitudCountByUsuarioEstatusCajaChica(idUsuario, idEstadoSolicitud, idTipoSolCajaChica, idTipoSolconXML, idTipoSolSinXML , idTipoSolReembolsos);
	}

	@Override
	public List<Solicitud> getAllSolicitudByStatus(Integer idEstadoSolicitud) {
		return solicitudDAO.getAllSolicitudByStatus(idEstadoSolicitud);
	}

	@Override
	public List<Solicitud> getAnticiposPendientes(Integer idUsuario, Integer idProveedor, Integer idEstadoSolicitud) {
		return solicitudDAO.getAnticiposPendientes(idUsuario, idProveedor, idEstadoSolicitud);
	}
	
	@Override
	public Long getSolicitudCountByUsuarioEstatusDoble(Integer idUsuario, Integer idEstadoSolicitud1, Integer idEstadoSolicitud2) {
		return solicitudDAO.getSolicitudCountByUsuarioEstatusDoble(idUsuario, idEstadoSolicitud1, idEstadoSolicitud2);
	}

	@Override
	public Long getSolicitudCountByUsuarioEstatusDobleCajaChica(Integer idUsuario, Integer idEstadoSolicitud1,
			Integer idEstadoSolicitud2) {
		return solicitudDAO.getSolicitudCountByUsuarioEstatusDobleCajaChica(idUsuario, idEstadoSolicitud1, idEstadoSolicitud2);
	}

	@Override
	public Long getSolicitudCountByAnticipoPentiente(Integer idUsuario, Integer idTipoSolicitud,
			Integer idEstadoSolicitud) {
		return solicitudDAO.getSolicitudCountByAnticipoPentiente(idUsuario, idTipoSolicitud, idEstadoSolicitud);
	}
	
	@Override
	public List<Solicitud> getAnticiposMultiplesByEstatus(Integer idUsuario, Integer idProveedor, Integer idEstadoSolicitudPagada, Integer idEstadoSolicitudMultiple) {
		return solicitudDAO.getAnticiposMultiplesByEstatus(idUsuario, idProveedor, idEstadoSolicitudPagada, idEstadoSolicitudMultiple);
	}

	@Override
	public List<Solicitud> getAnticiposMultiplesByEstatusAComprobar(Integer idUsuario, Integer idProveedor, Integer idEstadoSolicitudPagada, Integer idEstadoSolicitudMultiple, Integer idSolicitud) {
		return solicitudDAO.getAnticiposMultiplesByEstatusAComprobar(idUsuario, idProveedor, idEstadoSolicitudPagada, idEstadoSolicitudMultiple, idSolicitud);
	}

	@Override
	public List<Solicitud> getAnticiposPendientesPorComprobar(Integer idUsuario, Integer idProveedor, Integer idEstadoCreado, Integer idEstadoCancelado, Integer idEstadoComprobado) {
		return solicitudDAO.getAnticiposPendientesPorComprobar(idUsuario, idProveedor, idEstadoCreado, idEstadoCancelado, idEstadoComprobado);
	}

	@Override
	public List<Solicitud> getSolicitudesByProveedor(Integer idProveedor) {
		return solicitudDAO.getSolicitudesByProveedor(idProveedor);
	}

	@Override
	public void updateEstadoSolicitud(EstadoSolicitud estado, Integer idSolicitud) {
		solicitudDAO.updateEstadoSolicitud(estado, idSolicitud);
	}
}
